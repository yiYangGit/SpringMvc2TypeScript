package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.FastCodeGenerator;
import com.blueveery.springrest2ts.Rest2tsGenerator2;
import com.blueveery.springrest2ts.extensions.RestConversionExtension;
import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.spring.HandlerMethod2;
import com.blueveery.springrest2ts.spring.SpringRestMethodFilter;
import com.blueveery.springrest2ts.tsmodel.*;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
import com.blueveery.springrest2ts.tsmodel.generics.TSInterfaceReference;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public abstract class SpringAnnotationsBasedRestClassConverter extends RestClassConverter {
    protected SpringAnnotationsBasedRestClassConverter(ImplementationGenerator implementationGenerator) {
        super(implementationGenerator);
    }

    public SpringAnnotationsBasedRestClassConverter(ImplementationGenerator implementationGenerator, ClassNameMapper classNameMapper) {
        super(implementationGenerator, classNameMapper);
    }

    @Override
    public boolean preConverted(JavaPackageToTsModuleConverter javaPackageToTsModuleConverter, Class javaClass){
        if(TypeMapper.map(javaClass) == TypeMapper.tsAny && !javaClass.isInterface()){
            TSModule tsModule = javaPackageToTsModuleConverter.getTsModule(javaClass);
            String tsClassName = createTsClassName(javaClass);
            TSClass tsClass = new TSClass(tsClassName, tsModule, implementationGenerator);
            tsModule.addScopedElement(tsClass);
            TypeMapper.registerTsType(javaClass, tsClass);
            return true;
        }
        return false;
    }

    @Override
    public void convertInheritance(Class javaClass) {
        TSClassReference tsClassReference = (TSClassReference) TypeMapper.map(javaClass);
        TSClass tsClass = tsClassReference.getReferencedType();
        setSupperClass(javaClass, tsClass);
        setSuperInterfaces(javaClass, tsClass);
    }

    private void setSuperInterfaces(Class javaClass, TSClass tsClass) {
        HashSet<Class> restInterfaces = new HashSet<>();
        HashSet<ParameterizedType> parameterizedTypes = new HashSet<>();
        for (Type typeInterface : javaClass.getGenericInterfaces()) {
            if (typeInterface instanceof Class) {
                Class interfaceClass = (Class) typeInterface;
                if (isRestInterface(interfaceClass)) {
                    restInterfaces.add(interfaceClass);
                }
            } else if (typeInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedTypeInterface = (ParameterizedType) typeInterface;
                Type rawType = parameterizedTypeInterface.getRawType();
                if (rawType instanceof Class) {
                    if (isRestInterface((Class) rawType)) {
                        parameterizedTypes.add(parameterizedTypeInterface);
                    }
                }
            }
        }
        for (Class aClass : restInterfaces) {
            TSType superClass = TypeMapper.map(aClass);
            if (superClass instanceof TSInterfaceReference) {
                TSInterfaceReference tsSuperClassInterface = (TSInterfaceReference) superClass;
                tsClass.addImplementsInterfaces(tsSuperClassInterface);
            }
        }
        for (ParameterizedType parameterizedType : parameterizedTypes) {
            Type rawType = parameterizedType.getRawType();
            TSType tsInterface = TypeMapper.map(rawType);
            if (tsInterface instanceof TSInterfaceReference) {
                TSInterfaceReference tsSuperClassInterface = (TSInterfaceReference) tsInterface;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                List<TSType> tsTypes = Arrays.stream(actualTypeArguments).map(type -> {
                    return TypeMapper.map(type);
                }).collect(Collectors.toList());
                tsClass.addImplementsInterfacesWithActualType(tsSuperClassInterface, tsTypes);
            }
        }
    }

    private boolean isRestInterface(Class interfaceClass) {
        if (interfaceClass.getAnnotation(Controller.class) != null
                || interfaceClass.getAnnotation(RestController.class) != null) {
            return true;
        }
        return false;
    }


    public List<TSField> getReadOnlyStaticFieldConverter(Class javaClass, TSClass tsClass) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void convert(Class javaClass, NullableTypesStrategy nullableTypesStrategy) {
        TSClassReference tsClassReference = (TSClassReference) TypeMapper.map(javaClass);
        TSClass tsClass = tsClassReference.getReferencedType();
        convertFormalTypeParameters(javaClass.getTypeParameters(), tsClassReference);
        addClassAnnotations(javaClass, tsClass);
        List<TSField> genStaticField = getReadOnlyStaticFieldConverter(javaClass, tsClass);
        for (TSField tsField : genStaticField) {
            tsClass.addTsField(tsField);
        }
        TSMethod tsConstructorMethod = new TSMethod("constructor", tsClass, null, implementationGenerator, false, true);
        tsClass.addTsMethod(tsConstructorMethod);
        List<Method> restMethodList = filterRestMethods(javaClass);
        Map<Method, HandlerMethod2> methodHandlerMethod2Map = SpringRestMethodFilter.detectHandlerMethod2s(javaClass);
        Set<String> unionMethodName = new HashSet<>();
        for (Method method : restMethodList) {
            String methodName = method.getName();
            if (unionMethodName.contains(methodName)) {
                throw new IllegalStateException("生成的ts代码不允许有重复的方法名称 重复的方法 :" + method.getDeclaringClass().getName() + ":" + methodName);
            }
            unionMethodName.add(methodName);
        }
        Method[] sortRestMethodList = restMethodList.toArray(new Method[0]);
        RestInterfaceConverter.sortMethods(sortRestMethodList);
        for (Method method: sortRestMethodList) {

            Map<TypeVariable, Type> variableToJavaType = new HashMap<>();
            //todo 遍历所有的api方法
            Class<?> declaringClass = method.getDeclaringClass();
            if (declaringClass != javaClass && (!method.getDeclaringClass().isInterface())) {
                //不是本类中的rest接口又不是接口中的rest接口不生成ts代码
                continue;
            }
            boolean methodMustBeInlined = declaringClass != javaClass && method.getDeclaringClass().isInterface();
            if(methodMustBeInlined){
                variableToJavaType = mapVariableToJavaType(javaClass, declaringClass, new HashMap<>());
            }

            Type genericReturnType = methodHandlerMethod2Map.get(method).getReturnType().getGenericParameterType();
            String methodName = method.getName();
            TSType methodReturnType = TypeMapper.map(genericReturnType, variableToJavaType);
            //todo 返回类型自动加到model类中
            tsClass.getModule().scopedTypeUsage(methodReturnType);
            TSMethod tsMethod = new TSMethod(methodName, tsClass, methodReturnType, implementationGenerator, false, false);
            HandlerMethod2 handlerMethod2 = methodHandlerMethod2Map.get(method);
            addMethodAnnotations(method, tsMethod, handlerMethod2);
            Parameter[] parameters = method.getParameters();
            MethodParameter[] methodHandlerParameters = handlerMethod2.getMethodParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Type parameterizedType = methodHandlerMethod2Map.get(method).getMethodParameters()[i].getGenericParameterType();
                TSType tsType = TypeMapper.map(parameterizedType, variableToJavaType);
                TSParameter tsParameter = new TSParameter(parameter.getName(), tsType, tsMethod, implementationGenerator);
                addParameterAnnotations(parameter, tsParameter,methodHandlerParameters[i]);
                if (parameterIsMapped(tsParameter)) {
                    tsClass.getModule().scopedTypeUsage(tsParameter.getType());
                    setOptional(tsParameter);
                    nullableTypesStrategy.setAsNullableType(parameterizedType, parameter.getDeclaredAnnotations(), tsParameter);
                    tsMethod.getParameterList().add(tsParameter);
                }
                conversionListener.tsParameterCreated(parameter, tsParameter);
            }
            tsClass.addTsMethod(tsMethod);
            conversionListener.tsMethodCreated(method, tsMethod);
        }

        implementationGenerator.addComplexTypeUsage(tsClass);
        implementationGenerator.getSerializationExtension().addComplexTypeUsage(tsClass);
        conversionListener.tsScopedTypeCreated(javaClass, tsClass);
    }

    private void addParameterAnnotations(Parameter parameter, TSParameter tsParameter, MethodParameter methodHandlerParameter) {
        Annotation[] parameterAnnotations = methodHandlerParameter.getParameterAnnotations();
        tsParameter.addAllAnnotations(parameterAnnotations);
    }

    /**
     * 查找springmvc中的注解
     * @param method
     * @param tsMethod
     * @param handlerMethod2
     */
    private void addMethodAnnotations(Method method, TSMethod tsMethod, HandlerMethod2 handlerMethod2) {
        addAnnotationIfExit(tsMethod, handlerMethod2, RequestMapping.class);
        addAnnotationIfExit(tsMethod, handlerMethod2, ResponseBody.class);
        addAnnotationIfExit(tsMethod, handlerMethod2, PutMapping.class);
        addAnnotationIfExit(tsMethod, handlerMethod2, GetMapping.class);
        addAnnotationIfExit(tsMethod, handlerMethod2, PostMapping.class);
        addAnnotationIfExit(tsMethod, handlerMethod2, PatchMapping.class);
        addAnnotationIfExit(tsMethod, handlerMethod2, ResponseStatus.class);
        addAnnotationIfExit(tsMethod, handlerMethod2, DeleteMapping.class);
    }

    private void addAnnotationIfExit(TSMethod tsMethod, HandlerMethod2 handlerMethod2,Class clazz) {
        Annotation requestMapping = handlerMethod2.getMethodAnnotation(clazz);
        if (requestMapping != null) {
            tsMethod.addAnnotation(requestMapping);
        }
    }

    protected abstract void addClassAnnotations(Class javaClass, TSClass tsClass);

    protected abstract void addMethodAnnotations(Method method, TSMethod tsMethod);

    protected abstract void addParameterAnnotations(Parameter parameter, TSParameter tsParameter);

    protected abstract Type handleImplementationSpecificReturnTypes(Method method);

    private Map<TypeVariable, Type> mapVariableToJavaType(Class javaClass, Class<?> declaringClass, Map<TypeVariable, Type> typeParametersMap) {
        for (Type type:javaClass.getGenericInterfaces()){
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if (parameterizedType.getRawType() == declaringClass) {
                    for (int i = 0; i < parameterizedType.getActualTypeArguments().length; i++) {
                        TypeVariable typeParameter = declaringClass.getTypeParameters()[i];
                        Type actualTypeArgument = parameterizedType.getActualTypeArguments()[i];
                        typeParametersMap.put(typeParameter, actualTypeArgument);
                    }
                    return typeParametersMap;
                }
                mapVariableToJavaType(type.getClass(), declaringClass,typeParametersMap);
            }
        }
        return typeParametersMap;
    }

    public static List<Method> filterRestMethods(Class javaClass) {
        return new ArrayList<>(SpringRestMethodFilter.filterRestMethods(javaClass));
    }


    protected abstract RequestMapping getRequestMappingForMethod(Method method);

    private void appendHttpMethodToMethodName(Map<Method, StringBuilder> overloadedMethodNamesMap, Map<Method, RequestMapping> methodsRequestMappingsMap) {
        Map<Method, String> httpMethodsValuesMap = new HashMap<>();

        for (Map.Entry<Method, RequestMapping> entry : methodsRequestMappingsMap.entrySet()) {
            Method key = entry.getKey();
            RequestMapping value = entry.getValue();
            SortedSet<String> httpMethodsSet = new TreeSet<>();
            for (RequestMethod requestMethod : value.method()) {
                httpMethodsSet.add(requestMethod.name());
            }
            httpMethodsValuesMap.put(key, String.join("_", httpMethodsSet));
        }

        Set<String> allDifferentHttpMethods = new HashSet<>(httpMethodsValuesMap.values());
        if (allDifferentHttpMethods.size() <= 1) {
            return;
        }
        overloadedMethodNamesMap.forEach((method, methodName) -> methodName.append(httpMethodsValuesMap.get(method)));
    }

    private void appendHttpPathToMethodName(Map<Method, StringBuilder> overloadedMethodNamesMap, Map<Method, RequestMapping> methodsRequestMappingsMap) {
        Map<Method, String[]> methodsHttpPaths = new HashMap<>();
        for (Map.Entry<Method, RequestMapping> entry : methodsRequestMappingsMap.entrySet()) {
            Method method = entry.getKey();
            RequestMapping requestMapping = entry.getValue();
            if (requestMapping.path().length > 0) {
                methodsHttpPaths.put(method, requestMapping.path()[0].split("/"));
            }else{
                methodsHttpPaths.put(method, new String[]{""});
            }
        }

        int startIndex = findPathCommonPrefixIndex(methodsHttpPaths);

        for (Method method : overloadedMethodNamesMap.keySet()) {
            StringBuilder methodName = overloadedMethodNamesMap.get(method);
            String[] pathComponents = methodsHttpPaths.get(method);
            for (int i = startIndex; i < pathComponents.length; i++) {
                String pathComponent = pathComponents[i];
                if(!pathComponent.contains("{") && !"".equals(pathComponent)) {
                    methodName.append("_");
                    methodName.append(pathComponent.toUpperCase().replace("-", "_"));
                }
            }
        }

    }

    static int findPathCommonPrefixIndex(Map<Method, String[]> methodsHttpPaths) {
        return 0;
    }

    private boolean methodNamesAreUnique(Map<Method, StringBuilder> overloadedMethodNamesMap) {
        Set<String> methodNames = new HashSet<>();
        overloadedMethodNamesMap.forEach((k, v) -> methodNames.add(v.toString()));
        return overloadedMethodNamesMap.size() == methodNames.size();
    }

    public static void setOptional(TSParameter tsParameter) {
        for (Annotation annotation : tsParameter.getAnnotationList()) {
            if(annotation instanceof PathVariable){
                PathVariable pathVariable = (PathVariable) annotation;
                tsParameter.setOptional(!pathVariable.required());
                return;
            }
            if(annotation instanceof RequestParam){
                RequestParam requestParam = (RequestParam) annotation;
                tsParameter.setOptional(!requestParam.required());
                if(!ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue())) {
                    tsParameter.setDefaultValue(requestParam.defaultValue());
                }
                return;
            }
            if(annotation instanceof RequestBody) {
                RequestBody requestBody = (RequestBody) annotation;
                tsParameter.setOptional(!requestBody.required());
                return;
            }
       }
    }

    public static boolean isMapParameter(Annotation... annotations) {
        for (Annotation annotation : annotations) {
            if(annotation instanceof PathVariable){
                return true;
            }
            if(annotation instanceof RequestParam){
                return true;
            }
            if(annotation instanceof RequestBody) {
                return true;
            }
        }
        return false;

    }

    public static boolean parameterIsMapped(TSParameter tsParameter) {
        List<Annotation> annotationList = tsParameter.getAnnotationList();
        if (isMapParameter(annotationList.toArray(new Annotation[0]))) {
            return true;
        }
//        if (tsParameter.getType() instanceof TSInterfaceReference) {
//            TSInterfaceReference tsInterfaceReference = (TSInterfaceReference) tsParameter.getType();
//            for (Class nextClass : tsInterfaceReference.getReferencedType().getMappedFromJavaTypeSet()) {
//                for (RestConversionExtension extension : getConversionExtensionList()) {
//                    if (extension.isMappedRestParam(nextClass)) {
//                        return true;
//                    }
//                }
//            }
//        }
        return false;
    }

    public static boolean isRestMethod(Method method) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            return true;
        }
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(RequestMapping.class)) {
                return true;
            }
        }
        return false;
    }
//    /**
//     * 返回false 则不为 restmethod 否则返回 Method
//     * @param
//     * @return method springmvc中子类的 RequestMapping注解 可能是写在继承父接口或者父类中,
//     * 这里找到写有 RequestMapping 注解的方法
//     */
//    public static Method isRestMethod(Method method) {
//        boolean isRestMethod = checkIsMethod(method);
//        if (isRestMethod){
//            return method;
//        }
//                //查找父接口和父类里是否有RequestMapping注解
//        Class < ?> declaringClass = method.getDeclaringClass();
//        //这个方法是class中的方法
//        if (declaringClass.equals(Object.class)) {
//            return null;
//        }
//        if (!declaringClass.isInterface()) {
//            Class depParentClass = declaringClass;
//            while (true) {
//                if (depParentClass.equals(Object.class)) {
//                    break;
//                }
//                depParentClass = depParentClass.getSuperclass();
//                try {
//                    Method declaredMethod = depParentClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
//                    if (checkIsMethod(declaredMethod)) {
//                        return declaredMethod;
//                    }
//                } catch (NoSuchMethodException ignore) {
//                }
//            }
//        }
//        Class<?>[] interfaces = declaringClass.getInterfaces();
//        for (Class<?> anInterface : interfaces) {
//            try {
//                Method declaredMethod = anInterface.getDeclaredMethod(method.getName(), method.getParameterTypes());
//                Method checkIsRestMethod = isRestMethod(declaredMethod);
//                if (checkIsRestMethod != null) {
//                    return checkIsRestMethod;
//                }
//            } catch (NoSuchMethodException e) {
//                continue;
//            }
//        }
//
//        return null;
//    }

    private static boolean checkIsMethod(Method method) {
        if (Modifier.isStatic(method.getModifiers())) {
            return false;
        }
        if (method.isAnnotationPresent(RequestMapping.class)) {
            return true;
        }
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(RequestMapping.class)) {
                return true;
            }
        }
        return false;
    }

    private void setSupperClass(Class javaType, TSClass tsClass) {
        TSType tsSupperClass = TypeMapper.map(javaType.getAnnotatedSuperclass().getType());
        if(tsSupperClass instanceof TSClassReference){
            TSClassReference tsClassReference = (TSClassReference) tsSupperClass;
            convertFormalTypeParameters(javaType.getTypeParameters(), tsClassReference);
            tsClass.setExtendsClass(tsClassReference);
            tsClass.addScopedTypeUsage(tsClassReference);
        }
    }
}

package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.tsmodel.*;
import com.blueveery.springrest2ts.tsmodel.generics.TSInterfaceReference;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

/**
 * Created by yangyi on 2021/1/24.
 */
public class RestInterfaceConverter extends ComplexTypeConverter{
    public RestInterfaceConverter(ImplementationGenerator implementationGenerator) {
        super(implementationGenerator);
    }

    public RestInterfaceConverter(ImplementationGenerator implementationGenerator, ClassNameMapper classNameMapper) {
        super(implementationGenerator, classNameMapper);
    }

    @Override
    public boolean preConverted(JavaPackageToTsModuleConverter javaPackageToTsModuleConverter, Class javaClass) {
        if(TypeMapper.map(javaClass) == TypeMapper.tsAny){
            TSModule tsModule = javaPackageToTsModuleConverter.getTsModule(javaClass);
            String tsClassName = createTsClassName(javaClass);
            TSInterface tsInterface = new TSInterface(tsClassName, tsModule);
            this.implementationGenerator.addComplexTypeUsage(tsInterface);
            tsModule.addScopedElement(tsInterface);
            TypeMapper.registerTsType(javaClass, tsInterface);
            return true;
        }
        return false;
    }

    @Override
    public void convert(Class javaClass, NullableTypesStrategy nullableTypesStrategy) {
        TSInterfaceReference tSInterfaceReference = (TSInterfaceReference) TypeMapper.map(javaClass);
        TSInterface tsInterface = tSInterfaceReference.getReferencedType();

        convertFormalTypeParameters(javaClass.getTypeParameters(), tSInterfaceReference);
        tsInterface.addAllAnnotations(javaClass.getAnnotations());
        Method[] restMethodList = javaClass.getDeclaredMethods();
        sortMethods(restMethodList);

        for (Method method: restMethodList) {

            Map<TypeVariable, Type> variableToJavaType = new HashMap<>();
            Class<?> declaringClass = method.getDeclaringClass();
            boolean methodMustBeInlined = declaringClass != javaClass && method.getDeclaringClass().isInterface();
//            if(methodMustBeInlined){
//                variableToJavaType = mapVariableToJavaType(javaClass, declaringClass, new HashMap<>());
//            }

            Type genericReturnType = method.getGenericReturnType();
            String methodName = method.getName();
            TSType methodReturnType = TypeMapper.map(genericReturnType, variableToJavaType);
            tsInterface.getModule().scopedTypeUsage(methodReturnType);
            TSMethod tsMethod = new TSMethod(methodName, tsInterface, methodReturnType, implementationGenerator, false, false);
            tsMethod.setAbstract(true);
            tsMethod.addAllAnnotations(method.getAnnotations());
            for (Parameter parameter:method.getParameters()) {
                TSType tsType = TypeMapper.map(parameter.getParameterizedType(), variableToJavaType);
                TSParameter tsParameter = new TSParameter(parameter.getName(), tsType, tsMethod, implementationGenerator);
                tsParameter.addAllAnnotations(parameter.getAnnotations());
                if (SpringAnnotationsBasedRestClassConverter.parameterIsMapped(tsParameter)) {
                    tsInterface.getModule().scopedTypeUsage(tsParameter.getType());
                    SpringAnnotationsBasedRestClassConverter.setOptional(tsParameter);
                    nullableTypesStrategy.setAsNullableType(parameter.getParameterizedType(), parameter.getDeclaredAnnotations(), tsParameter);
                    tsMethod.getParameterList().add(tsParameter);
                }
                conversionListener.tsParameterCreated(parameter, tsParameter);
            }
            tsInterface.addTsMethod(tsMethod);
            conversionListener.tsMethodCreated(method, tsMethod);
        }

        // implementationGenerator.addComplexTypeUsage(tsInterface);
        implementationGenerator.getSerializationExtension().addComplexTypeUsage(tsInterface);
        conversionListener.tsScopedTypeCreated(javaClass, tsInterface);
    }

    public static void sortMethods(Method[] restMethodList) {
        Arrays.sort(restMethodList, new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                int i = o1.getName().compareTo(o2.getName());
                if (i == 0) {
                    i = o1.getParameterCount() - o2.getParameterCount();
                }
                if (i == 0) {
                    i = o1.getReturnType().getName().compareTo(o2.getReturnType().getName());
                }
                if (i == 0) {
                    int parameterCount = o1.getParameterCount();
                    Class<?>[] o1ParameterTypes = o1.getParameterTypes();
                    Class<?>[] o2parameterTypes = o2.getParameterTypes();
                    for (int pi = 0; pi < parameterCount; pi++) {
                        int pic = o1ParameterTypes[pi].getName().compareTo(o2parameterTypes[pi].getName());
                        if (pic != 0) {
                            i = pic;
                            break;
                        }
                    }
                }
                return i;
            }
        });
    }

    @Override
    public void convertInheritance(Class javaClass) {
        TSInterfaceReference tsClassReference = (TSInterfaceReference) TypeMapper.map(javaClass);
        TSInterface tsInterface = tsClassReference.getReferencedType();
        setSuperInterfaces(javaClass, tsInterface);
    }

    private void setSuperInterfaces(Class javaClass, TSInterface tsInterface) {
        Type[] genericInterfaces = javaClass.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            TSType tsType = TypeMapper.map(genericInterface);
            if (tsType instanceof TSInterfaceReference) {
                TSInterfaceReference tsInterfaceReference = (TSInterfaceReference) tsType;
                tsInterface.addExtendsInterfaces(tsInterfaceReference);
                tsInterface.addScopedTypeUsage(tsType);
            }
        }
    }
}

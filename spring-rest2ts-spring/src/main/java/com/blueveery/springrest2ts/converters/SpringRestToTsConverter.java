package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.StaticFieldConverter;
import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.spring.HandlerMethod2;
import com.blueveery.springrest2ts.spring.RequestMappingUtility;
import com.blueveery.springrest2ts.spring.SpringRestMethodFilter;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSParameter;
import com.blueveery.springrest2ts.tsmodel.TSType;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.async.DeferredResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.Callable;

public class SpringRestToTsConverter extends SpringAnnotationsBasedRestClassConverter{

    public SpringRestToTsConverter(ImplementationGenerator implementationGenerator) {
        super(implementationGenerator);
    }

    public SpringRestToTsConverter(ImplementationGenerator implementationGenerator, ClassNameMapper classNameMapper) {
        super(implementationGenerator, classNameMapper);
    }

    @Override
    protected void addClassAnnotations(Class javaClass, TSClass tsClass) {
        tsClass.addAllAnnotations(javaClass.getAnnotations());
    }

    @Override
    protected void addMethodAnnotations(Method method, TSMethod tsMethod) {
        tsMethod.addAllAnnotations(method.getAnnotations());
    }

    @Override
    protected void addParameterAnnotations(Parameter parameter, TSParameter tsParameter) {
        tsParameter.addAllAnnotations(parameter.getAnnotations());
    }


    public static Set<Class> searchModelClassOnRestClass(Class restClass) {
        Set<Class> modelClassSet = new HashSet<>();
        Map<Method, HandlerMethod2> restMethodList = SpringRestMethodFilter.detectHandlerMethod2s(restClass);
        for (HandlerMethod2 method : restMethodList.values()) {
            Type returnType = method.getReturnType().getGenericParameterType();
            searchType(modelClassSet, returnType);

            for (MethodParameter parameter : method.getMethodParameters()) {
                Annotation[] annotations = parameter.getParameterAnnotations();
                if (SpringAnnotationsBasedRestClassConverter.isMapParameter(annotations)) {
                    Type parameterizedType = parameter.getGenericParameterType();
                    searchType(modelClassSet, parameterizedType);
                }
            }
        }
        List<Type> genTsFieldType = StaticFieldConverter.getGenTsFieldType(restClass);
        for (Type type : genTsFieldType) {
            searchType(modelClassSet, type);
        }
        return modelClassSet;
    }


    public static boolean isActualClass(Type type) {
        if (type instanceof Class) {
            Class searchTypeWithClass = (Class) type;
            if (!searchTypeWithClass.isInterface() && !searchTypeWithClass.isPrimitive()) {
                //java包下面的ts代码全部都部生成
                if (searchTypeWithClass.getPackage().getName().startsWith("java")) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public static void searchType(Set<Class> types, Type searchType) {
        if (isActualClass(searchType)) {
            types.add((Class) searchType);
        }
        if (searchType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) searchType;
            Type rawType = parameterizedType.getRawType();
            if (isActualClass(rawType)) {
                types.add((Class) rawType);
            }
            Type[] arguments = parameterizedType.getActualTypeArguments();
            for (Type argument : arguments) {
                searchType(types, argument);
            }
        }
    }

    @Override
    protected Type handleImplementationSpecificReturnTypes(Method method) {
        return getReturnType(method);
    }

    private static Type getReturnType(Method method) {
        Type genericReturnType = method.getGenericReturnType();
        if (genericReturnType instanceof ParameterizedType) {// handling ResponseEntity
            ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
            Type rawType = parameterizedType.getRawType();
            if (rawType == ResponseEntity.class || rawType == DeferredResult.class || rawType == Callable.class) {
                genericReturnType = parameterizedType.getActualTypeArguments()[findPathCommonPrefixIndex(null)];
            }
        }
        return genericReturnType;
    }

    @Override
    protected RequestMapping getRequestMappingForMethod(Method method) {
        return RequestMappingUtility.getRequestMapping(Arrays.asList (method.getDeclaredAnnotations()));
    }
}

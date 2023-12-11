package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.*;
import com.fasterxml.jackson.annotation.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;

/**
 * 默认的java class转typeScript class的映射转换器
 * 这个转换器要求比较严格
 * 1 java的model类中的属性为public时 将会自动转换
 * 2 java的model中的属性为private时 必须要有对应的 get 和set方法
 * 注意 如果 属性为boolean类型时,不支持 isXXX 模式的json序列化方法(通用性比较差)
 * 3 model中只有get方法或者set方法的但是没有对应的属性 这种也不会生成为 typeScript 中的 class的属性
 * 4 如果java的model类中的属性是基本类型(这里的基本类型指的是字符串,布尔类型,数字类型) 因为json和typeScript支持的数据类型有限 只支持 java.lang.Number 的子类或其基本类型
 *                             Boolean 以及其基本类型
 *                             String  类型
 *
 *
 */
public class DefaultObjectMapper implements ObjectMapper {

    private static final Logger log = LoggerFactory.getLogger(DefaultObjectMapper.class);

    public DefaultObjectMapper() {
    }


    @Override
    public List<TSField> addTypeLevelSpecificFields(Class javaType, TSComplexElement tsComplexType) {
        return new ArrayList<>();
    }

    @Override
    public boolean filterClass(Class clazz) {
        return true;
    }

    @Override
    public boolean filter(Field field) {
        if (Modifier.isPublic(field.getModifiers())) {
            return true;
        }
        if (hasGetter(field) && hasSetter(field)) {
            return true;
        }
        log.warn("ignore private field {} in class {} because has not getter and setter method special boolean type cloud not use get set method like isXXX",field.getName(), field.getDeclaringClass().getName());

        return false;
    }

    private static boolean hasGetter(Field field) {
        Class<?> declaringClass = field.getDeclaringClass();
        String name = field.getName();
        try {
            Method method = declaringClass.getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
            return method.getGenericReturnType().equals(field.getGenericType());
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static boolean hasSetter(Field field) {
        Class<?> declaringClass = field.getDeclaringClass();
        String name = field.getName();
        try {
            Method method = declaringClass.getMethod("set" + name.substring(0, 1).toUpperCase() + name.substring(1),field.getType());
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @Override
    public boolean filter(Method method, boolean isGetter) {
        return false;
    }

    @Override
    public List<TSField> mapJavaPropertyToField(Property property, TSComplexElement tsComplexType,
                                                ComplexTypeConverter complexTypeConverter,
                                                ImplementationGenerator implementationGenerator,
                                                NullableTypesStrategy nullableTypesStrategy) {
        List<TSField> tsFieldList = new ArrayList<>();
        if (property.isIgnored()) {
            return tsFieldList;
        }
        Type type = property.getField().getGenericType();
        TSType fieldType = TypeMapper.map(type);
        TSField tsField = new TSField(property.getName(), tsComplexType, fieldType);
        tsFieldList.add(tsField);
        return tsFieldList;
    }

    @Override
    public String getPropertyName(Field field) {
        return field.getName();
    }

    @Override
    public String getPropertyName(Method method, boolean isGetter) {
        if (isGetter && method.getName().startsWith("get")) {
            return cutPrefix(method.getName(), "get");
        }

        if (!isGetter && method.getName().startsWith("set")) {
            return cutPrefix(method.getName(), "set");
        }

        throw new IllegalStateException();
    }

    @Override
    public void setIfIsIgnored(Property property, AnnotatedElement annotatedElement) {

    }

    private String cutPrefix(String methodName, String prefix) {
        if (methodName.startsWith(prefix)) {
            return Introspector.decapitalize(methodName.replaceFirst(prefix, ""));
        }
        return null;
    }

}

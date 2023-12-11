package com.blueveery.springrest2ts;

import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.filter.GenTsField;
import com.blueveery.springrest2ts.mapper.Object2JsonMapper;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSField;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangyi on 2021/1/17.
 */
public class StaticFieldConverter {

    public static List<Type> getGenTsFieldType(Class javaClass) {
        List<Type> fields = new ArrayList<>();

        for (Field declaredField : javaClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            int modifiers = declaredField.getModifiers();
            GenTsField annotation = declaredField.getAnnotation(GenTsField.class);
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && annotation != null) {
                fields.add(declaredField.getGenericType());
            }
        }
        return fields;
    }

    public static List<TSField> getGenStaticField(Class javaClass, TSComplexElement tsInterface, Object2JsonMapper jsonMapper) {
        List<TSField> retFields = new ArrayList<>();
        for (Field declaredField : javaClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            int modifiers = declaredField.getModifiers();
            GenTsField annotation = declaredField.getAnnotation(GenTsField.class);
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && annotation != null) {
                TSField tsField = new TSField(declaredField.getName(), tsInterface, TypeMapper.map(declaredField.getGenericType()));
                retFields.add(tsField);
                tsField.setReadOnly(true);
                try {
                    Object fieldValue = declaredField.get(javaClass);
                    String jsonV = jsonMapper.Object2Json(fieldValue);
                    tsField.setDefaultValue(jsonV);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return retFields;
    }

}

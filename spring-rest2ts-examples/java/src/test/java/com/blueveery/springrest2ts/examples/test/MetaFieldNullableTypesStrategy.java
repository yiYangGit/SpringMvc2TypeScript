package com.blueveery.springrest2ts.examples.test;

import com.blueveery.springrest2ts.converters.NullableTypesStrategy;
import com.blueveery.springrest2ts.tsmodel.INullableElement;
import example.dev.anno.MetaFiledInfo;
import example.dev.meta.fields.MetaField;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by yangyi0 on 2021/5/27.
 */
public class MetaFieldNullableTypesStrategy implements NullableTypesStrategy {
    @Override
    public void setAsNullableType(Type elementType, Annotation[] declaredAnnotations, INullableElement tsElement) {
        for (Annotation declaredAnnotation : declaredAnnotations) {
            if (declaredAnnotation instanceof MetaFiledInfo) {
                MetaFiledInfo metaFiledInfo = (MetaFiledInfo) declaredAnnotation;
                try {
                    boolean notnull = MetaField.getMetaFieldFromAnnotation(metaFiledInfo).getNotnull();
                    tsElement.setNullable(!notnull);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

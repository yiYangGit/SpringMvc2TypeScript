package com.blueveery.springrest2ts.examples.test;

import com.blueveery.springrest2ts.TsClassCreateListen;
import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSType;
import example.config.GsonCvt;
import example.dev.anno.MetaFiledInfo;
import example.dev.meta.fields.MetaField;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by yangyi0 on 2021/2/5.
 */
public class ModelMetaFieldTsGenerator implements TsClassCreateListen {

    @Override
    public boolean onTsClassCreate(TSClass tsClass, Class javaClass) {
        Field[] declaredFields = javaClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            MetaFiledInfo annotation = declaredField.getAnnotation(MetaFiledInfo.class);
            if (annotation != null) {
                TSType typeUse = TypeMapper.map(MetaField.class);
                tsClass.getModule().scopedTypeUsage(typeUse);
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterTsClassWrite(BufferedWriter writer, TSClass tsClass, Class javaClass) throws IOException {
        Field[] declaredFields = javaClass.getDeclaredFields();
        writer.newLine();
        writer.write("export class ");
        writer.write(tsClass.getName() + "_SCHEMA ");
        writer.write("{");
        writer.newLine();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            MetaFiledInfo annotation = declaredField.getAnnotation(MetaFiledInfo.class);
            if (annotation != null) {
                MetaField metaField = MetaField.getMetaFieldFromAnnotation(annotation);
                metaField.setName(declaredField.getName());

                writer.write("      public");
                writer.write(" " + metaField.getName());
                writer.write(": ");
                writer.write(MetaField.class.getSimpleName());
                writer.write(" = ");
                writer.write("<");
                writer.write(MetaField.class.getSimpleName());
                writer.write(">");
                writer.write(GsonCvt.getGson().toJson(metaField));
                writer.write(";");
                writer.newLine();
            }
        }
        writer.write("}");
    }
}

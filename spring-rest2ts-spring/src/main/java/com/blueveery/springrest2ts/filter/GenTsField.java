package com.blueveery.springrest2ts.filter;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
//标记为要生成ts readOnly属性的类
public @interface GenTsField {
}
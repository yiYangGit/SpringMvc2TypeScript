package com.blueveery.springrest2ts.filter;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
//标记为要生成ts model 的类
public @interface GenTsModel {

}
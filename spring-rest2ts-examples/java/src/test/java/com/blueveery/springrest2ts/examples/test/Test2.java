package com.blueveery.springrest2ts.examples.test;

import com.blueveery.springrest2ts.FastCodeGenerator;
import com.blueveery.springrest2ts.FastCodeGeneratorConfig;
import com.blueveery.springrest2ts.mapper.Object2JsonMapper;
import com.blueveery.springrest2ts.spring.HandlerMethod2;
import com.blueveery.springrest2ts.spring.SpringRestMethodFilter;
import example.biz.TestUserManager;
import example.config.GsonCvt;
import org.junit.Test;
import org.springframework.core.MethodParameter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * Created by yangyi on 2021/1/17.
 */

public class Test2 {
    protected static final Path OUTPUT_DIR_PATH = Paths.get("../web/src/app/_model");
    @Test
    public void test() throws IOException {
        long start = System.currentTimeMillis();
        FastCodeGeneratorConfig generatorConfig = new FastCodeGeneratorConfig();
        generatorConfig.setObject2JsonMapper(new Object2JsonMapper() {
            @Override
            public String Object2Json(Object o) {
                return GsonCvt.getGson().toJson(o);
            }
        });
        generatorConfig.setNullableTypesStrategy(new MetaFieldNullableTypesStrategy());
        generatorConfig.getTsClassCreateListens().add(new ModelMetaFieldTsGenerator());
        FastCodeGenerator.tsModuleCreatorConverter(OUTPUT_DIR_PATH.toFile(),true, Collections.singleton("example")
                , "/", generatorConfig
        );
        System.out.println(System.currentTimeMillis() - start);
    }


    @Test
    public void test2() throws IOException {
        Map<Method,HandlerMethod2> handlerMethod2s = SpringRestMethodFilter.detectHandlerMethod2s(TestUserManager.class);
        for (HandlerMethod2 handlerMethod2 : handlerMethod2s.values()) {
            for (MethodParameter methodParameter : handlerMethod2.getMethodParameters()) {
                System.out.println(Arrays.toString(methodParameter.getParameterAnnotations()));
            }
            System.out.println("-----------");
        }
    }
}

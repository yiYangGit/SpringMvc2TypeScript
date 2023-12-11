package com.blueveery.springrest2ts;

import com.blueveery.springrest2ts.converters.DefaultNullableTypesStrategy;
import com.blueveery.springrest2ts.converters.NullableTypesStrategy;
import com.blueveery.springrest2ts.mapper.Object2JsonMapper;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by yangyi on 2021/2/5.
 */
public class FastCodeGeneratorConfig {

    private Object2JsonMapper object2JsonMapper = null;

    private NullableTypesStrategy nullableTypesStrategy = new DefaultNullableTypesStrategy();

    public NullableTypesStrategy getNullableTypesStrategy() {
        return nullableTypesStrategy;
    }

    public FastCodeGeneratorConfig setNullableTypesStrategy(NullableTypesStrategy nullableTypesStrategy) {
        this.nullableTypesStrategy = nullableTypesStrategy;
        return this;
    }

    //必须要生成ts代码的 model类
    private Set<Class<?>> baseModelClass = new HashSet<>();
    //必须要生成ts代码的 rest类
    private Set<Class<?>> baseRestClass = new HashSet<>();
    private Set<TsClassCreateListen> tsClassCreateListens = new HashSet<>();

    public Object2JsonMapper getObject2JsonMapper() {
        return object2JsonMapper;
    }

    public FastCodeGeneratorConfig setObject2JsonMapper(Object2JsonMapper object2JsonMapper) {
        this.object2JsonMapper = object2JsonMapper;
        return this;
    }

    public Set<Class<?>> getBaseModelClass() {
        return baseModelClass;
    }

    public void setBaseModelClass(Set<Class<?>> baseModelClass) {
        this.baseModelClass = baseModelClass;
    }

    public Set<Class<?>> getBaseRestClass() {
        return baseRestClass;
    }

    public void setBaseRestClass(Set<Class<?>> baseRestClass) {
        this.baseRestClass = baseRestClass;
    }

    public Set<TsClassCreateListen> getTsClassCreateListens() {
        return tsClassCreateListens;
    }

    public void setTsClassCreateListens(Set<TsClassCreateListen> tsClassCreateListens) {
        this.tsClassCreateListens = tsClassCreateListens;
    }
}

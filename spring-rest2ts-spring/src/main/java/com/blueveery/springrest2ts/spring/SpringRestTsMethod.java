package com.blueveery.springrest2ts.spring;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSType;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Created by yangyi0 on 2021/1/22.
 */
public class SpringRestTsMethod extends TSMethod {
    private final HandlerMethod2 handlerMethod2;

    public HandlerMethod2 getHandlerMethod2() {
        return handlerMethod2;
    }

    public SpringRestTsMethod(String name, TSComplexElement owner, TSType type, ImplementationGenerator implementationGenerator, boolean isAbstract, boolean isConstructor,
                              HandlerMethod2 handlerMethod2
    ) {
        super(name, owner, type, implementationGenerator, isAbstract, isConstructor);
        this.handlerMethod2 = handlerMethod2;
    }

}

package com.blueveery.springrest2ts.spring;

import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodParameter;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by yangyi on 2021/1/21.
 */
public class SpringRestMethodFilter extends RequestMappingHandlerMapping {
    public static Set<Method> filterRestMethods(Class handlerType) {
        RequestMappingHandlerMappingExtends mappingExtends = new RequestMappingHandlerMappingExtends();
        return mappingExtends.detectMappingMethods(handlerType).keySet();
    }

    public static Map<Method,HandlerMethod2> detectHandlerMethod2s(Class handlerType) {
        RequestMappingHandlerMappingExtends mappingExtends = new RequestMappingHandlerMappingExtends();
        return mappingExtends.detectHandlerMethod2s(handlerType);
    }


}

class RequestMappingHandlerMappingExtends extends RequestMappingHandlerMapping {

    public Map<Method, RequestMappingInfo> detectMappingMethods(Class handlerType) {
        Class<?> userType = ClassUtils.getUserClass(handlerType);
        Map<Method, RequestMappingInfo> methods = MethodIntrospector.selectMethods(userType,
                (MethodIntrospector.MetadataLookup<RequestMappingInfo>) method -> {
                    try {
                        return getMappingForMethod(method, userType);
                    } catch (Throwable ex) {
                        throw new IllegalStateException("Invalid mapping on handler class [" +
                                userType.getName() + "]: " + method, ex);
                    }
                });
        return methods;
    }

    public Map<Method,HandlerMethod2> detectHandlerMethod2s(Class handlerType) {
        Map<Method, HandlerMethod2> springRestMethodMapping = new HashMap<>();
        Map<Method, RequestMappingInfo> methodEntry = detectMappingMethods(handlerType);
        for (Map.Entry<Method, RequestMappingInfo> entry : methodEntry.entrySet()) {
            HandlerMethod2 method2 = new HandlerMethod2(entry.getKey(), handlerType, entry.getValue());
            springRestMethodMapping.put(entry.getKey(), method2);
        }
        return springRestMethodMapping;
    }

}

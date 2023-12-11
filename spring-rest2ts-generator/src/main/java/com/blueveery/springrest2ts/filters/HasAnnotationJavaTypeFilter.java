package com.blueveery.springrest2ts.filters;

import org.slf4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HasAnnotationJavaTypeFilter implements JavaTypeFilter {
    Set<Class> annotations;

    public HasAnnotationJavaTypeFilter(Class... annotations) {
        for (Class annotation : annotations) {
            if (!annotation.isAnnotation()) {
                throw new IllegalStateException("Annotation required");
            }
        }

//        @Target({ElementType.METHOD, ElementType.TYPE})
//        @Retention(RetentionPolicy.RUNTIME)
//        Target targetAnnotation = (Target) annotation.getAnnotation(Target.class);
//        if (targetAnnotation != null) {
//            Arrays.binarySearch(targetAnnotation.value(), ElementType.TYPE).
//        }

        this.annotations = new HashSet<Class>(Arrays.asList(annotations));
    }

    @Override
    public boolean accept(Class javaType) {
        for (Class annotation : annotations) {
            if (javaType.isAnnotationPresent(annotation)) {
                return true;
            }
        }
        return false;

    }

    @Override
    public void explain(Class packageClass, Logger logger, String indentation) {
//        if (accept(packageClass)) {
//            logger.info(indentation + String.format("TRUE => class %s has annotation %s", packageClass.getSimpleName(), annotation.getSimpleName()));
//        }else {
//            logger.warn(indentation + String.format("FALSE => class %s doesn't have annotation %s", packageClass.getSimpleName(), annotation.getSimpleName() ));
//        }
    }
}

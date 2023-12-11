package com.blueveery.springrest2ts;

import java.util.List;
import java.util.Set;

/**
 * Created by yangyi on 2021/1/16.
 */
public interface SearchExtendsClass {

    Set<Class> searchModelClassOnRestClass(List<Class> loadClass, Set<Class> modelClasses, Set<Class> restClasses, Set<Class> enumClasses);

}

package example.biz.utls;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by yangyi0 on 2020/9/2.
 */
public class ReflectUtils {

    public static ParameterizedType getParameterizedTypeByTarget(Object targetObj, Class<?> targetClass) {
        Class<?> searchClass = targetObj.getClass();
        while (true) {
            Type[] interfacesTypes = searchClass.getGenericInterfaces();
            if (interfacesTypes.length == 0) {
                searchClass = searchClass.getSuperclass();
                if (searchClass == null) {
                    return null;
                } else {
                    continue;
                }
            }
            for (Type t : interfacesTypes) {
                if (t instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) t;
                    Type rawType = pt.getRawType();
                    if (rawType.equals(targetClass)) {
                        return pt;
                    }
                }
            }
        }
    }
}

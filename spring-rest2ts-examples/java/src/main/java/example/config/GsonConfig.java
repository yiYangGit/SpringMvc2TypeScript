package example.config;

import example.dev.anno.MetaFiledInfo;
import example.dev.meta.fields.MetaField;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJsonHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PreDestroy;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 控制spring mvc中的 @RequestBody和 @ResponseBody
 */
@Configuration
class JsonSerializeConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //删除 http json的消息转换器,换为自己的http 消息转换器
        converters.removeIf(next -> next instanceof AbstractJsonHttpMessageConverter);
        converters.add(0,httpMessageConverters());
    }

    /**
     * 缓存需要校验MetaField 的 class 类
     */
    private Map<Class<?>, ObjectValidatorField> classObjectMetaFieldCache = new ConcurrentHashMap<>();

    private static final ObjectValidatorField NONE_VALIDATOR = new ObjectValidatorField();
    @PreDestroy
    public void OnDestroy() {
        classObjectMetaFieldCache.clear();
    }


    public GsonHttpMessageConverter httpMessageConverters() {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter(){
            @Override
            protected Object readInternal(Type resolvedType, Reader reader) throws Exception {
                Object o = super.readInternal(resolvedType, reader);
                validator(o);
                return o;
            }
        };
        //使用GsonBuilder配置日期格式
        //其他配置
        converter.setGson(GsonCvt.getGson());
        return converter;
    }


    /**
     * 根据MetaField信息检测信息是否合法
     *
     * @param o
     */
    private void validator(Object o) throws Exception {
        if (o == null) {
            return;
        }
        //map只判断所有的value值
        if (o instanceof Map) {
            Map map = (Map) o;
            Collection values = map.values();
            for (Object value : values) {
                validator(value);
            }
            return;
        }
        //判断数据是不是集合类型
        if (o instanceof Collection) {
            Collection collection = (Collection) o;
            for (Object arrayItem : collection) {
                validator(arrayItem);
            }
            return;
        }
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            Object[] array = (Object[]) o;
            for (Object arrayItem : array) {
                validator(arrayItem);
            }
            return;
        }

        //不校验接口
        if (clazz.isInterface()) {
            return;
        }
        while (true) {
            if (null == clazz || clazz.equals(Object.class)) {
                return;
            }
            ObjectValidatorField objectValidatorField = classObjectMetaFieldCache.get(clazz);
            if (objectValidatorField == NONE_VALIDATOR) {
                return;
            }
            if (objectValidatorField == null) {
                objectValidatorField = createObjectValidatorField(clazz);
                classObjectMetaFieldCache.put(clazz, objectValidatorField);
            }
            List<FieldAndMetaInfo> metaFields = objectValidatorField.getMetaFields();
            for (FieldAndMetaInfo metaField : metaFields) {
                metaField.getMetaField().validate(metaField.getField().get(o));
            }
            List<Field> fields = objectValidatorField.getObjectFields();
            for (Field field : fields) {
                validator(field.get(o));
            }
            clazz = clazz.getSuperclass();
        }

    }

    private ObjectValidatorField createObjectValidatorField(Class<?> clazz) {
        //java包开头的类全部不校验
        if (isJavaPackageClassOrBaseType(clazz)) {
            return NONE_VALIDATOR;
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        ObjectValidatorField objectValidatorField = new ObjectValidatorField();
        List<FieldAndMetaInfo> fieldAndMetaInfos = objectValidatorField.getMetaFields();
        List<Field> objectFields = objectValidatorField.getObjectFields();
        for (Field declaredField : declaredFields) {
            if (Modifier.isStatic(declaredField.getModifiers())) {
                continue;
            }
            declaredField.setAccessible(true);
            MetaFiledInfo annotation = declaredField.getAnnotation(MetaFiledInfo.class);
            if (annotation != null) {
                MetaField metaField = MetaField.getMetaFieldFromAnnotation(annotation);
                fieldAndMetaInfos.add(new FieldAndMetaInfo(declaredField, metaField));
                continue;
            }
            Type genericType = declaredField.getGenericType();
            if (!(genericType instanceof Class)) {
                objectFields.add(declaredField);
                continue;
            }
            Class<?> genericClazz = (Class<?>) genericType;
            if (!isJavaPackageClassOrBaseType(genericClazz)) {
                objectFields.add(declaredField);
                continue;
            }
        }
        if (objectValidatorField.getObjectFields().isEmpty() && objectValidatorField.getMetaFields().isEmpty()) {
            return NONE_VALIDATOR;
        }
        return objectValidatorField;
    }

    private boolean isJavaPackageClassOrBaseType(Class<?> clazz) {
        return clazz.getPackage().getName().startsWith("java") || clazz.isPrimitive();
    }
}

class FieldAndMetaInfo {
    private Field field;
    private MetaField metaField;

    public FieldAndMetaInfo(Field field, MetaField metaField) {
        this.field = field;
        this.metaField = metaField;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public MetaField getMetaField() {
        return metaField;
    }

    public void setMetaField(MetaField metaField) {
        this.metaField = metaField;
    }
}

class ObjectValidatorField {
    private List<Field> objectFields =new ArrayList<>();
    private List<FieldAndMetaInfo> metaFields = new ArrayList<>();

    public List<FieldAndMetaInfo> getMetaFields() {
        return metaFields;
    }

    public void setMetaFields(List<FieldAndMetaInfo> metaFields) {
        this.metaFields = metaFields;
    }

    public List<Field> getObjectFields() {
        return objectFields;
    }

    public void setObjectFields(List<Field> objectFields) {
        this.objectFields = objectFields;
    }
}


package example.biz.core;

import example.biz.utls.ReflectUtils;
import example.dev.meta.fields.MetaField;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by yangyi0 on 2021/2/6.
 */
public abstract class BaseRecordObj<R> implements RecordObj<R>{
    @Override
    public R get() {
        return doGet();
    }

    @Override
    public void set(R r) {
        doSet(r);
    }

    public abstract R doGet();

    public abstract R doSet(R s);

    @Override
    public List<MetaField> getSchemas() {
        ParameterizedType target = ReflectUtils.getParameterizedTypeByTarget(this, BaseRecordObj.class);
        Class<R> actualTypeArgument = (Class<R>) target.getActualTypeArguments()[0];
        return MetaField.fromClass(actualTypeArgument);
    }
}

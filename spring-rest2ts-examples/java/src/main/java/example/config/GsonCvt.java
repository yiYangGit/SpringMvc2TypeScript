package example.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * Created by yangyi0 on 2021/1/27.
 * json序列工具类
 * 主要讲 thrift中的枚举序列化成 数字
 */
public class GsonCvt {

    public static Gson getGson() {
        Gson gson = new GsonBuilder()
                .serializeNulls().registerTypeAdapterFactory(new MetaFieldTypeAdapterFactory())
                .create();
        return gson;
    }
}

class MetaFieldTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        return null;
    }
}
package example.biz;


import com.blueveery.springrest2ts.filter.GenTsField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangyi0 on 2021/5/27.
 */

public class TestUser3 <T extends Number>{
    @GenTsField()
    public static final List<TestUser3<Integer>> objs = new ArrayList<>();

    static {
        TestUser3<Integer> e = new TestUser3<>();
        e.setT3(1);
        objs.add(e);
        objs.add(e);
    }
    T t3;

    public T getT3() {
        return t3;
    }

    public TestUser3<T> setT3(T t3) {
        this.t3 = t3;
        return this;
    }
}

package example.biz;

import example.dev.anno.MetaFiledInfo;
import example.model.core.BaseDTO;
import com.blueveery.springrest2ts.filter.GenTsField;
import com.blueveery.springrest2ts.filter.GenTsModel;

import java.math.BigDecimal;

/**
 * Created by yangyi on 2021/1/16.
 */
@GenTsModel
public class TestUser2<T extends TestUser3<Integer>,F extends TbCond> extends BaseDTO {

    @GenTsField()
    public static final String h = "\"hhhh";
    @GenTsField()
    public static final String asda = "asda";
    @GenTsField()
    public static final String a = "a";
    @GenTsField()
    public static final String b = "b";
    @GenTsField()
    public static final String c = "c";
    @GenTsField()
    public static final Integer d = 2;
    @GenTsField()
    public static final BigDecimal e = new BigDecimal("2E222");
    @GenTsField()
    public static final Boolean f = null;

    public BigDecimal qweqw;

    public int adminId;

    private F fn;

    public F getFn() {
        return fn;
    }

    public TestUser2<T, F> setFn(F fn) {
        this.fn = fn;
        return this;
    }

    public T t;

    public T getT() {
        return t;
    }

    public TestUser2<T,F> setT(T t) {
        this.t = t;
        return this;
    }
}

package example.model;

import example.model.core.BaseDTO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by yangyi on 2021/1/16.
 */
public class TestUser<T,R> extends BaseDTO {
    public BigDecimal qweqw;
    public T t;
    public R r;
    public Date updateTimeStamp;
    public List<String> strs;
    public User2<User2<User3>> user3User2;
}

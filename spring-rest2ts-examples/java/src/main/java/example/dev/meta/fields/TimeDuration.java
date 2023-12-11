package example.dev.meta.fields;

import com.blueveery.springrest2ts.filter.GenTsModel;
import example.dev.validator.MetaFieldValidatorException;
import org.jooq.DataType;
import org.jooq.impl.SQLDataType;

/**
 * Created by yangyi0 on 2021/1/25.
 * 以毫秒位单位的时间长度配置
 */
@GenTsModel
public class TimeDuration extends MetaField {

    private Long min;
    private Long max;

    public TimeDuration(String name, String caption, boolean notnull) {
        super(name, caption, notnull,MetaFieldType.TIME_DURATION);
    }

    @Override
    public DataType<?> dataType() {
        return SQLDataType.BIGINT;
    }

    public Long getMin() {
        return min;
    }

    public TimeDuration setMin(Long min) {
        this.min = min;
        return this;
    }

    public Long getMax() {
        return max;
    }

    public TimeDuration setMax(Long max) {
        this.max = max;
        return this;
    }

    @Override
    protected void exValidate(Object data) throws MetaFieldValidatorException {

        if (!(data instanceof Long) && !(data instanceof Integer)) {
            throw new MetaFieldValidatorException(getName() + "=" + data + " is not a vaild TimeDuration Type");
        }
        Long v = ((Number) data).longValue();
        if (min != null ) {
            if (v < min) {
                throw new MetaFieldValidatorException(getName() + "=" + data + " is small than min " + min);
            }
        }
        if (max != null) {
            if (v > max) {
                throw new MetaFieldValidatorException(getName() + "=" + data + " is big than max " + max);
            }
        }
    }

}

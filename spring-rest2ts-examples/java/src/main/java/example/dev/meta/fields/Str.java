package example.dev.meta.fields;

import com.blueveery.springrest2ts.filter.GenTsModel;
import example.dev.validator.MetaFieldValidatorException;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;

@GenTsModel
public class Str extends MetaField {
    private static int maxDefault = 4096;    // 如果不设置，缺省的max

    private Integer min = null;
    private Integer max = null;

    public Str(String name, String caption, boolean notnull) {
        super(name, caption, notnull, MetaFieldType.STR);
    }

    public Integer getMin() {
        return min;
    }

    public Str setMin(Integer min) {
        this.min = min;
        return this;
    }

    public Integer getMax() {
        return max;
    }

    public Str setMax(Integer max) {
        this.max = max;
        return this;
    }

    @Override
    public DataType<?> dataType() {
        if (max != null) {
            return SQLDataType.VARCHAR(max);
        } else {
            return SQLDataType.VARCHAR(maxDefault);
        }

    }


    @Override
    protected void exValidate(Object data) throws MetaFieldValidatorException {
        if (!(data instanceof String)) {
            throw new MetaFieldValidatorException(getName() + "=" + data + " is not a valid String");
        }
        String v = (String) data;
        int len = v.length();
        if (min != null && len < min) {
            throw new MetaFieldValidatorException(getName() + "=" + data + " is small than min length " + min);
        }
        if (max != null && len > max) {
            throw new MetaFieldValidatorException(getName() + "=" + data + " is big than max length " + max);
        }

    }
    
	@SuppressWarnings("unchecked")
	@Override
	public Field<String> toJooqField() {
		return (Field<String>) super.toJooqField();
	}

}

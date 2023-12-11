package example.dev.meta.fields;

import com.blueveery.springrest2ts.filter.GenTsModel;
import example.dev.validator.MetaFieldValidatorException;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;

@GenTsModel
public class Int extends MetaField {
	private Integer min = null;
	private Integer max = null;

    public Integer getMin() {
        return min;
    }

    public Int setMin(Integer min) {
        this.min = min;
        return this;
    }

    public Integer getMax() {
        return max;
    }

    public Int setMax(Integer max) {
        this.max = max;
        return this;
    }

    public Int(String name, String caption, boolean notnull) {
		super(name, caption, notnull,MetaFieldType.INT);
        this.max = Integer.MAX_VALUE;
	}
	

	
	@Override
	public DataType<?> dataType() {
		return SQLDataType.INTEGER;
	}

	
	@Override
	protected void exValidate(Object data) throws MetaFieldValidatorException {
		if (!(data instanceof Integer)) {
			throw new MetaFieldValidatorException(getName() + "=" + data + " is not a vaild Int Type");
		}
        Integer v = (Integer)data;
        if (min != null && v < min) {
            throw new MetaFieldValidatorException(getName() + "=" + data + " is small than min " + min);
        }
        if (max != null && v > max) {
            throw new MetaFieldValidatorException(getName() + "=" + data + " is big than max " + max);
        }
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Field<Integer> toJooqField() {
		return (Field<Integer>) super.toJooqField();
	}

}

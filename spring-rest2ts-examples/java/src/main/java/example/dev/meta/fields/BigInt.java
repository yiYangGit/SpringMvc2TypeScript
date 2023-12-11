package example.dev.meta.fields;

import com.blueveery.springrest2ts.filter.GenTsModel;
import example.dev.validator.MetaFieldValidatorException;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;

@GenTsModel
public class BigInt extends MetaField {
	private Long min = null;
	private Long max = null;

    public Long getMin() {
        return min;
    }

    public BigInt setMin(Long min) {
        this.min = min;
        return this;
    }

    public Long getMax() {
        return max;
    }

    public BigInt setMax(Long max) {
        this.max = max;
        return this;
    }

    public BigInt(String name, String caption, boolean notnull) {
		super(name, caption, notnull,MetaFieldType.BIG_INT);
        this.max = Long.MAX_VALUE;
	}

	
	@Override
	public DataType<?> dataType() {
		return SQLDataType.BIGINT;
	}


	@Override
	protected void exValidate(Object data) throws MetaFieldValidatorException {
		
		if (!(data instanceof Long) && !(data instanceof Integer)) {
			throw new MetaFieldValidatorException(getName() + "=" + data + " is not a vaild BigInt Type");
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
	
	@SuppressWarnings("unchecked")
	@Override
	public Field<Long> toJooqField() {
		return (Field<Long>) super.toJooqField();
	}


}

package example.dev.meta.fields;

import com.blueveery.springrest2ts.filter.GenTsModel;
import example.dev.validator.MetaFieldValidatorException;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;

@GenTsModel
public class Port extends MetaField {
	private Integer min = 0;
	private Integer max = 65535;
	
	public Port(String name, String caption, boolean notnull) {
		super(name, caption, notnull,MetaFieldType.PORT);
	}

    public Integer getMin() {
        return min;
    }

    public Port setMin(Integer min) {
        this.min = min;
        return this;
    }

    public Integer getMax() {
        return max;
    }

    public Port setMax(Integer max) {
        this.max = max;
        return this;
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
		
		if (min != null || max != null) {
			Integer v = (Integer)data;
			
			if (v < min) {
				throw new MetaFieldValidatorException(getName() + "=" + data + " is small than min " + min);
			}
			if (v > max) {
				throw new MetaFieldValidatorException(getName() + "=" + data + " is big than max " + max);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Field<Integer> toJooqField() {
		return (Field<Integer>) super.toJooqField();
	}

}

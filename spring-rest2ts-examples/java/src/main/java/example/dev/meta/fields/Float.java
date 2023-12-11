package example.dev.meta.fields;

import com.blueveery.springrest2ts.filter.GenTsModel;
import example.dev.validator.MetaFieldValidatorException;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;
@GenTsModel
public class Float extends MetaField {



    public Float(String name, String caption, boolean notnull) {
		super(name, caption, notnull,MetaFieldType.FLOAT);
	}
	
	
	@Override
	public DataType<?> dataType() {
		return SQLDataType.FLOAT;
	}

	@Override
	protected void exValidate(Object data) throws MetaFieldValidatorException {
		if (!(data instanceof java.lang.Float)) {
			throw new MetaFieldValidatorException(getName() + "=" + data + " is not a valid Float type");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Field<Float> toJooqField() {
		return (Field<Float>) super.toJooqField();
	}


}

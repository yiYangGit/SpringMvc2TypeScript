package example.dev.meta.fields;

import com.blueveery.springrest2ts.filter.GenTsModel;
import example.dev.validator.MetaFieldValidatorException;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;
@GenTsModel
public class Bool extends MetaField {


    public Bool(String name, String caption, boolean notnull) {
		super(name, caption, notnull,MetaFieldType.BOOL);
	}

	
	@Override
	public DataType<?> dataType() {
		return SQLDataType.BOOLEAN;
	}
	
	@Override
	protected void exValidate(Object data) throws MetaFieldValidatorException {
		if (!(data instanceof Boolean)) {
			throw new MetaFieldValidatorException(getName() + "=" + data + " is not a valid boolean type");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Field<Boolean> toJooqField() {
		return (Field<Boolean>) super.toJooqField();
	}


}

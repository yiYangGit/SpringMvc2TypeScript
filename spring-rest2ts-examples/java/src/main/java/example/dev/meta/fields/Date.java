package example.dev.meta.fields;

import com.blueveery.springrest2ts.filter.GenTsModel;
import example.dev.validator.MetaFieldValidatorException;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;

/**
 * 日期类型，底层为 long 类型
 * @author weir
 *
 */
@GenTsModel
public class Date extends MetaField {

    public Date(String name, String caption, boolean notnull) {
		super(name, caption, notnull,MetaFieldType.DATE);
	}
	
	
	@Override
	public DataType<?> dataType() {
		return SQLDataType.BIGINT;
	}


	@Override
	protected void exValidate(Object data) throws MetaFieldValidatorException {
		if (!(data instanceof Long)) {
			throw new MetaFieldValidatorException(getName() + "=" + data + " is not a valid Date type");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Field<Long> toJooqField() {
		return (Field<Long>) super.toJooqField();
	}


}

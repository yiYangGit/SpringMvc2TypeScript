package example.dev.meta.fields;

import com.blueveery.springrest2ts.filter.GenTsModel;
import example.dev.validator.MetaFieldValidatorException;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;

/**
 * 日期时间类型，底层为 long 类型
 * @author weir
 *
 */
@GenTsModel
public class DateTime extends MetaField {


	public DateTime(String name, String caption, boolean notnull) {
        super(name, caption, notnull, MetaFieldType.DATETIME);
	}
	
	
	@Override
	public DataType<?> dataType() {
		return SQLDataType.BIGINT;
	}

	@Override
	protected void exValidate(Object data) throws MetaFieldValidatorException {
		if (!(data instanceof Long)) {
			throw new MetaFieldValidatorException(getName() + "=" + data + " is not a valid DateTime type");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Field<Long> toJooqField() {
		return (Field<Long>) super.toJooqField();
	}

}

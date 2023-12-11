package example.dev.meta.fields;

import com.blueveery.springrest2ts.filter.GenTsModel;
import example.dev.validator.MetaFieldValidatorException;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;

/**
 * 对应数据库Text/Clob类型，无长度限制
 */
@GenTsModel
public class Text extends MetaField {

    public Text(String name, String caption, boolean notnull) {
        super(name, caption, notnull,MetaFieldType.TEXT);
    }


    @Override
    public DataType<?> dataType() {
        return SQLDataType.CLOB;
    }
    
	@SuppressWarnings("unchecked")
	@Override
	public Field<String> toJooqField() {
		return (Field<String>) super.toJooqField();
	}


	@Override
	protected void exValidate(Object data) throws MetaFieldValidatorException {
		// ignore
	}

}

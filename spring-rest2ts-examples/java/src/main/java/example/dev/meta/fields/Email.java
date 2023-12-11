package example.dev.meta.fields;

import com.blueveery.springrest2ts.filter.GenTsModel;
import example.dev.validator.MetaFieldValidatorException;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;

import java.util.regex.Pattern;
@GenTsModel
public class Email extends MetaField {


    private static Pattern patEmail = Pattern.compile("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$");
	
	public Email(String name, String caption, boolean notnull) {
		super(name, caption, notnull,MetaFieldType.EMAIL);
	}
	
	
	@Override
	public DataType<?> dataType() {
		return SQLDataType.VARCHAR(64);
	}


	@Override
	protected void exValidate(Object data) throws MetaFieldValidatorException {
		if (!(data instanceof String)) {
			throw new MetaFieldValidatorException(getName() + "=" + data + " is not a valid String");
		}
		
		String v = (String)data;
	    if (!patEmail.matcher(v).matches()) {
	    	throw new MetaFieldValidatorException(getName() + "=" + data + " is not a valid Email type");
	    }
	}

	@SuppressWarnings("unchecked")
	@Override
	public Field<String> toJooqField() {
		return (Field<String>) super.toJooqField();
	}

}

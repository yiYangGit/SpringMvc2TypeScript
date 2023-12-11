package example.dev.meta.fields;

import com.blueveery.springrest2ts.filter.GenTsModel;
import example.dev.validator.MetaFieldValidatorException;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;

import java.util.regex.Pattern;
@GenTsModel
public class Ipv4 extends MetaField {
	private static Pattern ptnIpv4 = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");

    public Ipv4(String name, String caption, boolean notnull) {
		super(name, caption, notnull,MetaFieldType.IPV4);
	}
	
	
	@Override
	public DataType<?> dataType() {
		return SQLDataType.VARCHAR(32);
	}

	@Override
	protected void exValidate(Object data) throws MetaFieldValidatorException {
		if (!(data instanceof String)) {
			throw new MetaFieldValidatorException(getName() + "=" + data + " is not a valid String");
		}
		
		 String v = (String)data;
	     if (!ptnIpv4.matcher(v).matches()) {
	    	 throw new MetaFieldValidatorException(getName() + "=" + data + " is not a valid Ipv4 type");
	     }
	}

	@SuppressWarnings("unchecked")
	@Override
	public Field<String> toJooqField() {
		return (Field<String>) super.toJooqField();
	}

}

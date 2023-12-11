package example.dev.meta.fields;

import com.blueveery.springrest2ts.filter.GenTsModel;
import example.dev.validator.MetaFieldValidatorException;
import org.jooq.DataType;

/**
 * Created by yangyi0 on 2021/6/1.
 * 不是 boolean String number 类型的  Array类型
 */
@GenTsModel
public class ArrayField extends MetaField {

    public ArrayField(String name, String caption, boolean notnull) {
        super(name, caption, notnull, MetaFieldType.ARRAY);
    }

    @Override
    public DataType<?> dataType() {
        return null;
    }

    @Override
    protected void exValidate(Object data) throws MetaFieldValidatorException {

    }
}

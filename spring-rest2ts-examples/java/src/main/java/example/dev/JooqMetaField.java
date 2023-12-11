package example.dev;

import org.jooq.Field;


/**
 * Created by yangyi on 2021/2/3.
 */
public interface JooqMetaField extends MetaFieldProvider {
    Field<?> getJooqField();
}

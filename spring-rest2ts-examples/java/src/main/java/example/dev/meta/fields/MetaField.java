package example.dev.meta.fields;


import example.dev.MetaFieldProvider;
import example.dev.validator.MetaFieldValidatorException;
import example.dev.anno.BooleanType;
import example.dev.anno.MetaFiledInfo;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;

public abstract class MetaField {
	private String name;
	private String caption;
	private boolean notnull;
	private boolean pk = false;
    private final MetaFieldType metaFieldType;

    public final MetaFieldType getMetaFieldType() {
        return metaFieldType;
    }


    public final void setMetaFieldType(MetaFieldType metaFieldType) {
        throw new UnsupportedOperationException();
    }


    public String getName() {
        return name;
    }

    public MetaField setName(String name) {
        this.name = name;
        return this;
    }

    public String getCaption() {
        return caption;
    }

    public MetaField setCaption(String caption) {
        this.caption = caption;
        return this;
    }

    public boolean getNotnull() {
        return notnull;
    }

    public MetaField setNotnull(boolean notnull) {
        this.notnull = notnull;
        return this;
    }

    public boolean getPk() {
        return pk;
    }

    public MetaField setPk(boolean pk) {
        this.pk = pk;
        return this;
    }




    /**
     * 使用反射获取 clazz 中使用 @MetaFiledInfo 注解标识的metafield 并转成 MetaFieldSchema
     * 前端可能需要此参数 动态渲染表单
     *
     * @param clazz
     * @return
     */
    public static List<MetaField> fromClass(Class clazz) {
        ArrayList<MetaField> metaFieldSchemas = new ArrayList<>();
        java.lang.reflect.Field[] declaredFields = clazz.getDeclaredFields();
        for (java.lang.reflect.Field declaredField : declaredFields) {
            MetaFiledInfo annotation = declaredField.getAnnotation(MetaFiledInfo.class);
            if (annotation != null) {
                MetaField metaFieldSchema = getMetaFieldFromAnnotation(annotation);
                metaFieldSchemas.add(metaFieldSchema);
            }
        }
        return metaFieldSchemas;
    }

    public static MetaField getMetaFieldFromAnnotation(MetaFiledInfo annotation) {
        MetaField metaFieldSchema = null;
        MetaFieldProvider metaFieldProvider;
        try {
            metaFieldProvider = annotation.metaInfo().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        metaFieldSchema = metaFieldProvider.getMetaField();
        BooleanType pk = annotation.pk();
        if (pk != BooleanType.NONE) {
            metaFieldSchema.setPk(pk.getBooleanType());
        }
        BooleanType notnull = annotation.notnull();
        if (notnull != BooleanType.NONE) {
            metaFieldSchema.setNotnull(notnull.getBooleanType());
        }
        return metaFieldSchema;
    }

    public MetaField(String name, String caption, boolean notnull,MetaFieldType metaFieldType) {
		this.name = name;
		this.caption = caption;
		this.notnull = notnull;
        this.metaFieldType = metaFieldType;
	}

    public MetaField(String name, String caption, boolean notnull, boolean pk,MetaFieldType metaFieldType) {
        this.name = name;
        this.caption = caption;
        this.notnull = notnull;
        this.pk = pk;
        this.metaFieldType = metaFieldType;
    }
	
	abstract public DataType<?> dataType();
	
	/**
	 * 校验数据对象
	 * @param data
	 * @throws Exception
	 */
	public void validate(Object data) throws MetaFieldValidatorException {
		if (data == null) {
			if (this.getNotnull()) {
				throw new MetaFieldValidatorException(getCaption() + " can't be null");
			}
			return;
		}
		// 子类校验
		exValidate(data);
	}
	
	abstract protected void exValidate(Object data) throws MetaFieldValidatorException;
	

	
	// 转换为jooq Field
	public Field<?> toJooqField() {
		Field<?> jField = DSL.field(DSL.name(name), dataType(), DSL.comment(caption));
		// 注册到Register
		return jField;
	}


}

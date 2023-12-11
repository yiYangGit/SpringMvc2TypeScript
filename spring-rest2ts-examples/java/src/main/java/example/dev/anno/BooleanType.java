package example.dev.anno;

/**
 * Created by yangyi0 on 2021/6/4.
 * 用来在注解上标识True False 或者 None 不选择
 */
public enum BooleanType {
    /**
     * 对应 Boolean.TRUE
     */
   TRUE(Boolean.TRUE),
    /**
     * 对应 Boolean.FALSE
     */
    FALSE(Boolean.FALSE),
    /**
     * 相当于null
     */
    NONE(null);

    private final Boolean booleanType;

    public Boolean getBooleanType() {
        return booleanType;
    }

    BooleanType(Boolean booleanType) {
        this.booleanType = booleanType;
    }
}

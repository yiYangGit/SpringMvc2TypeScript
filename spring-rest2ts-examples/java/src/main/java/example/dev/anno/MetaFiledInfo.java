package example.dev.anno;

import example.dev.MetaFieldProvider;

import java.lang.annotation.*;

/**
 * @author yangyi
 */
@Documented
@Retention(RetentionPolicy.RUNTIME) //相当于作用时期，比如：运行期、编译期
@Target({ElementType.FIELD,ElementType.TYPE})
/**
 * 定义的meta信息,用来标记字段对应前端的模型展示信息,是时间,字符串,邮件,文本框等前端属性
 * 方便前端根据这些信息做视图展示使用
 */
public @interface MetaFiledInfo {

    Class<? extends MetaFieldProvider> metaInfo();

    /**
     * 可以覆写 metaInfo中的 pk属性
     * @return
     */
    BooleanType pk() default BooleanType.NONE;

    /**
     * 可以覆写 metaInfo中的 notnull属性
     * @return
     */
    BooleanType notnull() default BooleanType.NONE;
}

package example.dev.anno;


import java.lang.annotation.*;

@Documented //会被javadoc命令识别
@Retention(RetentionPolicy.RUNTIME) //相当于作用时期，比如：运行期、编译期
@Target({ElementType.TYPE}) //相当于作用域,比如方法、类
/**
 * 定义的meta表，用于保留比 JOOQ 更多的信息，比如 field caption、字段的高级类型（Email、IP）等
 * 理论上，工具可根据所有meta表生成数据库，进而由 JOOQ 自动生成 db 类
 * @author weir
 *
 */
public @interface MetaTable {

}

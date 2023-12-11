package example.dev.meta;

import example.dev.meta.fields.MetaField;
import org.jooq.Table;

import java.util.List;

/**
 * Table资源接口
 * @author weir
 *
 */
public interface ITable {
	
	// 返回表名
	public Table table();
	
	// 返回所有字段
	public List<MetaField> getAllFields();

}

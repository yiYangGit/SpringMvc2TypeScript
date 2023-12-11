package example.biz.core;

import example.dev.meta.fields.MetaField;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by yangyi on 2021/2/4.
 * 新增接口
 */
@Controller
public interface InsertObj<I> {
    /**
     * 获取insert schema
     *
     * @return
     */
    @RequestMapping(value = "getInsertFieldSchema")
    @ResponseBody
    List<MetaField> getInsertFieldSchema();

    /**
     * 插入表对象
     *
     * @param insertObj: 提交的form数据， fieldname -> object
     * @throws Exception
     */
    @RequestMapping(value = "insert")
    @ResponseBody
    void insert(@RequestBody I insertObj) throws Exception;

}

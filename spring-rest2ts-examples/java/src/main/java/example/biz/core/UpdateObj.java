package example.biz.core;

import example.dev.meta.fields.MetaField;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by yangyi on 2021/2/4.
 * 根据id修改某条数据
 */
@Controller
public interface UpdateObj <U>{
    /**
     * 更新表对象
     *
     * @param keyID
     * @param dataModel: 提交的form数据， fieldname -> object
     * @throws Exception
     */
    @RequestMapping(value = "update/{keyID}")
    @ResponseBody
    void update(@PathVariable(value = "keyID",required = true)long keyID, @RequestBody U dataModel) throws Exception;

    /**
     * 获取update schema
     *
     * @return
     */
    @RequestMapping(value = "getUpdateFieldSchema")
    @ResponseBody
    List<MetaField> getUpdateFieldSchema();

    /**
     * 根据ID获取一条update记录，返回{key -> val }的json格式
     * 用于前端update表单的数据回填
     *
     * @param keyID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "getByID/{keyID}")
    @ResponseBody
    U getByID(@PathVariable(value = "keyID",required = true)long keyID) throws Exception;

}

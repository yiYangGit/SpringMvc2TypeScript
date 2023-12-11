package example.biz.core;

import example.dev.meta.fields.MetaField;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by yangyi on 2021/2/4.
 * 查看一条数据详情
 */
@Controller
public interface DetailObj<D> {
    /**
     * 根据ID获取一条query记录，返回{key -> val }的json格式
     * 用于查看详情表单的数据回填
     *
     * @param keyID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "queryByID")
    @ResponseBody
    D queryByID(@RequestBody long keyID) throws Exception;

    /**
     * 获取update schema
     *
     * @return
     */
    @RequestMapping(value = "getDetailFieldSchema")
    @ResponseBody
    List<MetaField> getDetailFieldSchema();

}

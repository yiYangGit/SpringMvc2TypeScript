package example.biz.core;

import example.biz.TbCond;
import example.dev.meta.fields.MetaField;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by yangyi on 2021/2/4.
 * 分页查询某些数据
 */
@Controller
public interface QueryObj<Q> {

    /**
     * 获取查询schema
     *
     * @return
     */
    @RequestMapping(value = "getQCondFieldSchema")
    @ResponseBody
    List<MetaField> getQueryCondFieldSchema();

    /**
     * 获取记录 count(*)
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "getTotal")
    @ResponseBody
    long getTotal(@RequestBody List<TbCond> conds) throws Exception;


    /**
     * 获取列表展示 FieldSchema
     *
     * @return
     */
    @RequestMapping(value = "getQueryFieldSchema")
    @ResponseBody
    List<MetaField> getQueryFieldSchema();

    /**
     * 查询表对象结果
     *
     * @param start   开始位置（分页用）
     * @param max     最多返回（分页用）
     * @param orderBy 排序对象，可为null
     * @param bAsc
     * @param conds   条件
     * @return
     * @throws Exception
     */
    @PostMapping(value = "query/{start}/{max}/{orderBy}/{bAsc}")
    @ResponseBody
    List<Q> query(@PathVariable(value = "start",required = true) long start, @PathVariable(value = "max",required = true) int max, @PathVariable(value = "orderBy",required = true)String orderBy, @PathVariable(value = "bAsc",required = true)boolean bAsc, @RequestBody List<TbCond> conds)
            throws Exception;

}

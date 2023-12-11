package example.biz.core;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @param <Q> query 查询一条数据的泛型 model类
 * @param <I> insert 新增一条数据的泛型 model类
 * @param <U> update 修改一条数据的泛型 model类
 * @param <D> detail 查看一天数据详情的model类
 */
@Controller
public interface ITableObj<I,U,Q,D> extends InsertObj<I>,UpdateObj<U>,QueryObj<Q>,DetailObj<D>  {



    /**
     * 删除表对象
     *
     * @param keyIDs
     * @throws Exception
     */
    @RequestMapping(value = "delete")
    @ResponseBody
    void delete(@RequestBody List<Long> keyIDs) throws Exception;
}

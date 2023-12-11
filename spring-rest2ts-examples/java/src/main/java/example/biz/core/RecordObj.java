package example.biz.core;

import example.dev.meta.fields.MetaField;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by yangyi0 on 2021/2/6.
 * record 单挑数据的管理
 */
@Controller
public interface RecordObj<R> {

    @ResponseBody
    @RequestMapping(value = "recordObjGet")
    public R get();

    @ResponseBody
    @RequestMapping(value = "recordObjSet")
    public void set(@RequestBody R s);

    @ResponseBody
    @RequestMapping(value = "recordGetSchemas")
    public List<MetaField> getSchemas();

}

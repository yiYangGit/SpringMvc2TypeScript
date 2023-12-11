package example.biz;

import com.blueveery.springrest2ts.filter.GenTsField;
import example.biz.core.ITableObj;
import example.dev.meta.fields.MetaField;
import example.model.OrderDTO;
import example.model.AddressDTO;
import example.model.PersonDTO;
import com.blueveery.springrest2ts.filter.GenTsController;
import example.model.enums.OrderPaymentStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yangyi0 on 2021/1/26.
 */
@RestController
@GenTsController
@RequestMapping(value = "download")
public class TestUserManager implements ITableObj<OrderDTO, PersonDTO, AddressDTO, AddressDTO> {

    @GenTsField()
    public static final String h = "hhhh";
    @GenTsField()
    public static final String asda = "asda";

    @GenTsField()
    public static final List<TestUser3<Integer>> objs = new ArrayList<>();

    static {
        TestUser3<Integer> e = new TestUser3<>();
        e.setT3(1);
        objs.add(e);
        objs.add(e);
    }
    /**
     * 声明一个函数
     *
     * @param req
     * @return
     */
    @RequestMapping("/getByOwn")
    public Map<String,UserProfileDTO> getByOwn(@RequestBody @Validated Map<String,UserProfileDTO> req) {
        return req;
    }
    @RequestMapping("/getByOwn2")
    public Map<String, OrderPaymentStatus> getByOwn2(@RequestBody Map<String,OrderPaymentStatus> req) {
        return req;
    }

    @Override
    public AddressDTO queryByID(long keyID) throws Exception {
        return null;
    }

    @Override
    public List<MetaField> getDetailFieldSchema() {
        return null;
    }

    @Override
    public void delete(List<Long> keyIDs) throws Exception {

    }

    @Override
    public List<MetaField> getInsertFieldSchema() {
        return null;
    }

    @Override
    public void insert(OrderDTO insertObj) throws Exception {

    }

    @Override
    public List<MetaField> getQueryCondFieldSchema() {
        return null;
    }

    @Override
    public long getTotal(List<TbCond> conds) throws Exception {
        return 0;
    }

    @Override
    public List<MetaField> getQueryFieldSchema() {
        return null;
    }

    @Override
    public List<AddressDTO> query(long start, int max, String orderBy, boolean bAsc, List<TbCond> conds) throws Exception {
        return null;
    }

    @Override
    public void update(long keyID, PersonDTO dataModel) throws Exception {

    }

    @Override
    public List<MetaField> getUpdateFieldSchema() {
        return null;
    }

    @Override
    public PersonDTO getByID(long keyID) throws Exception {
        return null;
    }
}

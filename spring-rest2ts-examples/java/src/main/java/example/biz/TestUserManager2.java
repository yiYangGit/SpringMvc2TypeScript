package example.biz;

import com.blueveery.springrest2ts.filter.GenTsController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by yangyi0 on 2021/2/6.
 */
@Controller
@GenTsController
@RequestMapping(value = "TestUserManager2")
public class TestUserManager2 extends TestUserManager {

}

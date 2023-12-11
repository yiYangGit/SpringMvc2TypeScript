package example.config;

import example.dev.validator.MetaFieldValidatorException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class ExceptionsHandler {

    /**
     * 处理表单参数校验异常
     * @param exception
     * @param response
     * @throws IOException
     */
    @ExceptionHandler(MetaFieldValidatorException.class)//可以直接写@ExceptionHandler,不指明异常类，会自动映射
    public void customGenericExceptionHandler(MetaFieldValidatorException exception, HttpServletResponse response) throws IOException { //还可以声明接收其他任意参数
        PrintWriter writer = response.getWriter();
        try {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write(exception.getMessage());
        }finally {
            writer.close();
        }
    }


}
package cn.lanyue.cas.core.exception;


import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import cn.lanyue.cas.core.utils.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title: ExceptionAdvice</p>
 * <p>Description: 统一异常处理中心</p>
 * @author lanyue
 * @date 2020年02月9日
 * @version 1.0
 */
@ControllerAdvice
public class ExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

    /**
     * 运行时异常
     * @param ex
     * @param response
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    //@CrossOrigin(maxAge=3600,origins="*",methods={RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
    public BaseResponse handleException(Exception ex, HttpServletResponse response) {
        //设置跨域
        response.setHeader("Access-Control-Allow-Origin", "*");
        ex.printStackTrace();
        LOGGER.error(Exceptions.getStackTraceAsString(ex));
        return new ObjectRestResponse(ExceptionEnum.OPERATION_FAILED, ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public BaseResponse handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpServletResponse response) {
        //设置跨域
        response.setHeader("Access-Control-Allow-Origin", "*");
        ex.printStackTrace();
        String stackTraceAsString = Exceptions.getStackTraceAsString(ex);
        LOGGER.error(stackTraceAsString);
        return new ObjectRestResponse(ExceptionEnum.PARAM_ERR, stackTraceAsString);
    }

    //处理参数校验异常
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Object methodArgumentNotValidHandler(HttpServletResponse response, MethodArgumentNotValidException ex) {
        response.setStatus(200);
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorMessage.append(error.getField()).append(error.getDefaultMessage()).append(";");
        }
        String message = errorMessage.subSequence(0, errorMessage.length() - 1).toString();
        LOGGER.error(message, ex);
        return new ObjectRestResponse(ExceptionEnum.PARAM_ERR, message);
    }
}

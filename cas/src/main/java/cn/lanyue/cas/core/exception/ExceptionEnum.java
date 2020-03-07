package cn.lanyue.cas.core.exception;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/18 10:07
 */
public enum ExceptionEnum {
    /**公共异常*/
    PARAM_ERR(3001,"参数错误"),
    REGISTER_ERR(3002,"注册失败"),
    OPERATION_FAILED(3003,"操作失败"),
    UPLOAD_FILE_FAILED(3004,"上传失败"),
    POSTER_FAILED(3005,"海报制作失败"),
    NO_AUTHORITY(3006,"无权限"),
    MOBILE_PHONE_ERR(3007,"手机号码格式错误"),
    DELETE_SUC(3008,"删除成功"),
    ADD_REPEAT_ERR(3009,"重复添加"),

    /**业务异常*/
    SEND_SMS_ERR(4001, "短信发送失败，请稍后重试"),
    VERIFY_SMS_ERR(4002, "验证码不通过"),
    ;



    private int status;

    private String message;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    ExceptionEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }
}

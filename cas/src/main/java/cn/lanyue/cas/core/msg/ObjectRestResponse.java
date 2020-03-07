package cn.lanyue.cas.core.msg;

import cn.lanyue.cas.core.exception.ExceptionEnum;

/**
 * Created by Ace on 2018/6/11.
 */
public class ObjectRestResponse<T> extends BaseResponse {
    T data;

    public ObjectRestResponse data(T data) {
        this.setData(data);
        return this;
    }

    public ObjectRestResponse() {
    }

    public ObjectRestResponse(ExceptionEnum exceptionEnum) {
        this.setStatus(exceptionEnum.getStatus());
        this.setMessage(exceptionEnum.getMessage());
    }

    public ObjectRestResponse(ExceptionEnum exceptionEnum, T data) {
        this.setStatus(exceptionEnum.getStatus());
        this.setMessage(exceptionEnum.getMessage());
        this.setData(data);
    }

    

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}

package cn.lanyue.cas.core.msg;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by ace on 2018/8/23.
 */
public class BaseResponse {
	private int status = 200;
	private String message;

	public BaseResponse(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public BaseResponse(String message) {
		this.message = message;
	}

	public BaseResponse() {
	}

	public String getMessage() {
		return message;
	}

	public BaseResponse setMessage(String message) {
		this.message = message;
		return this;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@JsonIgnore
	public static BaseResponse success() {
		return new BaseResponse();
	}

	@JsonIgnore
	public static BaseResponse success(String message) {
		return new BaseResponse(message);
	}

	@JsonIgnore
	public static BaseResponse fail() {
		BaseResponse response = new BaseResponse();
		response.setStatus(500);
		return response;
	}

	@JsonIgnore
	public static BaseResponse fail(String message) {
		BaseResponse response = new BaseResponse(message);
		response.setStatus(500);
		return response;
	}

	public boolean isSuccess() {
		return this.status == 200;
	}

}

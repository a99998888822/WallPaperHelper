package com.example.octopus.wallpaperhelper.entity.vo;

public class BaseVO<T> {
	private String stringData;
	private String message;
	private boolean success = true;
	private T t;
	//状态码
	private String status = "0000";
	public String getStringData() {
		return stringData;
	}
	public void setStringData(String stringData) {
		this.stringData = stringData;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public T getData() {
		return t;
	}
	public void setData(T t) {
		this.t = t;
	}
}

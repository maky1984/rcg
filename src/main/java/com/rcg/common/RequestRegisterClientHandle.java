package com.rcg.common;

public class RequestRegisterClientHandle extends ClientRequest {

	private String msg;
	
	public RequestRegisterClientHandle() {
		super(RequestRegisterClientHandle.class.getName());
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getMsg() {
		return msg;
	}
	
}

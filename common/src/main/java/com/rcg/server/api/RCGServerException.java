package com.rcg.server.api;

public class RCGServerException extends Exception {

	public static final int CONNECTION_ERROR = 1;
	
	private int code;
	
	public RCGServerException(int code, String msg) {
		super(msg);
		this.code = code;
	}
	
	public RCGServerException(int code) {
		this(code, null);
	}
	
	public int getCode() {
		return code;
	}
	
}

package com.rcg.server.impl;

class Header {

	private long uid;
	private long code;
	private long messageSize;
	private long reserved1;
	private long reserved2;
	private long reserved3;
	private long reserved4;

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getCode() {
		return code;
	}

	public void setCode(long code) {
		this.code = code;
	}

	public long getMessageSize() {
		return messageSize;
	}

	public void setMessageSize(long messageSize) {
		this.messageSize = messageSize;
	}

	public long getReserved1() {
		return reserved1;
	}

	public void setReserved1(long reserved1) {
		this.reserved1 = reserved1;
	}

	public long getReserved2() {
		return reserved2;
	}

	public void setReserved2(long reserved2) {
		this.reserved2 = reserved2;
	}

	public long getReserved3() {
		return reserved3;
	}

	public void setReserved3(long reserved3) {
		this.reserved3 = reserved3;
	}

	public long getReserved4() {
		return reserved4;
	}

	public void setReserved4(long reserved4) {
		this.reserved4 = reserved4;
	}
	
}
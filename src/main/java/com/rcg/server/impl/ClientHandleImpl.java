package com.rcg.server.impl;

import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.Message;

public class ClientHandleImpl implements ClientHandle {

	boolean isFull;
	private long uid;
	private String host;
	private int port;
	
	private ClientHandleImpl(boolean isFull) {
		this.isFull = isFull;
	}
	
	public ClientHandleImpl(long uid, String host, int port) {
		this(true);
		this.uid = uid;
		this.host = host;
		this.port = port;
	}

	public ClientHandleImpl(long uid) {
		this(false);
		this.uid = uid;
	}

	@Override
	public AckStatus process(Message message) {
		System.out.println("Message:" + message);
		return AckStatus.OK;
	}

	@Override
	public long getUid() {
		return uid;
	}
	
	@Override
	public String getHost() {
		return host;
	}
	
	@Override
	public int getPort() {
		return port;
	}

}

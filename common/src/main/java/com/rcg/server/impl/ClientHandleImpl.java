package com.rcg.server.impl;

import java.util.ArrayList;
import java.util.List;

import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageHandler;

public class ClientHandleImpl implements ClientHandle {

	private List<MessageHandler> handlers = new ArrayList<>();
	
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
	public void addMessageHandler(MessageHandler messageHandler) {
		handlers.add(messageHandler);
	}
	
	@Override
	public void removeMessageHandler(MessageHandler messageHandler) {
		handlers.remove(messageHandler);
	}
	
	@Override
	public AckStatus process(Message message) {
		System.out.println("Message:" + message);
		MessageHandler[] handlers = this.handlers.toArray(new MessageHandler[0]);
		for (MessageHandler handler : handlers) {
			if (handler.accept(message, this)) {
				return AckStatus.OK;
			}
		}
		return AckStatus.UNEXPECTED_CLASS;
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
	
	@Override
	public void updateUid(long uid) {
		this.uid = uid;
	}

}

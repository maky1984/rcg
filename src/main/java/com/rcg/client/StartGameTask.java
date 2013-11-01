package com.rcg.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.common.RegisterClientHandleRequest;
import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageService;
import com.rcg.server.api.Task;
import com.rcg.server.impl.ClientHandleImpl;

public class StartGameTask implements Task {

	private final static Logger logger = LoggerFactory.getLogger(StartGameTask.class);
	
	private MessageService messageService;
	private ClientHandle clientHandle;

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}
	
	private void setClientHandle(ClientHandle clientHandle) {
		this.clientHandle = clientHandle;
	}
	
	public ClientHandle getClientHandle() {
		return clientHandle;
	}
	
	@Override
	public void run() {
		ClientHandle client = new ClientHandleImpl(123, "localhost", 47777);
		RegisterClientHandleRequest request = new RegisterClientHandleRequest();
		Message message = new Message();
		message.fillMessage(request);
		messageService.addClientHandle(client);
		messageService.send(client, message);
	}
	
}

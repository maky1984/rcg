package com.rcg.client;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.common.RegisterClientHandleRequest;
import com.rcg.common.RegisterClientHandleResponse;
import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageHandler;
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
		ClientHandle client = new ClientHandleImpl(UUID.randomUUID().getLeastSignificantBits(), "localhost", 47777);
		client.setMessageHandler(new MessageHandler() {
			@Override
			public boolean accept(Message message, ClientHandle caller) {
				RegisterClientHandleResponse response = message.unpackMessage();
				System.out.println("Response status:" + response.getStatus());
				//TODO 
				return true;
			}
		});
		RegisterClientHandleRequest request = new RegisterClientHandleRequest();
		Message message = new Message();
		message.fillMessage(request);
		messageService.addClientHandle(client);
		messageService.send(client, message);
	}
	
}

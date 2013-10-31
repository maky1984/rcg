package com.rcg.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.common.RegisterClientHandleRequest;
import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.ClientHandleManager;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageService;
import com.rcg.server.api.NewClientHandleListener;
import com.rcg.server.api.Task;
import com.rcg.server.impl.ClientHandleImpl;
import com.rcg.server.impl.ClientHandleManagerImpl;

public class StartGameTask implements Task {

	private final static Logger logger = LoggerFactory.getLogger(StartGameTask.class);
	
	private MessageService messageService;
	private ClientHandleManager clientHandleManager;
	private ClientHandle clientHandle;

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}
	
	public void setClientHandleManager(ClientHandleManager clientHandleManager) {
		this.clientHandleManager = clientHandleManager;
	}
	
	private void setClientHandle(ClientHandle clientHandle) {
		this.clientHandle = clientHandle;
	}
	
	public ClientHandle getClientHandle() {
		return clientHandle;
	}
	
	@Override
	public void run() {
		ClientHandle client = new ClientHandleImpl(ClientHandleManagerImpl.UNKNOWN_UID, "localhost", 47777);
		clientHandleManager.registerNewClientListener(new NewClientHandleListener() {
			@Override
			public void connected(ClientHandle handle) {
				logger.info("Client conected.");
				setClientHandle(handle);
			}
		});
		RegisterClientHandleRequest request = new RegisterClientHandleRequest();
		Message message = new Message();
		message.fillMessage(request);
		if (!messageService.send(client, message)) {
			logger.error("Cant send client handle request.");
		}
	}

}

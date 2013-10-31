package com.rcg.server.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.common.RegisterClientHandleRequest;
import com.rcg.common.RegisterClientHandleResponse;
import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.ClientHandleManager;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageHandler;
import com.rcg.server.api.MessageService;
import com.rcg.server.api.NewClientHandleListener;
import com.rcg.server.api.Task;
import com.rcg.server.api.TaskExecutor;

public class ClientHandleManagerImpl implements ClientHandleManager {

	private static final Logger logger = LoggerFactory.getLogger(ClientHandleManagerImpl.class);

	public static final long UNKNOWN_UID = -1;

	private Map<Long, ClientHandle> handles = Collections.synchronizedMap(new HashMap<Long, ClientHandle>());
	private MessageService messageService;
	private TaskExecutor taskExecutor;
	private NewClientHandleListener newClientListener;
	
	public ClientHandleManagerImpl() {
	}
	
	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}
	
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}
	
	@Override
	public void registerNewClientListener(NewClientHandleListener listener) {
		newClientListener = listener;
	}
	
	@Override
	public ClientHandle getClientHandle(long uid) {
		ClientHandle handle = handles.get(uid);
		if (handle == null) {
			ClientHandle anonym = new ClientHandleImpl(UNKNOWN_UID);
			anonym.setMessageHandler(new MessageHandler() {
				@Override
				public boolean accept(Message message, final ClientHandle caller) {
					if (message.containsClass(RegisterClientHandleRequest.class)) {
						logger.info("Request recieved");
						RegisterClientHandleRequest request = message.unpackMessage();
						taskExecutor.addTask(new Task() {
							@Override
							public void run() {
								logger.info("Sending response");
								RegisterClientHandleResponse response = new RegisterClientHandleResponse();
								long newUid = UUID.randomUUID().getMostSignificantBits();
								response.setUid(newUid);
								caller.updateUid(newUid);
								caller.setMessageHandler(null);
								handles.put(newUid, caller);
								messageService.send(caller, new Message(response));
								newClientListener.connected(caller);
							}
						});
						return true;
					} else if (message.containsClass(RegisterClientHandleResponse.class)) {
						logger.info("Response recived");
						RegisterClientHandleResponse response = message.unpackMessage();
						handles.put(response.getUid(), caller);
						caller.updateUid(response.getUid());
						caller.setMessageHandler(null);
						newClientListener.connected(caller);
						return true;
					} else {
						logger.error("ERROR Unexpected class in the message recieved");
						return false;
					}
				}
			});
			handle = anonym;
		}
		return handle;
	}
}

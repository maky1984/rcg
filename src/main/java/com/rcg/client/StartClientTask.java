package com.rcg.client;

import com.rcg.server.api.ClientHandleManager;
import com.rcg.server.api.MessageService;
import com.rcg.server.api.Task;
import com.rcg.server.api.TaskExecutor;
import com.rcg.server.impl.ClientHandleManagerImpl;
import com.rcg.server.impl.MessageServiceImpl;
import com.rcg.server.impl.TaskExecutorImpl;

public class StartClientTask implements Task {

	private MessageService messageService = new MessageServiceImpl();
	private ClientHandleManager clientHandleManager = new ClientHandleManagerImpl();
	private TaskExecutor taskExecutor = new TaskExecutorImpl();
	private boolean isReady;
	
	public StartClientTask() {
	}
	
	public boolean isReady() {
		return isReady;
	}
	
	public TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}

	@Override
	public void run() {
		taskExecutor.start();
		messageService.init(clientHandleManager);
		clientHandleManager.setMessageService(messageService);
		clientHandleManager.setTaskExecutor(taskExecutor);
		messageService.open(47778);
		isReady = true;
	}
	
	public ClientHandleManager getClientHandleManager() {
		return clientHandleManager;
	}
	
	public MessageService getMessageService() {
		return messageService;
	}
}

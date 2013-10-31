package com.rcg.server;

import com.rcg.server.api.ClientHandleManager;
import com.rcg.server.api.MessageService;
import com.rcg.server.api.Task;
import com.rcg.server.api.TaskExecutor;
import com.rcg.server.impl.ClientHandleManagerImpl;
import com.rcg.server.impl.MessageServiceImpl;
import com.rcg.server.impl.TaskExecutorImpl;

public class StartServerTask implements Task {
	
	public static final int PORT = 47777;
	
	private TaskExecutor executor = new TaskExecutorImpl();

	private MessageService messageService = new MessageServiceImpl();
	
	private ClientHandleManager clientHandleManager = new ClientHandleManagerImpl();

	@Override
	public void run() {
		executor.start();
		messageService.init(clientHandleManager);
		clientHandleManager.setMessageService(messageService);
		clientHandleManager.setTaskExecutor(executor);
		messageService.open(PORT);
	}

}

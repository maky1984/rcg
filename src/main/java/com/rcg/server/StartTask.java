package com.rcg.server;

import com.rcg.server.api.MessageService;
import com.rcg.server.api.Task;
import com.rcg.server.api.TaskExecutor;
import com.rcg.server.impl.MessageServiceImpl;
import com.rcg.server.impl.TaskExecutorImpl;

public class StartTask implements Task {
	
	private TaskExecutor executor = new TaskExecutorImpl();

	private MessageService messageService = new MessageServiceImpl();

	@Override
	public void run() {
		messageService.setTaskExecutor(executor);
		executor.start();
		messageService.open();
	}

}

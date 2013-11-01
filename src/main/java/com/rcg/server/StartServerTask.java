package com.rcg.server;

import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageHandler;
import com.rcg.server.api.MessageService;
import com.rcg.server.api.Task;
import com.rcg.server.api.TaskExecutor;
import com.rcg.server.impl.MessageServiceImpl;
import com.rcg.server.impl.TaskExecutorImpl;

public class StartServerTask implements Task {

	public static final int PORT = 47777;

	private TaskExecutor executor = new TaskExecutorImpl();

	private MessageService messageService = new MessageServiceImpl();

	@Override
	public void run() {
		executor.start();
		messageService.setDefaultMessageHandler(new MessageHandler() {
			@Override
			public boolean accept(Message message, ClientHandle caller) {
				System.out.println("From new client received: " + message);
				return true;
			}
		});
		messageService.open(PORT);
	}

}

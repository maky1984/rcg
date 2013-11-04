package com.rcg.server;

import com.rcg.common.RegisterClientHandleRequest;
import com.rcg.common.RegisterClientHandleResponse;
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
				RegisterClientHandleRequest request = message.unpackMessage();
				System.out.println("From request:" + request.getMsg());
				RegisterClientHandleResponse response = new RegisterClientHandleResponse();
				response.setStatus("OK");
				messageService.send(caller, new Message(response));
				return true;
			}
		});
		messageService.open(PORT);
	}

}

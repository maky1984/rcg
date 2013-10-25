package com.rcg.client.console;

import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageService;
import com.rcg.server.api.Task;
import com.rcg.server.impl.ClientHandleImpl;
import com.rcg.server.impl.ClientHandleManagerImpl;
import com.rcg.server.impl.MessageServiceImpl;

public class ConsoleClientRunner {

	
	
	private MessageService messageService;

	public ConsoleClientRunner() {
		messageService = new MessageServiceImpl();
		messageService.init(new ClientHandleManagerImpl());
		messageService.open(47778);
	}
	
	public void exec() {
		ClientHandle client = new ClientHandleImpl(123, "localhost", 47777);
		Message message = new Message() {
			
			private String msg = "Hello from client handle";

			@Override
			public boolean hasTask() {
				return false;
			}

			@Override
			public Task getTask() {
				return null;
			}

			@Override
			public long getSizeInBytes() {
				byte[] utf16Bytes = msg.getBytes();
				return utf16Bytes.length;
			}
		};
		if (!messageService.send(client, message)) {
			System.out.println(" Cant send. Maybe server is down.");
		} else {
			System.out.println("Sending..done");
		}
		messageService.stop();
	}

	public static void main(String[] args) {
		new ConsoleClientRunner().exec();
	}
}

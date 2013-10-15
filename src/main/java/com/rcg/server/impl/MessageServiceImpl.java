package com.rcg.server.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcg.server.api.MessageService;
import com.rcg.server.api.Task;
import com.rcg.server.api.TaskExecutor;

public class MessageServiceImpl implements MessageService, Runnable {

	public static final int PORT = 47777;

	private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

	private TaskExecutor executor;
	private ServerSocket serverSocket;
	private ObjectMapper mapper;
	private volatile boolean isStopped;

	@Override
	public void setTaskExecutor(TaskExecutor executor) {
		this.executor = executor;
	}

	@Override
	public void open() {
		logger.info("Openning message server with PORT=" + PORT);
		try {
			serverSocket = new ServerSocket(PORT);
			mapper = new ObjectMapper();
			mapper.enableDefaultTyping();
		} catch (IOException e) {
			logger.error("Cant create socket");
		}
	}
	
	@Override
	public void run() {
		logger.info("Message server opened");
		while (!isStopped) {
			try {
				Socket socket = serverSocket.accept();
				Task task = mapper.readValue(socket.getInputStream(), Task.class);
				executor.addTask(task);
			} catch (IOException e) {
				logger.error("Error during socket binding", e);
			}
		}
		logger.info("Message server stopped");
	}

	@Override
	public void stop() {
		isStopped = true;
	}
}

package com.rcg.server.api;

public interface MessageService {

	public void setTaskExecutor(TaskExecutor executor);
	
	public void open();
	
	public void stop();
}

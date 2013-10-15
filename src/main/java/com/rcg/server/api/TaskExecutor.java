package com.rcg.server.api;

public interface TaskExecutor {

	public void start();
	
	public void addTask(Task task);
	
	public void stop();
	
}

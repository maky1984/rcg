package com.rcg.server.api;

public interface ClientHandleManager {

	public ClientHandle getClientHandle(long uid);

	public void registerNewClientListener(NewClientHandleListener listener);
	
	public void setMessageService(MessageService messageService);
	
	public void setTaskExecutor(TaskExecutor taskExecutor);
	
}

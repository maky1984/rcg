package com.rcg.server.api;

public interface MessageService {

	public void init(ClientHandleManager clientHandleManager, MessageFactory messageFactory);

	public void open(int port);

	public ClientHandle[] getClients();

	public boolean send(ClientHandle client, Message message);

	public void stop();

}

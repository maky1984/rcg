package com.rcg.server.api;

public interface ClientHandle {

	public enum AckStatus {
		OK, DENIED, DENIED_SUM_CHECK, UNEXPECTED_CLASS;
	}

	/**
	 * Posts message to client.
	 * @param message
	 * @return Status of the receipt of the message 
	 */
	public AckStatus process(Message message);

	public void addMessageHandler(MessageHandler messageHandler);
	
	public void removeMessageHandler(MessageHandler messageHandler);

	public long getUid();
	
	public String getHost();
	
	public int getPort();
	
	public void updateUid(long uid);

}

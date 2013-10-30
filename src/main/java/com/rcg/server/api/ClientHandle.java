package com.rcg.server.api;

public interface ClientHandle {

	public enum AckStatus {
		OK, DENIED, DENIED_SUM_CHECK;
	}

	/**
	 * Posts message to client.
	 * @param message
	 * @return Status of the receipt of the message 
	 */
	public AckStatus process(Message message);

	public void setMessageHandler(MessageHandler messageHandler);
	
	public long getUid();
	
	public String getHost();
	
	public int getPort();

}

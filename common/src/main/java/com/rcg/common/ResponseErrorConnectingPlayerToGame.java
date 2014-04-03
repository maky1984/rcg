package com.rcg.common;

public class ResponseErrorConnectingPlayerToGame extends ClientResponse {

	private long playerId;
	private String name;
	private String status;

	public void setName(String name) {
		this.name = name;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public String getName() {
		return name;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}

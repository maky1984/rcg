package com.rcg.common;

public class RequestCreateGame extends ClientRequest {

	private String playerName;
	
	public RequestCreateGame() {
		super(RequestCreateGame.class.getName());
	}
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
}

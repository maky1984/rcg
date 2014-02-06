package com.rcg.common;

public class RequestConnectToGame extends ClientRequest {

	private String playerName;
	private long playerId;
	private long gameId;
	private long deckId;
	
	public RequestConnectToGame() {
		super(RequestConnectToGame.class.getName());
	}
	
	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	
	public long getPlayerId() {
		return playerId;
	}
	
	public long getGameId() {
		return gameId;
	}
	
	public void setGameId(long gameId) {
		this.gameId = gameId;
	}
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public void setDeckId(long deckId) {
		this.deckId = deckId;
	}
	
	public long getDeckId() {
		return deckId;
	}
	
}

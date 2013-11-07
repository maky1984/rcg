package com.rcg.common;

public class RequestConnectToGame extends ClientRequest {

	private String playerName;
	private long playerId;
	private boolean createNewGame;
	private long gameId;
	
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
	
	public boolean isCreateNewGame() {
		return createNewGame;
	}
	
	public void setCreateNewGame(boolean createNewGame) {
		this.createNewGame = createNewGame;
	}
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
}

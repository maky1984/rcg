package com.rcg.common;

public class ResponseConnectToGame extends ClientResponse {

	private long gameId;
	private String gameName;
	private String player1Name;
	private String player2Name;

	public void setGameId(long gameId) {
		this.gameId = gameId;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public void setPlayer1Name(String player1Name) {
		this.player1Name = player1Name;
	}

	public void setPlayer2Name(String player2Name) {
		this.player2Name = player2Name;
	}

	public long getGameId() {
		return gameId;
	}

	public String getGameName() {
		return gameName;
	}

	public String getPlayer1Name() {
		return player1Name;
	}

	public String getPlayer2Name() {
		return player2Name;
	}
}
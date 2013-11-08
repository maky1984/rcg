package com.rcg.common;

import java.util.List;

public class ResponseGameList extends ClientResponse {

	private List<GameView> games;
	
	public List<GameView> getGames() {
		return games;
	}
	
	public void setGames(List<GameView> games) {
		this.games = games;
	}
}

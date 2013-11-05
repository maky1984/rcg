package com.rcg.game.model.server;

import java.util.List;

public interface GameClub {

	public List<Game> getGames();
	
	public Game addGame(Player player);
	
}

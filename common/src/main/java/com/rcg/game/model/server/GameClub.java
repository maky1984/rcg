package com.rcg.game.model.server;

import java.util.List;

public interface GameClub {

	public List<Game> getGames();
	
	public Game createGameWithPlayer1(Player player);
	
	public Game getGame(long id);
	
	public Game connectPlayer2ToGame(long id, Player player);
}

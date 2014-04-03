package com.rcg.game.model.server;

import java.util.List;

public interface GameClub {

	public List<Game> getGames();
	
	public Game createGameWithPlayer1(Player player, long deckId);
	
	public Game getGame(long id);
	
	public Game getGameByPlayer(Player player);
	
	public Game connectPlayer2ToGame(long id, Player player, long deckId);
	
}

package com.rcg.game.model.server;

public interface PlayerBase {

	public Player getPlayerById(long id);
	
	public void addPlayer(Player player);
	
	public void removePlayer(Player player);
	
	public void updatePlayer(Player player);
	
	public void refresh();
	
}

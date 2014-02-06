package com.rcg.game.model.server;

public interface Game {

	public static final long EMPTY_GAME_ID = 0;
	
	public static final int HAND_SIZE = 6;

	public long getId();

	public String getName();
	
	public void open();
	
	public void setPlayer1(Player player, long deckId);
	
	public void setPlayer2(Player player, long deckId);
	
	public boolean isReadyForPlay();
	
	public Player getPlayer1();
	
	public Player getPlayer2();
	
}

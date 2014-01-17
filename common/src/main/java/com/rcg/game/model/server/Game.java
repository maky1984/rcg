package com.rcg.game.model.server;

public interface Game {

	public static final long EMPTY_GAME_ID = 0;
	
	public static final int HAND_SIZE = 6;

	public long getId();

	public String getName();
	
	public void open();
	
	public void add(Player player);
	
	public boolean isReadyForPlay();
	
	public String getPlayer1Name();
	
	public String getPlayer2Name();
	
}

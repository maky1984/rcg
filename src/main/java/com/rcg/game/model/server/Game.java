package com.rcg.game.model.server;

public interface Game {

	public static final int HAND_SIZE = 6;

	public void open();
	
	public void add(Player player);
	
}

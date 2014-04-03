package com.rcg.client.javafxapp;

public interface GameTableListener {

	public static final int WIN = 0;
	public static final int LOSE = 1;
	public static final int DRAW = 2;
	
	public void gameOver(int state);
	
}

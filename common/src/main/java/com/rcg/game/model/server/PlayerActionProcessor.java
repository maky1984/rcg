package com.rcg.game.model.server;

import com.rcg.game.model.Card;
import com.rcg.game.model.PlayerState;

public interface PlayerActionProcessor {

	public PlayerState getState();
	
	public void addListener(PlayerActionListener listener);
	
	public void removeListener(PlayerActionListener listener);
	
	public void initState();
	
	public void startTurn();
	
	public void endTurn();
	
	public void drawCards(int number);
	
	public void buildTower(int number);
	
	public void demolishTower(int number);

	public void buildWall(int number);
	
	public void demolishWall(int number);
	
	public void removeCardFromHand(Card card);
}

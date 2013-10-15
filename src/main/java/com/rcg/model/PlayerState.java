package com.rcg.model;

import java.util.List;

public interface PlayerState {
	
	public int getBricks();
	
	public int getGems();
	
	public int getRecruits();
	
	public int getQuarry();
	
	public int getMagic();
	
	public int getDungeon();
	
	public int getWall();
	
	public int getTower();
	
	public List<Card> getHand();
	
	public boolean hasTurn();
	
	public int getTurns();
	
}

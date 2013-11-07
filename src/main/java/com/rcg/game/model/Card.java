package com.rcg.game.model;

import java.util.List;

public interface Card {

	public String getName();
	
	public long getId();
	
	public List<Action> getActions();
	
	public CardCost getCost();
	
}

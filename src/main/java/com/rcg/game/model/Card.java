package com.rcg.game.model;

import java.util.List;

public interface Card {

	public List<Action> getActions();
	
	public CardCost getCost();
	
}

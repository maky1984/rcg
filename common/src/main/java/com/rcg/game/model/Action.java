package com.rcg.game.model;

import java.util.List;

import com.rcg.game.model.impl.PlayerState;


public interface Action {

	public int getType();
	
	public List<Integer> getValues();
	
	public void execute(PlayerState myState, PlayerState enemyState);
	
}

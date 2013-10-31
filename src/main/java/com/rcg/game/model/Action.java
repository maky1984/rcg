package com.rcg.game.model;

public interface Action {

	public void execute(PlayerState myState, PlayerState enemyState);
	
}

package com.rcg.common;

import com.rcg.game.model.PlayerState;

public class GameTableUpdate extends ServerMessage {

	private PlayerState ownState;
	private PlayerState enemyState;
	
	public void setEnemyState(PlayerState enemyState) {
		this.enemyState = enemyState;
	}
	
	public void setOwnState(PlayerState ownState) {
		this.ownState = ownState;
	}
	
	public PlayerState getEnemyState() {
		return enemyState;
	}
	
	public PlayerState getOwnState() {
		return ownState;
	}
	
}

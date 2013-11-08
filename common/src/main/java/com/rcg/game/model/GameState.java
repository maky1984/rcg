package com.rcg.game.model;

import com.rcg.game.model.impl.PlayerState;


public class GameState {

	private PlayerState firstPlayerState;
	private PlayerState secondPlayerState;
	
	public void setFirstPlayerState(PlayerState firstPlayerState) {
		this.firstPlayerState = firstPlayerState;
	}
	
	public void setSecondPlayerState(PlayerState secondPlayerState) {
		this.secondPlayerState = secondPlayerState;
	}

	public PlayerState getFirstPlayerState() {
		return firstPlayerState;
	}
	
	public PlayerState getSecondPlayerState() {
		return secondPlayerState;
	}
}

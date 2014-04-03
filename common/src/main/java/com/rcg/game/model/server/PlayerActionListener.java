package com.rcg.game.model.server;


public interface PlayerActionListener {

	public void towerDecreased(PlayerActionProcessor processor);
	
	public void towerDestroyed(PlayerActionProcessor processor);
	
	public void towerIncreased(PlayerActionProcessor processor);
	
	public void handIsEmpty(PlayerActionProcessor processor);
	
	public void towerIsFull(PlayerActionProcessor processor);
	
}

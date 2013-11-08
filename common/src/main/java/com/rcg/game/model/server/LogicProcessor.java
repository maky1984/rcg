package com.rcg.game.model.server;

import com.rcg.game.model.PlayerAction;

public interface LogicProcessor {

	public void stop();
	
	public void add(PlayerAction playerAction);
	
}

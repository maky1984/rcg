package com.rcg.model.server;

import com.rcg.model.PlayerAction;

public interface LogicProcessor {

	public void stop();
	
	public void add(PlayerAction playerAction);
	
}

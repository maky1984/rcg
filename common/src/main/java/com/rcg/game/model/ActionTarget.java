package com.rcg.game.model;

import com.rcg.game.model.server.PlayerActionProcessor;

public interface ActionTarget {

	public PlayerActionProcessor getProcessor();

	public Card getCard();

}

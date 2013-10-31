package com.rcg.game.model.server;

import com.rcg.game.model.Deck;
import com.rcg.server.api.ClientHandle;

public interface Player {

	public ClientHandle getClientHandle();
	
	public Deck getDeck();
	
	public void fillHand(Deck deck);
	
}

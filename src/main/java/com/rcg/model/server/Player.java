package com.rcg.model.server;

import com.rcg.model.Deck;
import com.rcg.server.api.ClientHandle;

public interface Player {

	public ClientHandle getClientHandle();
	
	public Deck getDeck();
	
	public void fillHand(Deck deck);
	
}

package com.rcg.game.model.server.impl;

import com.rcg.game.model.Deck;
import com.rcg.game.model.server.Player;
import com.rcg.server.api.ClientHandle;

public class PlayerImpl implements Player {

	private String name;
	private ClientHandle handle;
	
	public PlayerImpl(String name, ClientHandle handle) {
		this.name = name;
		this.handle = handle;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void fillHand(Deck deck) {
		// TODO
	}
	
	@Override
	public ClientHandle getClientHandle() {
		return handle;
	}
	
	@Override
	public Deck getDeck() {
		// TODO
		return null;
	}
	
}

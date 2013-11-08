package com.rcg.game.model.server.impl;

import java.util.List;

import com.rcg.game.model.Deck;
import com.rcg.game.model.server.Player;
import com.rcg.server.api.ClientHandle;

public class PlayerImpl implements Player {

	private String name;
	private long id;
	// List of card ids
	private List<Long> allCardIds;
	// List of deck ids
	private List<Long> allDeckIds;

	private Deck deck;
	private ClientHandle handle;

	public PlayerImpl(long id, String name, List<Long> allCardIds) {
		this.id = id;
		this.name = name;
		this.allCardIds = allCardIds;
	}

	@Override
	public List<Long> getAllCardIds() {
		return allCardIds;
	}
	
	public void setAllCardIds(List<Long> allCardIds) {
		this.allCardIds = allCardIds;
	}
	
	public List<Long> getAllDeckIds() {
		return allDeckIds;
	}
	
	public void setAllDeckIds(List<Long> allDeckIds) {
		this.allDeckIds = allDeckIds;
	}
	
	@Override
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void fillHand(Deck deck) {
		// TODO
	}

	public void setClientHandle(ClientHandle handle) {
		this.handle = handle;
	}
	
	@Override
	public ClientHandle getClientHandle() {
		return handle;
	}

	@Override
	public Deck getCurrentDeck() {
		return deck;
	}

}

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

	private ClientHandle handle;

	public PlayerImpl(long id, String name, List<Long> allCardIds, List<Long> allDeckIds) {
		this.id = id;
		this.name = name;
		this.allCardIds = allCardIds;
		this.allDeckIds = allDeckIds;
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

	public void setClientHandle(ClientHandle handle) {
		this.handle = handle;
	}
	
	@Override
	public ClientHandle getClientHandle() {
		return handle;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Player)) {
			return false;
		}
		Player otherPlayer = (Player)obj;
		if (otherPlayer.getId() == getId() && otherPlayer.getName().equals(getName())) {
			return true;
		}
		return false;
	}

}

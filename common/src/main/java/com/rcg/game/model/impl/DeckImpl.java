package com.rcg.game.model.impl;

import java.util.List;

import com.rcg.game.model.Card;
import com.rcg.game.model.Deck;
import com.rcg.game.model.server.CardBase;

public class DeckImpl implements Deck {

	private long id;
	private String name;
	private List<Long> allCardIds;
	
	private CardBase cardBase;
	
	public DeckImpl(long id, String name, List<Long> cardIds) {
		this.id = id;
		this.name = name;
		this.allCardIds = cardIds;
	}
	
	public void setCardBase(CardBase cardBase) {
		this.cardBase = cardBase;
	}
	
	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Long> getAllCardIds() {
		return allCardIds;
	}

	@Override
	public void shuffle() {
		// TODO 
	}

	@Override
	public Card drawNext() {
		// TODO 
		return null;
	}

	@Override
	public Card viewNext() {
		// TODO 
		return null;
	}

	@Override
	public boolean hasNext() {
		// TODO 
		return false;
	}

}

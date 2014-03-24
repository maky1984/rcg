package com.rcg.game.model;

import java.util.List;

import com.rcg.game.model.server.CardBase;

public interface Deck {

	public long getId();
	
	public String getName();
	
	public List<Long> getAllCardIds();

	public List<Card> getAllCards();
	
	public void setCardBase(CardBase cardBase);
	
}

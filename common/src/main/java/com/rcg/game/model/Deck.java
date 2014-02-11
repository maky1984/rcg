package com.rcg.game.model;

import java.util.List;

public interface Deck {

	public long getId();
	
	public String getName();
	
	public List<Long> getAllCardIds();

	public List<Card> getAllCards();
}

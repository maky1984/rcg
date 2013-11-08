package com.rcg.game.model;

import java.util.List;

public interface Deck {

	public long getId();
	
	public String getName();
	
	public List<Long> getAllCardIds();
	
	public void shuffle();
	
	public Card drawNext();
	
	public Card viewNext();
	
	public boolean hasNext();
	
}

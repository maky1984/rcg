package com.rcg.model;

public interface Deck {

	public void shuffle();
	
	public Card drawNext();
	
	public Card viewNext();
	
	public boolean hasNext();
	
}

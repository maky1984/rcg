package com.rcg.game.model;

public interface Deck {

	public void shuffle();
	
	public Card drawNext();
	
	public Card viewNext();
	
	public boolean hasNext();
	
}

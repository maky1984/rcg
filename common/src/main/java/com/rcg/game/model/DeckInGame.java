package com.rcg.game.model;

public interface DeckInGame {

	public void shuffle();
	
	public Card drawNext();
	
	public Card viewNext();
	
	public boolean hasNext();

}

package com.rcg.game.model.server.impl;

import java.util.Map;

import com.rcg.game.model.Deck;
import com.rcg.game.model.server.DeckBase;

public class DeckBaseImpl implements DeckBase {

	private Map<Long, Deck> decks;

	public DeckBaseImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addDeck(Deck deck) {
		// TODO Auto-generated method stub

	}

	@Override
	public Deck getDeckById(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeDeck(Deck deck) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDeck(Deck deck) {
		// TODO Auto-generated method stub

	}

	private void readDecks() {
		
	}
	
	private void writeDecks() {
		
	}
}

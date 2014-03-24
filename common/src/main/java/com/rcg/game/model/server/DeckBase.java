package com.rcg.game.model.server;

import com.rcg.game.model.Card;
import com.rcg.game.model.Deck;

public interface DeckBase {

	public Deck getDeckById(long id);

	public Card getCardById(long cardId);
	
	public void addDeck(Deck deck);

	public void removeDeck(Deck deck);

	public void updateDeck(Deck deck);

}

package com.rcg.game.model.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.rcg.game.model.Card;
import com.rcg.game.model.Deck;
import com.rcg.game.model.DeckInGame;

public class DeckInGameImpl implements DeckInGame {

	private LinkedList<Card> deck;

	public DeckInGameImpl(Deck baseDeck) {
		List<Card> cards = baseDeck.getAllCards();
		this.deck = new LinkedList<Card>();
		for (Card card : cards) {
			this.deck.add(card);
		}
	}

	@Override
	public void shuffle() {
		ArrayList<Card> tempDeck = new ArrayList<>();
		tempDeck.addAll(deck);
		LinkedList<Card> newDeck = new LinkedList<Card>();
		int size = tempDeck.size();
		for (int i = 0; i < size; i++) {
			int randIndex = (int) Math.round(Math.random() * tempDeck.size()) - 1;
			newDeck.add(tempDeck.remove(randIndex));
		}
		deck = newDeck;
	}

	@Override
	public Card drawNext() {
		return deck.poll();
	}

	@Override
	public Card viewNext() {
		return deck.getFirst();
	}

	@Override
	public boolean hasNext() {
		return deck.size() > 0;
	}

}

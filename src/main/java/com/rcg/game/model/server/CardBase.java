package com.rcg.game.model.server;

import java.util.List;

import com.rcg.game.model.Card;

public interface CardBase {

	public Card getCardById(long id);
	
	public void removeCard(Card card);
	
	public void addCard(Card card);

	public void addCard(Card... cards);

	public List<Card> getAllCards();
	
}

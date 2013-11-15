package com.rcg.game.model.server.impl;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.rcg.game.model.Deck;
import com.rcg.game.model.impl.DeckImpl;

public class DeckBaseImplTest {

	static Deck deck1 = new DeckImpl(11, "deck1", Arrays.asList(
			CardBaseImplTest.card1.getId(),
			CardBaseImplTest.card2.getId(),
			CardBaseImplTest.card3.getId(),
			CardBaseImplTest.card4.getId())
			);
	static Deck deck2 = new DeckImpl(13, "deck2", Arrays.asList(
			CardBaseImplTest.card4.getId(),
			CardBaseImplTest.card5.getId(),
			CardBaseImplTest.card6.getId(),
			CardBaseImplTest.card7.getId())
			);

	@Test
	public void testAddDeck() {
		DeckBaseImpl base1 = new DeckBaseImpl();
		base1.addDeck(deck1);
		base1.addDeck(deck2);

		DeckBaseImpl base2 = new DeckBaseImpl();
		Deck deck = base2.getDeckById(13);
		Assert.assertEquals(CardBaseImplTest.card7.getId(), deck.getAllCardIds().get(3).intValue());
	}
}

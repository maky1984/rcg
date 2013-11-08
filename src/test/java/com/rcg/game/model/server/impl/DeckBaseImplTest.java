package com.rcg.game.model.server.impl;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.rcg.game.model.Deck;
import com.rcg.game.model.impl.DeckImpl;

public class DeckBaseImplTest {

	@Test
	public void testAddDeck() {
		DeckBaseImpl base1 = new DeckBaseImpl();
		List<Long> ids1 = new ArrayList<>();
		ids1.add(new Long(1));
		ids1.add(new Long(2));
		ids1.add(new Long(3));
		ids1.add(new Long(4));
		Deck deck1 = new DeckImpl(11, "deck1", ids1);
		List<Long> ids2 = new ArrayList<>();
		ids2.add(new Long(7));
		ids2.add(new Long(8));
		ids2.add(new Long(9));
		ids2.add(new Long(10));
		Deck deck2 = new DeckImpl(13, "deck2", ids2);
		base1.addDeck(deck1);
		base1.addDeck(deck2);
		
		DeckBaseImpl base2 = new DeckBaseImpl();
		Deck deck = base2.getDeckById(13);
		Assert.assertEquals(10, deck.getAllCardIds().get(3).intValue());
	}
}

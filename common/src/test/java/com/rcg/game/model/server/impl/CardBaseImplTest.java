package com.rcg.game.model.server.impl;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.rcg.game.model.Action;
import com.rcg.game.model.Card;
import com.rcg.game.model.CardCost;
import com.rcg.game.model.impl.ActionImpl;
import com.rcg.game.model.impl.CardCostImpl;
import com.rcg.game.model.impl.CardImpl;

public class CardBaseImplTest {

	@Test
	public void test() {
		CardBaseImpl base1 = new CardBaseImpl();
		CardCost cost1 = new CardCostImpl(1, 2, 3);
		CardCost cost2 = new CardCostImpl(4, 5, 6);
		CardCost cost3 = new CardCostImpl(7, 8, 9);
		Action action1 = new ActionImpl(1, Arrays.asList(123, 321));
		Action action2 = new ActionImpl(2, Arrays.asList(4, 1));
		Action action3 = new ActionImpl(3, Arrays.asList(13, 31));
		Card card1 = new CardImpl(1, "Card1", cost1, Arrays.asList(action1));
		Card card2 = new CardImpl(2, "Card2", cost2, Arrays.asList(action2));
		Card card3 = new CardImpl(3, "Card3", cost3, Arrays.asList(action3));
		base1.addCard(card1, card2, card3);

		CardBaseImpl base2 = new CardBaseImpl();
		List<Card> cards = base2.getAllCards();
		Assert.assertArrayEquals(cards.toArray(new Card[0]), new Card[] { card1, card2, card3 });
	}

}

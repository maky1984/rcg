package com.rcg.game.model.server.impl;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.rcg.game.model.server.Player;
import com.rcg.game.model.server.PlayerBase;

public class PlayerBaseImplTest {

	static Player player1 = new PlayerImpl(1, "Player1",
			Arrays.asList(
					CardBaseImplTest.card1.getId(),
					CardBaseImplTest.card2.getId(),
					CardBaseImplTest.card3.getId()
					),
			Arrays.asList(
					DeckBaseImplTest.deck1.getId()
					)
			);
	static Player player2 = new PlayerImpl(2, "Player2",
			Arrays.asList(
					CardBaseImplTest.card1.getId(),
					CardBaseImplTest.card2.getId(),
					CardBaseImplTest.card3.getId()
					),
			Arrays.asList(
					DeckBaseImplTest.deck1.getId()
					)
			);

	@Test
	public void test() {
		PlayerBase playerBase = new PlayerBaseImpl("playersTest.xml");
		playerBase.addPlayer(player1);
		playerBase.addPlayer(player2);
		PlayerBase newPlayerBase = new PlayerBaseImpl("playersTest.xml");
		Player player = newPlayerBase.getPlayerById(player2.getId());
		Assert.assertEquals(player.getName(), player2.getName());
		Assert.assertEquals(player.getAllDeckIds().get(0).longValue(), player2.getAllDeckIds().get(0).longValue());
	}
}

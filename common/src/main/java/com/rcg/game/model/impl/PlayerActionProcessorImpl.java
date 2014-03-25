package com.rcg.game.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.rcg.game.model.Card;
import com.rcg.game.model.Deck;
import com.rcg.game.model.DeckInGame;
import com.rcg.game.model.PlayerActionListener;
import com.rcg.game.model.PlayerActionProcessor;
import com.rcg.game.model.PlayerState;
import com.rcg.game.model.RuleConstants;
import com.rcg.game.model.server.Player;

public class PlayerActionProcessorImpl implements PlayerActionProcessor {

	private PlayerState ownState;
	private DeckInGame deck;
	private List<PlayerActionListener> listeners;

	public PlayerActionProcessorImpl(Player player, Deck deck) {
		this.deck = new DeckInGameImpl(deck);
		this.ownState = new PlayerState();
		listeners = new ArrayList<>();
	}

	@Override
	public PlayerState getState() {
		return ownState;
	}

	@Override
	public void addListener(PlayerActionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(PlayerActionListener listener) {
		listeners.remove(listener);
	}

	private void notifyTowerIncreased() {
		for (PlayerActionListener listener : listeners) {
			listener.towerIncreased();
		}
	}

	private void notifyTowerDestroyed() {
		for (PlayerActionListener listener : listeners) {
			listener.towerDestroyed();
		}
	}

	private void notifyTowerDecreased() {
		for (PlayerActionListener listener : listeners) {
			listener.towerDecreased();
		}
	}

	@Override
	public void buildTower(int number) {
		int tower = ownState.getTower();
		ownState.setTower(tower + number);
		notifyTowerIncreased();
	}

	@Override
	public void buildWall(int number) {
		ownState.setWall(ownState.getWall() + number);
	}

	@Override
	public void demolishTower(int number) {
		int tower = ownState.getTower();
		if (tower <= number) {
			ownState.setTower(0);
			notifyTowerDestroyed();
		} else {
			ownState.setTower(tower - number);
			notifyTowerDecreased();
		}
	}

	@Override
	public void demolishWall(int number) {
		ownState.setWall(ownState.getWall() - number);
	};

	@Override
	public void drawCards(int number) {
		List<Long> hand = ownState.getHand();
		while (number > 0 && deck.hasNext() && hand.size() < RuleConstants.MAX_HAND_CARD_NUMBER) {
			Card card = deck.drawNext();
			if (card != null) {
				number--;
				hand.add(card.getId());
			}
		}
		ownState.setHand(hand);
	}

	@Override
	public void removeCardFromHand(Card card) {
		List<Long> hand = ownState.getHand();
		hand.remove(card.getId());
		ownState.setHand(hand);
	}
	
	@Override
	public void initState() {
		ownState.setBricks(RuleConstants.INIT_BRICKS);
		ownState.setGems(RuleConstants.INIT_GEMS);
		ownState.setRecruiters(RuleConstants.INIT_RECRUITERS);
		ownState.setDungeon(RuleConstants.INIT_DUNGEON);
		ownState.setMagic(RuleConstants.INIT_MAGIC);
		ownState.setQuarry(RuleConstants.INIT_QUARRY);
	}
	
	@Override
	public void startTurn() {
		ownState.setBricks(ownState.getBricks() + ownState.getQuarry());
		ownState.setGems(ownState.getGems() + ownState.getMagic());
		ownState.setRecruiters(ownState.getRecruiters() + ownState.getDungeon());
		drawCards(1);
		ownState.setHasTurn(PlayerState.HAS_TURN);
	}
	
	@Override
	public void endTurn() {
		ownState.setHasTurn(PlayerState.NO_TURN);
	}
	
	
}

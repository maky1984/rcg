package com.rcg.game.model.impl;

import java.util.List;

import com.rcg.game.model.Card;
import com.rcg.game.model.Deck;
import com.rcg.game.model.DeckInGame;
import com.rcg.game.model.PlayerActionListener;
import com.rcg.game.model.PlayerActionProcessor;
import com.rcg.game.model.PlayerState;
import com.rcg.game.model.server.Player;

public class PlayerActionProcessorImpl implements PlayerActionProcessor {

	private PlayerState ownState;
	private DeckInGame deck;
	private List<PlayerActionListener> listeners;
	
	public PlayerActionProcessorImpl(Player player, Deck deck) {
		this.deck = new DeckInGameImpl(deck);
		this.ownState = new PlayerState(player);
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
		List<Card> hand = ownState.getHand();
		while (number > 0 && deck.hasNext()) {
			Card card = deck.drawNext();
			number--;
			hand.add(card);
		}
		ownState.setHand(hand);
	}
	
	@Override
	public void removeCardFromHand(Card card) {
		List<Card> hand = ownState.getHand();
		hand.remove(card);
		ownState.setHand(hand);
	}
}

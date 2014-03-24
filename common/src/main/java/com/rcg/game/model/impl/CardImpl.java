package com.rcg.game.model.impl;

import java.util.List;

import com.rcg.game.model.Action;
import com.rcg.game.model.Card;
import com.rcg.game.model.CardCost;

public class CardImpl implements Card {

	private long id;
	private String name;
	private CardCost cost;
	private List<Action> actions;

	public CardImpl(long id, String name, CardCost cost, List<Action> actions) {
		this.id = id;
		this.name = name;
		this.cost = cost;
		this.actions = actions;
	}

	@Override
	public CardCost getCost() {
		return cost;
	}

	@Override
	public List<Action> getActions() {
		return actions;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return new Long(id).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Card))
			return false;
		Card other = (Card) obj;
		if (other.getId() == getId()
				&& other.getName().equals(getName())
				&& other.getCost().equals(getCost())
				&& actions.size() == getActions().size()
				&& actions.containsAll(getActions())) {
			return true;
		}
		return false;
	}

}

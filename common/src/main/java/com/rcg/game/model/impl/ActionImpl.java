package com.rcg.game.model.impl;

import java.util.List;

import com.rcg.game.model.Action;
import com.rcg.game.model.ActionTarget;
import com.rcg.game.model.PlayerActionProcessor;

public class ActionImpl implements Action {

	private ActionType type;
	private List<Integer> values;

	public ActionImpl(ActionType type, List<Integer> values) {
		this.type = type;
		this.values = values;
	}

	@Override
	public ActionType getType() {
		return type;
	}

	@Override
	public List<Integer> getValues() {
		return values;
	}

	private int getValue(int index) {
		return values.get(index);
	}

	@Override
	public void execute(PlayerActionProcessor owner, PlayerActionProcessor enemy, ActionTarget target) {
		switch (type) {
		case BUILD_OWN_TOWER:
			owner.buildTower(getValue(0));
			break;
		case BUILD_OWN_WALL:
			owner.buildWall(getValue(0));
			break;
		case DEMOLISH_TARGET_TOWER:
			target.getProcessor().demolishTower(getValue(0));
			break;
		case DEMOLISH_TARGET_WALL:
			target.getProcessor().demolishWall(getValue(0));
			break;
		case BUILD_TARGET_TOWER:
			target.getProcessor().buildTower(getValue(0));
			break;
		case BUILD_TARGET_WALL:
			target.getProcessor().buildWall(getValue(0));
			break;
		case DESTROY_ENEMY_WALL:
			enemy.demolishWall(enemy.getState().getWall());
			break;
		case REMOVE_TARGET_CARD_FROM_HAND:
			target.getProcessor().removeCardFromHand(target.getCard());
			break;
		}
	}

	@Override
	public int hashCode() {
		int hash = type.ordinal();
		int mult = 1;
		for (Integer value : values) {
			mult *= 10;
			hash += mult * value.intValue();
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof Action))
			return false;
		Action other = (Action) obj;
		if (other.getType() == getType() && other.getValues().size() == getValues().size() && other.getValues().containsAll(getValues())) {
			return true;
		}
		return false;
	}

}

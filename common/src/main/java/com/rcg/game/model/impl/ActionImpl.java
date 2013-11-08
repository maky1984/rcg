package com.rcg.game.model.impl;

import java.util.List;

import com.rcg.game.model.Action;

public class ActionImpl implements Action {

	private int type;
	private List<Integer> values;

	public ActionImpl(int type, List<Integer> values) {
		this.type = type;
		this.values = values;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public List<Integer> getValues() {
		return values;
	}

	@Override
	public void execute(PlayerState myState, PlayerState enemyState) {
		// TODO
	}

	@Override
	public int hashCode() {
		int hash = type;
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

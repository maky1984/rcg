package com.rcg.game.model.impl;

import com.rcg.game.model.CardCost;

public class CardCostImpl implements CardCost {

	private int bricks, gems, recruiters;

	public CardCostImpl(int bricks, int gems, int recruiters) {
		this.bricks = bricks;
		this.gems = gems;
		this.recruiters = recruiters;
	}

	@Override
	public int getBricks() {
		return bricks;
	}

	@Override
	public int getGems() {
		return gems;
	}

	@Override
	public int getRecruiters() {
		return recruiters;
	}
	
	@Override
	public int hashCode() {
		return bricks + gems * 1000 + recruiters * 1000000;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof CardCost)) return false;
		CardCost other = (CardCost)obj;
		if (other.getBricks() == getBricks() && other.getGems() == getGems() && other.getRecruiters() == getRecruiters()) {
			return true;
		}
		return false;
	}

}

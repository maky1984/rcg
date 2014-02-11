package com.rcg.game.model;

import java.util.List;

public interface Action {

	public enum ActionType {
		BUILD_OWN_TOWER,
		BUILD_TARGET_TOWER,
		DEMOLISH_TARGET_TOWER,
		BUILD_OWN_WALL,
		BUILD_TARGET_WALL,
		DEMOLISH_TARGET_WALL,
		DESTROY_ENEMY_WALL,
		REMOVE_TARGET_CARD_FROM_HAND
	}

	public ActionType getType();

	public List<Integer> getValues();

	public void execute(PlayerActionProcessor owner, PlayerActionProcessor enemy, ActionTarget target);

}

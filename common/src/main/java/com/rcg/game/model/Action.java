package com.rcg.game.model;

import java.util.List;

import com.rcg.game.model.server.PlayerActionProcessor;

public interface Action {

	public enum ActionType {
		BUILD_OWN_TOWER,
		BUILD_TARGET_TOWER(true),
		DEMOLISH_TARGET_TOWER(true),
		BUILD_OWN_WALL,
		BUILD_TARGET_WALL(true),
		DEMOLISH_TARGET_WALL(true),
		DESTROY_ENEMY_WALL,
		REMOVE_TARGET_CARD_FROM_HAND(true);
		
		private boolean needTarget;
		
		private ActionType() {
			this(false);
		}
		
		private ActionType(boolean needTarget) {
			this.needTarget = needTarget;
		}
		
		public boolean needTarget() {
			return needTarget;
		}
	}

	public ActionType getType();

	public List<Integer> getValues();

	public void execute(PlayerActionProcessor owner, PlayerActionProcessor enemy, ActionTarget target);

}

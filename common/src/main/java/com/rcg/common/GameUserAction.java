package com.rcg.common;

public class GameUserAction extends ClientRequest {

	private int choosenCardInHandNumber;
	private boolean hasTarget;
	private boolean isEnemyTarget;
	private int targetCardInHand;
	
	public GameUserAction() {
		super(GameUserAction.class.getName());
	}

	public void setChoosenCardInHandNumber(int choosenCardInHandNumber) {
		this.choosenCardInHandNumber = choosenCardInHandNumber;
	}

	public int getChoosenCardInHandNumber() {
		return choosenCardInHandNumber;
	}
	
	public void setEnemyTarget(boolean isEnemyTarget) {
		this.isEnemyTarget = isEnemyTarget;
	}
	
	public void setHasTarget(boolean hasTarget) {
		this.hasTarget = hasTarget;
	}
	
	public void setTargetCardInHand(int targetCardInHand) {
		this.targetCardInHand = targetCardInHand;
	}
	
	public boolean isEnemyTarget() {
		return isEnemyTarget;
	}
	
	public boolean isHasTarget() {
		return hasTarget;
	}
	
	public int getTargetCardInHand() {
		return targetCardInHand;
	}
}

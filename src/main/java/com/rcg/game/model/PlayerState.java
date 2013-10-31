package com.rcg.game.model;

import java.util.List;

public class PlayerState {

	private int bricks, gems, recruits, quarry, magic, dungeon, wall, tower;
	private List<Card> hand;
	private boolean hasTurn;

	public int getBricks() {
		return bricks;
	}
	
	public int getDungeon() {
		return dungeon;
	}
	
	public int getGems() {
		return gems;
	}
	
	public List<Card> getHand() {
		return hand;
	}
	
	public int getMagic() {
		return magic;
	}
	
	public int getQuarry() {
		return quarry;
	}
	
	public int getRecruits() {
		return recruits;
	}
	
	public int getTower() {
		return tower;
	}
	
	public int getWall() {
		return wall;
	}
	
	public boolean hasTurn() {
		return hasTurn;
	}
	
	public void setBricks(int bricks) {
		this.bricks = bricks;
	}
	
	public void setDungeon(int dungeon) {
		this.dungeon = dungeon;
	}
	
	public void setGems(int gems) {
		this.gems = gems;
	}
	
	public void setHand(List<Card> hand) {
		this.hand = hand;
	}
	
	public void setHasTurn(boolean hasTurn) {
		this.hasTurn = hasTurn;
	}
	
	public void setMagic(int magic) {
		this.magic = magic;
	}
	
	public void setQuarry(int quarry) {
		this.quarry = quarry;
	}
	
	public void setRecruits(int recruits) {
		this.recruits = recruits;
	}
	
	public void setTower(int tower) {
		this.tower = tower;
	}
	
	public void setWall(int wall) {
		this.wall = wall;
	}
}

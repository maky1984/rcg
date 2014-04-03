package com.rcg.game.model;

import java.util.ArrayList;
import java.util.List;

import com.rcg.game.model.server.Player;

public class PlayerState {

	public static final int NO_TURN = 0;
	public static final int HAS_TURN = 1;
	public static final int WIN = 2;
	public static final int LOSE = 3;
	public static final int DRAW = 4;
	
	private int bricks, gems, recruiters, quarry, magic, dungeon, wall, tower;
	private List<Long> hand = new ArrayList<Long>();

	private int state = NO_TURN;

	public PlayerState() {
	}

	/**
	 * Quarry generates bricks
	 * 
	 * @return
	 */
	public int getQuarry() {
		return quarry;
	}

	/**
	 * Magic generates gems
	 * 
	 * @return
	 */
	public int getMagic() {
		return magic;
	}

	/**
	 * Dungeon generates recruiters
	 * 
	 * @return
	 */
	public int getDungeon() {
		return dungeon;
	}

	public List<Long> getHand() {
		return hand;
	}

	public int getBricks() {
		return bricks;
	}

	public int getGems() {
		return gems;
	}

	public int getRecruiters() {
		return recruiters;
	}

	public int getTower() {
		return tower;
	}

	public int getWall() {
		return wall;
	}

	public int getState() {
		return state;
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

	public void setHand(List<Long> hand) {
		this.hand = hand;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setMagic(int magic) {
		this.magic = magic;
	}

	public void setQuarry(int quarry) {
		this.quarry = quarry;
	}

	public void setRecruiters(int recruiters) {
		this.recruiters = recruiters;
	}

	public void setTower(int tower) {
		this.tower = tower;
	}

	public void setWall(int wall) {
		this.wall = wall;
	}
}

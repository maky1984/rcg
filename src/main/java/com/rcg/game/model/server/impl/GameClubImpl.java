package com.rcg.game.model.server.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rcg.game.model.server.Game;
import com.rcg.game.model.server.GameClub;
import com.rcg.game.model.server.Player;

public class GameClubImpl implements GameClub {

	private Map<Player, Game> games = new HashMap<Player, Game>();
	
	@Override
	public Game addGame(Player player) {
		Game game = new GameImpl();
		game.open();
		game.add(player);
		games.put(player, game);
		return game;
	}
	
	@Override
	public List<Game> getGames() {
		return new ArrayList<Game>(games.values());
	}
}

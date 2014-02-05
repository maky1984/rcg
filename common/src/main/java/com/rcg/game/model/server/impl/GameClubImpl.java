package com.rcg.game.model.server.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rcg.game.model.server.Game;
import com.rcg.game.model.server.GameClub;
import com.rcg.game.model.server.Player;

public class GameClubImpl implements GameClub {

	private Map<Long, Game> games = new HashMap<Long, Game>();
	
	@Override
	public Game createGameWithPlayer1(Player player) {
		Game game = new GameImpl();
		game.open();
		game.add(player);
		games.put(game.getId(), game);
		return game;
	}
	
	@Override
	public Game getGame(long id) {
		return games.get(id);
	}
	
	@Override
	public Game connectPlayer2ToGame(long id, Player player) {
		Game game = games.get(id);
		game.add(player);
		return game;
	}
	
	@Override
	public List<Game> getGames() {
		return new ArrayList<Game>(games.values());
	}

}

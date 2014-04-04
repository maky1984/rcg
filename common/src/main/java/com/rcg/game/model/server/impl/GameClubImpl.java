package com.rcg.game.model.server.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rcg.game.model.server.CardBase;
import com.rcg.game.model.server.DeckBase;
import com.rcg.game.model.server.Game;
import com.rcg.game.model.server.GameClub;
import com.rcg.game.model.server.GameListener;
import com.rcg.game.model.server.Player;

public class GameClubImpl implements GameClub, GameListener {

	private Map<Long, Game> games = new HashMap<Long, Game>();
	
	private CardBase cardBase = new CardBaseImpl();
	private DeckBase deckBase = new DeckBaseImpl(cardBase);
	
	@Override
	public Game createGameWithPlayer1(Player player, long deckId) {
		Game game = new GameImpl();
		game.setListener(this);
		game.setDeckBase(deckBase);
		game.open();
		game.setPlayer1(player, deckId);
		games.put(game.getId(), game);
		return game;
	}
	
	@Override
	public Game getGame(long id) {
		return games.get(id);
	}
	
	@Override
	public Game connectPlayer2ToGame(long id, Player player, long deckId) {
		Game game = games.get(id);
		game.setPlayer2(player, deckId);
		return game;
	}
	
	@Override
	public List<Game> getGames() {
		return new ArrayList<Game>(games.values());
	}
	
	@Override
	public Game getGameByPlayer(Player player) {
		Game result = null;
		List<Game> games = getGames();
		for (Game game : games) {
			if ((game.getPlayer1() != null && game.getPlayer1().equals(player)) || (game.getPlayer2() != null && game.getPlayer2().equals(player))) {
				result = game;
				break;
			}
		}
		return result;
	}
	
	@Override
	public void gameIsOver(Game game) {
		game.setListener(null);
		game.close();
		games.remove(game.getId());
	}

}

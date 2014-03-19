package com.rcg.game.model.server.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.game.model.DeckInGame;
import com.rcg.game.model.PlayerActionProcessor;
import com.rcg.game.model.impl.DeckInGameImpl;
import com.rcg.game.model.impl.PlayerActionProcessorImpl;
import com.rcg.game.model.server.DeckBase;
import com.rcg.game.model.server.Game;
import com.rcg.game.model.server.Player;
import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageHandler;
import com.rcg.server.api.MessageService;
import com.rcg.server.api.TaskExecutor;

public class GameImpl implements Game, MessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(GameImpl.class);

	private enum ServerInnerState {
		INIT,
		WAIT_PLAYER1,
		WAIT_PLAYER2,
		INITIALIZING,
		WAIT_CARD_FROM_1,
		WAIT_CARD_FROM_2,
		FINISHING
	};

	private MessageService messageService;
	private TaskExecutor taskExecutor;
	private DeckBase deckBase;

	private long id = Game.EMPTY_GAME_ID;

	private Player player1;
	private PlayerActionProcessor processor1;

	private Player player2;
	private PlayerActionProcessor processor2;

	private ServerInnerState state = ServerInnerState.INIT;

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setDeckBase(DeckBase deckBase) {
		this.deckBase = deckBase;
	}

	private void updateInnerState(ServerInnerState newState) {
		state = newState;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getName() {
		String name1 = player1 == null ? "NULL" : player1.getName();
		String name2 = player2 == null ? "NULL" : player2.getName();
		return name1 + " vs " + name2;
	}

	@Override
	public void open() {
		do {
			id = UUID.randomUUID().getMostSignificantBits();
		} while (id == EMPTY_GAME_ID);
		// check all preconditions
		updateInnerState(ServerInnerState.WAIT_PLAYER1);
	}

	@Override
	public void setPlayer1(Player player, long deckId) {
		setPlayer(player, deckId);
	}

	@Override
	public void setPlayer2(Player player, long deckId) {
		setPlayer(player, deckId);
	}

	private void startGameProcessing() {
		updateInnerState(ServerInnerState.INITIALIZING);
		// TODO
	}
	
	public void setPlayer(Player player, long deckId) {
		if (state == ServerInnerState.WAIT_PLAYER1) {
			player1 = player;
			player1.getClientHandle().addMessageHandler(this);
			processor1 = new PlayerActionProcessorImpl(getPlayer1(), deckBase.getDeckById(deckId));
			updateInnerState(ServerInnerState.WAIT_PLAYER2);
		} else if (state == ServerInnerState.WAIT_PLAYER2) {
			if (player1.equals(player)) {
				logger.info("ERROR You are trying to add player that is duplicating the first player=", player);
			} else {
				player2 = player;
				player2.getClientHandle().addMessageHandler(this);
				processor2 = new PlayerActionProcessorImpl(getPlayer2(), deckBase.getDeckById(deckId));
				startGameProcessing();
			}
		} else {
			logger.error("ERROR Cant add more players to the game");
		}
	}

	@Override
	public boolean isReadyForPlay() {
		boolean result;
		switch (state) {
		case INIT:
		case WAIT_PLAYER1:
		case WAIT_PLAYER2:
			result = false;
			break;
		default:
			result = true;
			break;
		}
		return result;
	}

	@Override
	public Player getPlayer1() {
		return player1;
	}

	@Override
	public Player getPlayer2() {
		return player2;
	}

	@Override
	public boolean accept(Message message, ClientHandle caller) {
		// TODO:
		return false;
	}

}

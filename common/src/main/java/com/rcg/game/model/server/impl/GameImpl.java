package com.rcg.game.model.server.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.game.model.server.Game;
import com.rcg.game.model.server.Player;
import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageHandler;
import com.rcg.server.api.MessageService;
import com.rcg.server.api.Task;
import com.rcg.server.api.TaskExecutor;

public class GameImpl implements Game, MessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(GameImpl.class);
	
	private enum ServerInnerState {
		INIT,
		WAIT_PLAYER1,
		WAIT_PLAYER2,
		DRAW_CARDS,
		WAIT_CARD_FROM_1,
		WAIT_CARD_FROM_2
	};
	
	private MessageService messageService;
	private TaskExecutor taskExecutor;
	
	private long id;
	private Player player1;
	private Player player2;
	
	private ServerInnerState state = ServerInnerState.INIT;

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}
	
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}
	
	private void updateState(ServerInnerState newState) {
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
		id = UUID.randomUUID().getMostSignificantBits();
		// check all preconditions
		updateState(ServerInnerState.WAIT_PLAYER1);
	}
	
	private void drawCards() {
		updateState(ServerInnerState.DRAW_CARDS);
		player1.fillHand(player1.getCurrentDeck());
		player2.fillHand(player2.getCurrentDeck());
		sendGameState();
		updateState(ServerInnerState.WAIT_CARD_FROM_1);
	}
	
	private void sendGameState() {
		// TODO
	}

	@Override
	public void add(Player player) {
		if ( state == ServerInnerState.WAIT_PLAYER1 ) {
			player1 = player;
			player1.getClientHandle().addMessageHandler(this);
			updateState(ServerInnerState.WAIT_PLAYER2);
		} else if (state == ServerInnerState.WAIT_PLAYER2) {
			player2 = player;
			player2.getClientHandle().addMessageHandler(this);
			drawCards();
		} else {
			logger.error("ERROR Cant add more players to the game");
		}
	}
	
	@Override
	public boolean accept(Message message, ClientHandle caller) {
		// TODO:
		return false;
	}

}

package com.rcg.model.server.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.model.server.Game;
import com.rcg.model.server.Player;
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
	public void open() {
		Task initTask = new Task() {
			@Override
			public void run() {
				// check all preconditions
				updateState(ServerInnerState.WAIT_PLAYER1);
			}
		};
		taskExecutor.addTask(initTask);
	}
	
	private void drawCards() {
		updateState(ServerInnerState.DRAW_CARDS);
		player1.fillHand(player1.getDeck());
		player2.fillHand(player2.getDeck());
		sendGameState();
		updateState(ServerInnerState.WAIT_CARD_FROM_1);
	}
	
	private void sendGameState() {
		
	}

	@Override
	public void add(Player player) {
		if ( state == ServerInnerState.WAIT_PLAYER1 ) {
			player1 = player;
			player1.getClientHandle().setMessageHandler(this);
			updateState(ServerInnerState.WAIT_PLAYER2);
		} else if (state == ServerInnerState.WAIT_PLAYER2) {
			player2 = player;
			player2.getClientHandle().setMessageHandler(this);
			drawCards();
		} else {
			logger.error("ERROR Cant add more players to the game");
		}
	}
	
	@Override
	public void accept(Message message, ClientHandle caller) {
		// TODO:
	}

}

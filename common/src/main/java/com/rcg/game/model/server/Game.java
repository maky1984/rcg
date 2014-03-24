package com.rcg.game.model.server;

import com.rcg.game.model.RuleConstants;
import com.rcg.server.api.MessageHandler;
import com.rcg.server.api.MessageService;
import com.rcg.server.api.TaskExecutor;

public interface Game extends MessageHandler {

	public static final long EMPTY_GAME_ID = 0;
	
	public static final int HAND_SIZE = RuleConstants.MAX_HAND_CARD_NUMBER;

	public long getId();

	public String getName();
	
	public void open();
	
	public void setPlayer1(Player player, long deckId);
	
	public void setPlayer2(Player player, long deckId);
	
	public void setDeckBase(DeckBase deckBase);
	
	public boolean isReadyForPlay();
	
	public Player getPlayer1();
	
	public Player getPlayer2();
	
	public void start();

	
	// TODO: task executor and msg service should be seperated
	
	public void setTaskExecutor(TaskExecutor executor);
	
	public void setMsgService(MessageService msgServcie);
	
}

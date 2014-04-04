package com.rcg.game.model.server.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.common.GameTableUpdate;
import com.rcg.common.GameUserAction;
import com.rcg.common.RequestGameTableUpdate;
import com.rcg.game.model.Action;
import com.rcg.game.model.ActionTarget;
import com.rcg.game.model.Card;
import com.rcg.game.model.CardCost;
import com.rcg.game.model.PlayerState;
import com.rcg.game.model.RuleConstants;
import com.rcg.game.model.server.DeckBase;
import com.rcg.game.model.server.Game;
import com.rcg.game.model.server.GameListener;
import com.rcg.game.model.server.Player;
import com.rcg.game.model.server.PlayerActionListener;
import com.rcg.game.model.server.PlayerActionProcessor;
import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageService;
import com.rcg.server.api.Task;
import com.rcg.server.api.TaskExecutor;

public class GameImpl implements Game, PlayerActionListener, Task {

	private static final Logger logger = LoggerFactory.getLogger(GameImpl.class);

	private enum ServerInnerState {
		INIT,
		WAIT_PLAYER1,
		WAIT_PLAYER2,
		INITIALIZING,
		WAIT_TURN_FROM_1,
		WAIT_TURN_FROM_2,
		FINISHING
	};

	private static final int PING_PERIOD = 1000;
	private static final long PLAYER_INACTIVITY_MAX_TIME = RuleConstants.PLAYER_INACTIVITY_MAX_TIME;
	
	private DeckBase deckBase;
	private TaskExecutor taskExecutor;
	private MessageService msgService;

	private long id = Game.EMPTY_GAME_ID;

	private Player player1;
	private PlayerActionProcessor processor1;
	private long player1ThinkTime;

	private Player player2;
	private PlayerActionProcessor processor2;
	private long player2ThinkTime;
	
	private volatile ServerInnerState state = ServerInnerState.INIT;

	private GameListener listener;
	
	public void setDeckBase(DeckBase deckBase) {
		this.deckBase = deckBase;
	}
	
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}
	
	public void setMsgService(MessageService msgService) {
		this.msgService = msgService;
	}
	
	@Override
	public void setListener(GameListener listener) {
		this.listener = listener;
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

	public void setPlayer(Player player, long deckId) {
		if (state == ServerInnerState.WAIT_PLAYER1) {
			player1 = player;
			player1.getClientHandle().addMessageHandler(this);
			processor1 = new PlayerActionProcessorImpl(getPlayer1(), deckBase.getDeckById(deckId));
			processor1.addListener(this);
			updateInnerState(ServerInnerState.WAIT_PLAYER2);
		} else if (state == ServerInnerState.WAIT_PLAYER2) {
			if (player1.equals(player)) {
				logger.info("ERROR You are trying to add player that is duplicating the first player=", player);
			} else {
				player2 = player;
				player2.getClientHandle().addMessageHandler(this);
				processor2 = new PlayerActionProcessorImpl(getPlayer2(), deckBase.getDeckById(deckId));
				processor2.addListener(this);
				updateInnerState(ServerInnerState.INITIALIZING);
			}
		} else {
			// TODO Add logic here for recovery players from new clients
			logger.error("ERROR Cant add more players to the game");
		}
	}
	
	@Override
	public void close() {
		if (state == ServerInnerState.FINISHING) {
			player1.getClientHandle().removeMessageHandler(this);
			player2.getClientHandle().removeMessageHandler(this);
		}
	}

	@Override
	public boolean isReadyForPlay() {
		boolean result;
		switch (state) {
		case INIT:
		case WAIT_PLAYER1:
		case WAIT_PLAYER2:
		case FINISHING:
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
	
	private Player getActivePlayer() {
		Player activePlayer;
		switch (state) {
		case WAIT_TURN_FROM_1:
			activePlayer = getPlayer1();
			break;
		case WAIT_TURN_FROM_2:
			activePlayer = getPlayer2();
			break;
		default:
			activePlayer = null;
			break;
		}
		return activePlayer;
	}

	private PlayerActionProcessor getNonActivePlayerProcessor() {
		PlayerActionProcessor activeProcessor;
		switch (state) {
		case WAIT_TURN_FROM_1:
			activeProcessor = processor2;
			break;
		case WAIT_TURN_FROM_2:
			activeProcessor = processor1;
			break;
		default:
			activeProcessor = null;
			break;
		}
		return activeProcessor;
	}

	private PlayerActionProcessor getActivePlayerProcessor() {
		PlayerActionProcessor activeProcessor;
		switch (state) {
		case WAIT_TURN_FROM_1:
			activeProcessor = processor1;
			break;
		case WAIT_TURN_FROM_2:
			activeProcessor = processor2;
			break;
		default:
			activeProcessor = null;
			break;
		}
		return activeProcessor;
	}
	
	@Override
	public void start() {
		taskExecutor.addTask(this, PING_PERIOD);
		// By default player 1 has first turn
		processor1.drawCards(HAND_SIZE);
		processor2.drawCards(HAND_SIZE);
		processor1.initState();
		processor2.initState();
		updateInnerState(ServerInnerState.WAIT_TURN_FROM_1);
		processor1.startTurn();
	}
	
	private void doCardAction(int cardNumberInHand, ActionTarget target) {
		PlayerActionProcessor cardOwner = getActivePlayerProcessor();
		PlayerActionProcessor oppositePlayer = getNonActivePlayerProcessor();
		PlayerState playerState = cardOwner.getState();
		List<Long> hand = playerState.getHand();
		Card card = deckBase.getCardById(hand.get(cardNumberInHand));
		CardCost cost = card.getCost();
		if (playerState.getBricks() >= cost.getBricks() && playerState.getGems() >= cost.getGems() && playerState.getRecruiters() >= cost.getRecruiters()) {
			// pay cost for card playing
			playerState.setBricks(playerState.getBricks() - cost.getBricks());
			playerState.setGems(playerState.getGems() - cost.getGems());
			playerState.setRecruiters(playerState.getRecruiters() - cost.getRecruiters());
			cardOwner.removeCardFromHand(card);
			// get card actions
			List<Action> actions = card.getActions();
			for (Action action : actions) {
				if (!action.getType().needTarget() || target != null) {
					action.execute(cardOwner, oppositePlayer, target);
				} else {
					logger.error("Skip one of the action, because there is no target, action=" + action);
				}
			}
		} else {
			logger.error("Card with cost:" + cost + " cant be prcessed with player:" + cardOwner + " No resources.");
		}
	}
	
	@Override
	public void postGameStateToPlayers() {
		postCurrentStateToPlayers();
	}
	
	private void postCurrentStateToPlayers() {
		GameTableUpdate updateForPlayer1 = new GameTableUpdate();
		updateForPlayer1.setOwnState(processor1.getState());
		updateForPlayer1.setEnemyState(processor2.getState());
		GameTableUpdate updateForPlayer2 = new GameTableUpdate();
		updateForPlayer2.setOwnState(processor2.getState());
		updateForPlayer2.setEnemyState(processor1.getState());
		msgService.send(player1.getClientHandle(), new Message(updateForPlayer1));
		msgService.send(player2.getClientHandle(), new Message(updateForPlayer2));
	}
	
	@Override
	public boolean accept(Message message, ClientHandle caller) {
		logger.info("GameImpl accepts msg:" + message);
		if (message.getClassName().equals(GameUserAction.class.getName())) {
			Player activePlayer = getActivePlayer();
			if (activePlayer.getClientHandle().equals(caller)) {
				final GameUserAction userAction = message.unpackMessage();
				int cardInHandNumber = userAction.getChoosenCardInHandNumber();
				ActionTarget target;
				if (userAction.isHasTarget()) {
					target = new ActionTarget() {
						@Override
						public PlayerActionProcessor getProcessor() {
							return userAction.isEnemyTarget() ? getNonActivePlayerProcessor() : getActivePlayerProcessor();
						}
						
						@Override
						public Card getCard() {
							int targetCardInHand = userAction.getTargetCardInHand();
							List<Long> hand = getProcessor().getState().getHand();
							Card card;
							if (targetCardInHand >= 0 && targetCardInHand < hand.size()) {
								card = deckBase.getCardById(hand.get(targetCardInHand));
							} else {
								card = null;
							}
							return card;
						}
					};
				} else {
					target = null;
				}
				doCardAction(cardInHandNumber, target);
				switch(state) {
				case WAIT_TURN_FROM_1:
					processor1.endTurn();
					updateInnerState(ServerInnerState.WAIT_TURN_FROM_2);
					processor2.startTurn();
					break;
				case WAIT_TURN_FROM_2:
					processor2.endTurn();
					updateInnerState(ServerInnerState.WAIT_TURN_FROM_1);
					processor1.startTurn();
					break;
				case FINISHING:
					break;
				default:
					break;
				}
				postCurrentStateToPlayers();
			} else {
				logger.info("Player:" + caller + " does not have turn now");
			}
		} else if (message.getClassName().equals(RequestGameTableUpdate.class.getName())) {
			postCurrentStateToPlayers();
		}
		// TODO:
		return false;
	}

	private void gameOver(PlayerActionProcessor processor, boolean isWin) {
		if ((processor.equals(processor1) && isWin) || (processor.equals(processor2) && !isWin)) {
			processor1.getState().setState(PlayerState.WIN);
			processor2.getState().setState(PlayerState.LOSE);
		} else {
			processor1.getState().setState(PlayerState.LOSE);
			processor2.getState().setState(PlayerState.WIN);
		}
		updateInnerState(ServerInnerState.FINISHING);
	}
	
	@Override
	public void towerDecreased(PlayerActionProcessor processor) {
	}

	@Override
	public void towerDestroyed(PlayerActionProcessor processor) {
		gameOver(processor, false);
	}

	@Override
	public void towerIncreased(PlayerActionProcessor processor) {
	}

	@Override
	public void handIsEmpty(PlayerActionProcessor processor) {
		gameOver(processor, false);
	}

	@Override
	public void towerIsFull(PlayerActionProcessor processor) {
		gameOver(processor, true);
	}
	
	private void checkGameInactivity(PlayerActionProcessor processor, long thinkTime) {
		if (System.currentTimeMillis() - thinkTime > PLAYER_INACTIVITY_MAX_TIME) {
			gameOver(processor, false);
		}
	}
	
	@Override
	public void run() {
		switch (state) {
		case INIT:
		case INITIALIZING:
		case WAIT_PLAYER1:
		case WAIT_PLAYER2:
			break;
		case WAIT_TURN_FROM_1:
			player2ThinkTime = 0;
			if (player1ThinkTime == 0) {
				player1ThinkTime = System.currentTimeMillis();
			}
			checkGameInactivity(processor1, player1ThinkTime);
			break;
		case WAIT_TURN_FROM_2:
			player1ThinkTime = 0;
			if (player2ThinkTime == 0) {
				player2ThinkTime = System.currentTimeMillis();
			}
			checkGameInactivity(processor2, player2ThinkTime);
			break;
		case FINISHING:
			processor1.removeListener(this);
			processor2.removeListener(this);
			taskExecutor.removeTask(this);
			listener.gameIsOver(this);
			break;
		}
	}

}

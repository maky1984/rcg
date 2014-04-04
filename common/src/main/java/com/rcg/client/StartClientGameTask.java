package com.rcg.client;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.client.javafxapp.ClientFXApplication;
import com.rcg.common.RequestConnectToGame;
import com.rcg.common.RequestGameList;
import com.rcg.common.RequestRegisterClientHandle;
import com.rcg.common.ResponseConnectToGame;
import com.rcg.common.ResponseGameList;
import com.rcg.common.ResponseRegisterClientHandle;
import com.rcg.common.ResponseErrorConnectingPlayerToGame;
import com.rcg.game.model.server.Game;
import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageHandler;
import com.rcg.server.api.MessageService;
import com.rcg.server.api.RCGServerException;
import com.rcg.server.api.Task;
import com.rcg.server.api.TaskExecutor;
import com.rcg.server.impl.ClientHandleImpl;

public class StartClientGameTask implements Task, MessageHandler {

	private final static Logger logger = LoggerFactory.getLogger(StartClientGameTask.class);
	private final static long UPDATE_GAME_LIST_PERIOD = 5000;

	private String host = "localhost";
	private int port = 47777;

	private MessageService messageService;
	private TaskExecutor executor;
	private ClientHandle clientHandle;
	private ClientFXApplication app;
	private Task updateGameListTask = new Task() {

		@Override
		public void run() {
			updateGameList();
		}
	};

	public StartClientGameTask() {
	}

	public StartClientGameTask(String serverHost, int serverPort) {
		this.host = serverHost;
		this.port = serverPort;
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	public void setExecutor(TaskExecutor executor) {
		this.executor = executor;
	}

	public void setApp(ClientFXApplication app) {
		this.app = app;
	}

	private void setClientHandle(ClientHandle clientHandle) {
		clientHandle.addMessageHandler(this);
		this.clientHandle = clientHandle;
	}

	public ClientHandle getClientHandle() {
		return clientHandle;
	}

	@Override
	public boolean accept(Message message, ClientHandle caller) {
		if (message.getClassName().equals(ResponseGameList.class.getName())) {
			ResponseGameList response = message.unpackMessage();
			app.updateGameList(response.getGames());
		} else if (message.getClassName().equals(ResponseConnectToGame.class.getName())) {
			ResponseConnectToGame response = message.unpackMessage();
			if (response.isReadyToStart()) {
				executor.removeTask(updateGameListTask); // maybe let update the
															// game list forever
				app.startGameProcess(response.getGameName(), response.getGameId());
			} else {
				app.updateWaitForPlayer(response.getGameName(), response.getGameId());
			}
		} else if (message.getClassName().equals(ResponseErrorConnectingPlayerToGame.class.getName())) {
			ResponseErrorConnectingPlayerToGame response = message.unpackMessage();
			app.updateUnknownPlayer(response.getStatus());
		}
		return false;
	}

	private void updateGameList() {
		RequestGameList request = new RequestGameList();
		messageService.send(getClientHandle(), new Message(request));
	}

	@Override
	public void run() {
		ClientHandle client = new ClientHandleImpl(UUID.randomUUID().getLeastSignificantBits(), host, port);
		client.addMessageHandler(new MessageHandler() {
			@Override
			public boolean accept(Message message, ClientHandle caller) {
				ResponseRegisterClientHandle response = message.unpackMessage();
				logger.info("Response status:" + response.getStatus() + " caller=" + caller);
				caller.removeMessageHandler(this);
				setClientHandle(caller);
				restartGameListUpdateTask();
				return true;
			}
		});
		try {
			RequestRegisterClientHandle request = new RequestRegisterClientHandle();
			Message message = new Message();
			message.fillMessage(request);
			messageService.addClientHandle(client);
			messageService.send(client, message);
		} catch (RCGServerException ex) {
			logger.info("Cant connect to server", ex);
			app.updatePopup("Cant connect to server");
		}
	}

	public void startGame() {
		connectToGame(Game.EMPTY_GAME_ID);
	}
	
	public void restartGameListUpdateTask() {
		executor.addTask(updateGameListTask, UPDATE_GAME_LIST_PERIOD);
	}

	public void connectToGame(long id) {
		RequestConnectToGame request = new RequestConnectToGame();
		request.setGameId(id);
		request.setDeckId(app.getDeckId());
		request.setPlayerName(app.getPlayerName());
		request.setPlayerId(app.getPlayerId());
		messageService.send(getClientHandle(), new Message(request));
	}

}

package com.rcg.client;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.client.javafxapp.ClientFXApplication;
import com.rcg.common.ClientResponse;
import com.rcg.common.RequestConnectToGame;
import com.rcg.common.RequestGameList;
import com.rcg.common.RequestRegisterClientHandle;
import com.rcg.common.ResponseConnectToGame;
import com.rcg.common.ResponseGameList;
import com.rcg.common.ResponseRegisterClientHandle;
import com.rcg.common.ResponseUnknownPlayer;
import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageHandler;
import com.rcg.server.api.MessageService;
import com.rcg.server.api.Task;
import com.rcg.server.api.TaskExecutor;
import com.rcg.server.impl.ClientHandleImpl;

public class StartGameTask implements Task, MessageHandler {

	private final static Logger logger = LoggerFactory.getLogger(StartGameTask.class);
	private final static long UPDATE_GAME_LIST_PERIOD = 5000;	
	
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
			if (response.getPlayer1Name() != null && response.getPlayer2Name() != null) {
				executor.removeTask(updateGameListTask);
				app.startGameProcess(response.getGameName(), response.getGameId());
			} else {
				app.updateWaitForPlayer(response.getGameName(), response.getGameId());
			}
		} else if (message.getClassName().equals(ResponseUnknownPlayer.class.getName())) {
			ResponseUnknownPlayer response = message.unpackMessage();
			app.updateUnknownPlayer(response.getStatus());
		}
		return true;
	}
	
	private void updateGameList() {
		RequestGameList request = new RequestGameList();
		messageService.send(getClientHandle(), new Message(request));
	}
	
	@Override
	public void run() {
		ClientHandle client = new ClientHandleImpl(UUID.randomUUID().getLeastSignificantBits(), "localhost", 47777);
		client.addMessageHandler(new MessageHandler() {
			@Override
			public boolean accept(Message message, ClientHandle caller) {
				ResponseRegisterClientHandle response = message.unpackMessage();
				logger.info("Response status:" + response.getStatus() + " caller=" + caller);
				caller.removeMessageHandler(this);
				setClientHandle(caller);
				executor.addTask(updateGameListTask, UPDATE_GAME_LIST_PERIOD);
				return true;
			}
		});
		RequestRegisterClientHandle request = new RequestRegisterClientHandle();
		Message message = new Message();
		message.fillMessage(request);
		messageService.addClientHandle(client);
		messageService.send(client, message);
	}

	public void startGame() {
		RequestConnectToGame request = new RequestConnectToGame();
		request.setCreateNewGame(true);
		request.setPlayerName(app.getPlayerName());
		request.setPlayerId(app.getPlayerId());
		messageService.send(getClientHandle(), new Message(request));
	}

	public void connectToGame(long id) {
		RequestConnectToGame request = new RequestConnectToGame();
		request.setGameId(id);
		request.setPlayerName(app.getPlayerName());
		request.setPlayerId(app.getPlayerId());
		messageService.send(getClientHandle(), new Message(request));
	}
	
}

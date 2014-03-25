package com.rcg.server;

import java.util.ArrayList;
import java.util.List;

import com.rcg.common.ClientResponse;
import com.rcg.common.GameView;
import com.rcg.common.RequestConnectToGame;
import com.rcg.common.RequestGameList;
import com.rcg.common.RequestRegisterClientHandle;
import com.rcg.common.ResponseConnectToGame;
import com.rcg.common.ResponseGameList;
import com.rcg.common.ResponseRegisterClientHandle;
import com.rcg.common.ResponseUnknownPlayer;
import com.rcg.game.model.server.Game;
import com.rcg.game.model.server.GameClub;
import com.rcg.game.model.server.Player;
import com.rcg.game.model.server.PlayerBase;
import com.rcg.game.model.server.impl.GameClubImpl;
import com.rcg.game.model.server.impl.PlayerBaseImpl;
import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageHandler;
import com.rcg.server.api.MessageService;
import com.rcg.server.api.Task;
import com.rcg.server.api.TaskExecutor;
import com.rcg.server.impl.MessageServiceImpl;
import com.rcg.server.impl.TaskExecutorImpl;

public class StartServerTask implements Task, MessageHandler {

	public static final int PORT = 47777;

	private TaskExecutor executor = new TaskExecutorImpl();

	private MessageService messageService = new MessageServiceImpl();

	private GameClub gameClub = new GameClubImpl();

	private PlayerBase playerBase = new PlayerBaseImpl();

	@Override
	public boolean accept(Message message, final ClientHandle caller) {
		if (message.getClassName().equals(RequestGameList.class.getName())) {
			// RequestGameList request = message.unpackMessage();
			List<Game> games = gameClub.getGames();
			ResponseGameList response = new ResponseGameList();
			List<GameView> views = new ArrayList<>();
			for (Game game : games) {
				GameView view = new GameView();
				view.setId(game.getId());
				view.setName(game.getName());
				views.add(view);
			}
			response.setGames(views);
			messageService.send(caller, new Message(response));
		} else if (message.getClassName().equals(RequestConnectToGame.class.getName())) {
			RequestConnectToGame request = message.unpackMessage();
			Player player = playerBase.getPlayerById(request.getPlayerId());
			if (player == null) {
				// Player not registered
				ResponseUnknownPlayer unknownResponse = new ResponseUnknownPlayer();
				unknownResponse.setName(request.getName());
				unknownResponse.setPlayerId(request.getPlayerId());
				unknownResponse.setStatus("Unknown player. Cant find this player in registered player base");
				messageService.send(caller, new Message(unknownResponse));
			} else {
				player.setClientHandle(caller);
				final Game game;
				final ResponseConnectToGame connectResponse = new ResponseConnectToGame();
				if (request.getGameId() == Game.EMPTY_GAME_ID) {
					game = gameClub.createGameWithPlayer1(player, request.getDeckId());
					game.setTaskExecutor(executor);
					game.setMsgService(messageService);
					connectResponse.setReadyToStart(false);
					connectResponse.setGameId(game.getId());
					connectResponse.setGameName(game.getName());
					connectResponse.setPlayer1Name(game.getPlayer1().getName());
					messageService.send(caller, new Message(connectResponse));
				} else {
					game = gameClub.connectPlayer2ToGame(request.getGameId(), player, request.getDeckId());
					if (game.isReadyForPlay()) {
						connectResponse.setReadyToStart(true);
						connectResponse.setGameId(game.getId());
						connectResponse.setGameName(game.getName());
						connectResponse.setPlayer1Name(game.getPlayer1().getName());
						connectResponse.setPlayer2Name(game.getPlayer2().getName());
						executor.addTask(new Task() {
							@Override
							public void run() {
//								game.getPlayer1().getClientHandle().addMessageHandler(game);
//								game.getPlayer2().getClientHandle().addMessageHandler(game);
								game.start();
								// Send response to first player, that both player are ready to play
								messageService.send(game.getPlayer1().getClientHandle(), new Message(connectResponse.copyIt()));
								messageService.send(caller, new Message(connectResponse));
							}
						});
					} else {
						connectResponse.setReadyToStart(false);
						connectResponse.setGameId(game.getId());
						connectResponse.setGameName(game.getName());
						messageService.send(caller, new Message(connectResponse));
					}
				}
			}
		}
		return false;
	}

	@Override
	public void run() {
		executor.start();
		messageService.setDefaultMessageHandler(new MessageHandler() {
			@Override
			public boolean accept(Message message, ClientHandle caller) {
				System.out.println("From new client received: " + message);
				RequestRegisterClientHandle request = message.unpackMessage();
				System.out.println("From request:" + request.getMsg());
				ResponseRegisterClientHandle response = new ResponseRegisterClientHandle();
				response.setStatus("OK");
				messageService.send(caller, new Message(response));
				caller.removeMessageHandler(this);
				caller.addMessageHandler(StartServerTask.this);
				return true;
			}
		});
		messageService.open(PORT);
	}

}

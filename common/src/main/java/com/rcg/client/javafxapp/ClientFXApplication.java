package com.rcg.client.javafxapp;

import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.rcg.client.StartClientGameTask;
import com.rcg.client.StartClientTask;
import com.rcg.common.GameView;
import com.rcg.server.api.Task;
import com.rcg.server.api.TaskExecutor;

public class ClientFXApplication extends Application implements GameTableListener {

	public static long DEFAULT_DECK_ID = 11;

	private TaskExecutor taskExecutor;
	private StartClientTask task;
	private StartClientGameTask startGameTask;

	private Pane group = new VBox();

	private TextField tPlayerName = new TextField("Player name");
	private TextField tPlayerId = new TextField("1");
	private Button bConnect = new Button("Connect to server");
	private Button bNewGame = new Button("Start new game");
	private Label lWaitPlayer = new Label();
	private Pane gameList = new HBox();
	private Popup popup;
	private Stage stage;
	private GameTableFX gameTable;

	@Override
	public void start(final Stage stage) throws Exception {
		this.stage = stage;
		stage.setHeight(300);
		stage.setWidth(400);
		final Label statusLabel = new Label();
		popup = new Popup();
		popup.setAutoHide(true);
		bConnect.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (task.isReady()) {
					startGameTask = new StartClientGameTask();
					startGameTask.setMessageService(task.getMessageService());
					startGameTask.setExecutor(taskExecutor);
					startGameTask.setApp(ClientFXApplication.this);
					taskExecutor.addTask(startGameTask);
				} else {
					statusLabel.setText("Initializing ... try again");
				}
			}
		});
		group.getChildren().addAll(bConnect, statusLabel);
		Scene scene = new Scene(group);
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				taskExecutor.addTask(new Task() {

					@Override
					public void run() {
						task.getMessageService().stop();
						taskExecutor.stop();
					}
				});
			}
		});
		initClient();
	}

	private void initClient() {
		task = new StartClientTask();
		taskExecutor = task.getTaskExecutor();
		new Thread(task).start();
	}

	public void updateGameList(final List<GameView> games) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				group.getChildren().clear();
				gameList.getChildren().clear();
				group.getChildren().add(tPlayerName);
				group.getChildren().add(tPlayerId);
				group.getChildren().add(bNewGame);
				group.getChildren().add(lWaitPlayer);
				group.getChildren().add(gameList);
				bNewGame.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent arg0) {
						startGameTask.startGame();
					}
				});
				for (GameView game : games) {
					Button button = new Button(game.getName());
					button.setUserData(game);
					button.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							long gameId = ((GameView) ((Button) arg0.getSource()).getUserData()).getId();
							System.out.println("Connecting to game with id:" + gameId);
							startGameTask.connectToGame(gameId);
						}
					});
					gameList.getChildren().add(button);
				}
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}

	public String getPlayerName() {
		return tPlayerName.getText();
	}

	public long getPlayerId() {
		return Long.parseLong(tPlayerId.getText());
	}

	public void startGameProcess(String gameName, long gameId) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				group.getChildren().clear();
				gameList.getChildren().clear();
				startGameTable();
			}
		});
	}

	private void startGameTable() {
		gameTable = new GameTableFX();
		gameTable.setListener(this);
		gameTable.setMsgService(task.getMessageService());
		gameTable.start(startGameTask.getClientHandle());
		gameTable.show();
	}

	public void updateWaitForPlayer(final String gameName, final long gameId) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				lWaitPlayer.setText("Wait for player GameName:" + gameName + " gameId:" + gameId);
			}
		});
	}

	public void updateUnknownPlayer(final String status) {
		updatePopup("Unknown player:" + status);
	}

	public void updatePopup(final String status) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				popup.getContent().clear();
				Label label = new Label(status);
				label.setFont(Font.font("Tahoma", FontWeight.BOLD, 25));  
				label.setTextFill(Color.RED);
				label.setStyle("-fx-background-color: rgb(0,0,0,0.1);");
				popup.getContent().addAll(label);
				popup.show(stage);
			}
		});
	}

	public long getDeckId() {
		return DEFAULT_DECK_ID;
	}
	
	@Override
	public void gameOver(int state) {
		gameTable.stop();
		gameTable.setListener(null);
		updatePopup(state == GameTableListener.WIN ? "YOU WON" : (state == GameTableListener.LOSE ? "YOU LOST" : "DRAW"));
		startGameTask.restartGameListUpdateTask();
	}
}

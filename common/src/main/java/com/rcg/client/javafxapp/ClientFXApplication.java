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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.rcg.client.StartClientTask;
import com.rcg.client.StartGameTask;
import com.rcg.common.GameView;
import com.rcg.server.api.Task;
import com.rcg.server.api.TaskExecutor;

public class ClientFXApplication extends Application {

	private TaskExecutor taskExecutor;
	private StartClientTask task;
	private StartGameTask startGameTask;

	private Pane group = new VBox();

	private TextField tPlayerName = new TextField("Player name");
	private TextField tPlayerId = new TextField("1");
	private Button bConnect = new Button("Connect to server");
	private Button bNewGame = new Button("Start new game");
	private Label lWaitPlayer = new Label();
	private Pane gameList = new HBox();;

	@Override
	public void start(final Stage stage) throws Exception {
		final Label statusLabel = new Label();
		bConnect.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (task.isReady()) {
					startGameTask = new StartGameTask();
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
							long gameId = ((GameView)((Button)arg0.getSource()).getUserData()).getId();
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
				
			}
		});
	}

	public void updateWaitForPlayer(final String gameName, final long gameId) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				lWaitPlayer.setText("Wait for player GameName:" + gameName + " gameId:" + gameId);
			}
		});
	}

	public void updateUnknownPlayer(String status) {
		// TODO Auto-generated method stub
		System.out.println("Unknown player");
	}
}
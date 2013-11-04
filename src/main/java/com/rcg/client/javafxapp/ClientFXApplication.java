package com.rcg.client.javafxapp;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.rcg.client.StartClientTask;
import com.rcg.client.StartGameTask;
import com.rcg.server.api.Task;
import com.rcg.server.api.TaskExecutor;

public class ClientFXApplication extends Application {
	
	private TaskExecutor taskExecutor;
	private StartClientTask task;
	
	@Override
	public void start(final Stage stage) throws Exception {
		Pane group = new VBox();
		Button bStart = new Button("Start game");
		final Label statusLabel = new Label();
		bStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (task.isReady()) {
					StartGameTask startGame = new StartGameTask();
					startGame.setMessageService(task.getMessageService());
					taskExecutor.addTask(startGame);
				} else {
					statusLabel.setText("Initializing ... try again");
				}
			}
		});
		group.getChildren().addAll(bStart, statusLabel);
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
	
	public static void main(String[] args) {
		launch(args);
	}
}

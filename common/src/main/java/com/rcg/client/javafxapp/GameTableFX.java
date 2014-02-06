package com.rcg.client.javafxapp;

import com.rcg.common.GameTableUpdate;
import com.rcg.common.ResponseConnectToGame;
import com.rcg.common.ResponseGameList;
import com.rcg.common.ResponseUnknownPlayer;
import com.rcg.game.model.Card;
import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageHandler;
import com.rcg.server.api.MessageService;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameTableFX implements MessageHandler {

	private Stage stage;
	private Scene scene;

	private BorderPane mainBorderPane = new BorderPane();

	private HBox cardList = new HBox(10);

	private VBox userState = new VBox(10);
	private VBox enemyState = new VBox(10);

	private HBox center = new HBox(100);

	private Text leftBricksNumber = new Text();
	private Text leftQuerryNumber = new Text();
	private Text leftGemsNumber = new Text();
	private Text leftMagicNumber = new Text();
	private Text leftRecruitsNumber = new Text();
	private Text leftDungeonNumber = new Text();

	private Text rightBricksNumber = new Text();
	private Text rightQuerryNumber = new Text();
	private Text rightGemsNumber = new Text();
	private Text rightMagicNumber = new Text();
	private Text rightRecruitsNumber = new Text();
	private Text rightDungeonNumber = new Text();

	private Text leftTowerNumber;
	private Text leftWallNumber;
	private Text rightTowerNumber;
	private Text rightWallNumber;

	private Card[] cards = new Card[0];
	
	private MessageService msgService;
	
	public GameTableFX() {
		initialize();
	}

	public void setMsgService(MessageService msgService) {
		this.msgService = msgService;
	}
	
	public Scene getScene() {
		return scene;
	}

	private Node createCard() {
		Paint cardImage = Color.GREEN;
		Rectangle card = new Rectangle(60, 90, cardImage);
		return card;
	}

	private void fillCardList() {
		cardList.setAlignment(Pos.CENTER);
		for (int i = 0; i < getCardListSize(); i++) {
			cardList.getChildren().add(createCard());
		}
	}
	
	public void update() {
		//TODO
	}
	
	private int getCardListSize() {
		return cards.length;
	}

	private Node createStateNode(Paint color, String first, Text firstNumber, String second, Text secondNumber) {
		StackPane stack = new StackPane();
		Rectangle rec = new Rectangle(100, 100, color);
		VBox vbox = new VBox(0);
		Font font = new Font(16);
		Text firstText = new Text(first);
		firstText.setFont(font);
		firstText.setFill(Color.WHITE);
		firstNumber.setFill(Color.WHITE);
		firstNumber.setFont(font);
		Text secondText = new Text(second);
		secondText.setFill(Color.WHITE);
		secondText.setFont(font);
		secondNumber.setFill(Color.WHITE);
		secondNumber.setFont(font);
		vbox.getChildren().addAll(firstText, firstNumber, secondText, secondNumber);
		stack.getChildren().addAll(rec, vbox);
		return stack;
	}

	private void fillUserState() {
		Node bricks = createStateNode(Color.BLACK, "Bricks:", leftBricksNumber, "Quarry:", leftQuerryNumber);
		Node gems = createStateNode(Color.BLACK, "Gems:", leftGemsNumber, "Magic:", leftMagicNumber);
		Node recruits = createStateNode(Color.BLACK, "Recruits:", leftRecruitsNumber, "Dungeon:", leftDungeonNumber);
		userState.getChildren().addAll(bricks, gems, recruits);
	}

	private void fillEnemyState() {
		Node bricks = createStateNode(Color.BLACK, "Bricks:", rightBricksNumber, "Quarry:", rightQuerryNumber);
		Node gems = createStateNode(Color.BLACK, "Gems:", rightGemsNumber, "Magic:", rightMagicNumber);
		Node recruits = createStateNode(Color.BLACK, "Recruits:", rightRecruitsNumber, "Dungeon:", rightDungeonNumber);
		enemyState.getChildren().addAll(bricks, gems, recruits);
	}

	private void fillCenter() {
		Font font = new Font(20);
		Text leftTowerText = new Text("Tower:");
		leftTowerText.setFont(font);
		leftTowerNumber = new Text();
		leftTowerNumber.setFont(font);
		Text leftWallText = new Text("Wall:");
		leftWallText.setFont(font);
		leftWallNumber = new Text();
		leftWallNumber.setFont(font);
		Text rightTowerText = new Text("Enemy tower:");
		rightTowerText.setFont(font);
		rightTowerNumber = new Text();
		rightTowerNumber.setFont(font);
		Text rightWallText = new Text("Enemy wall:");
		rightWallText.setFont(font);
		rightWallNumber = new Text();
		rightWallNumber.setFont(font);
		VBox leftPart = new VBox(10);
		leftPart.getChildren().addAll(leftTowerText, leftTowerNumber, leftWallText, leftWallNumber);
		leftPart.setAlignment(Pos.CENTER);
		VBox rightPart = new VBox(10);
		rightPart.getChildren().addAll(rightTowerText, rightTowerNumber, rightWallText, rightWallNumber);
		rightPart.setAlignment(Pos.CENTER);
		center.getChildren().addAll(leftPart, rightPart);
		center.setAlignment(Pos.CENTER);
	}

	private void initialize() {
		stage = new Stage();
		scene = new Scene(mainBorderPane, 800, 480);
		fillCardList();
		fillUserState();
		fillEnemyState();
		fillCenter();
		mainBorderPane.setBottom(cardList);
		mainBorderPane.setLeft(userState);
		mainBorderPane.setRight(enemyState);
		mainBorderPane.setCenter(center);
		stage.setScene(scene);
	}
	
	public Stage getStage() {
		return stage;
	}
	
	@Override
	public boolean accept(Message message, ClientHandle caller) {
		if (message.getClassName().equals(GameTableUpdate.class.getName())) {
			GameTableUpdate update = message.unpackMessage();
			// TODO
		}
		return true;
	}

	public static class TestFXApp extends Application {

		@Override
		public void start(Stage arg0) throws Exception {
			GameTableFX ui = new GameTableFX();
			arg0.setScene(ui.getScene());
			arg0.show();
		}
	}

	public static void main(String[] args) {
		Application.launch(TestFXApp.class, args);
	}
}

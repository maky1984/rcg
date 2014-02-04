package com.rcg.client.javafxapp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

public class GameTableFX {

	public static final int CARD_LIST_SIZE = 8;

	private Stage stage;
	private Scene scene;

	private BorderPane mainBorderPane = new BorderPane();

	private HBox cardList = new HBox(10);

	private VBox userState = new VBox(10);
	private VBox enemyState = new VBox(10);
	
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
	
	private Text leftTower;
	private Text leftWall;
	private Text rightTower;
	private Text rightWall;

	public GameTableFX() {
		initialize();
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
		for (int i = 0; i < CARD_LIST_SIZE; i++) {
			cardList.getChildren().add(createCard());
		}
	}

	private Node createStateNode(Paint color, String first, Text firstNumber, String second, Text secondNumber) {
		StackPane stack = new StackPane();
		Rectangle rec= new Rectangle(100, 100, color);
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
		Node bricks = createStateNode(Color.BLACK,"Bricks:", leftBricksNumber, "Quarry:", leftQuerryNumber);
		Node gems = createStateNode(Color.BLACK,"Gems:", leftGemsNumber, "Magic:", leftMagicNumber);
		Node recruits = createStateNode(Color.BLACK,"Recruits:", leftRecruitsNumber, "Dungeon:", leftDungeonNumber);
		userState.getChildren().addAll(bricks, gems, recruits);
	}

	private void fillEnemyState() {
		Node bricks = createStateNode(Color.BLACK,"Bricks:", rightBricksNumber, "Quarry:", rightQuerryNumber);
		Node gems = createStateNode(Color.BLACK,"Gems:", rightGemsNumber, "Magic:", rightMagicNumber);
		Node recruits = createStateNode(Color.BLACK,"Recruits:", rightRecruitsNumber, "Dungeon:", rightDungeonNumber);
		enemyState.getChildren().addAll(bricks, gems, recruits);
	}

	private void initialize() {
		stage = new Stage();
		scene = new Scene(mainBorderPane, 800, 480);
		fillCardList();
		fillUserState();
		fillEnemyState();
		mainBorderPane.setBottom(cardList);
		mainBorderPane.setLeft(userState);
		mainBorderPane.setRight(enemyState);
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

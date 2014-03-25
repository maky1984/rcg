package com.rcg.client.javafxapp;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.common.GameTableUpdate;
import com.rcg.common.GameUserAction;
import com.rcg.common.RequestGameTableUpdate;
import com.rcg.game.model.Action;
import com.rcg.game.model.Card;
import com.rcg.game.model.PlayerState;
import com.rcg.game.model.server.CardBase;
import com.rcg.game.model.server.impl.CardBaseImpl;
import com.rcg.server.api.ClientHandle;
import com.rcg.server.api.Message;
import com.rcg.server.api.MessageHandler;
import com.rcg.server.api.MessageService;

public class GameTableFX implements MessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(GameTableFX.class);

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
	
	private Text turnSign;

	private Card[] cards = new Card[6];

	private CardBase cardBase = new CardBaseImpl();

	private MessageService msgService;
	private ClientHandle serverHandle;
	
	private PlayerState ownPlayerState, enemyPlayerState;

	public GameTableFX() {
		initialize();
	}

	public void setMsgService(MessageService msgService) {
		this.msgService = msgService;
	}

	public Scene getScene() {
		return scene;
	}

	private Node createCard(final Card card, final int numberInHand) {
		Paint cardImage = Color.GREEN;
		Pane stack = new StackPane();
		Rectangle cardRect = new Rectangle(100, 120, cardImage);
		Font font = new Font(14);
		StringBuilder str = new StringBuilder(card.getName() + "\r\nB:" + card.getCost().getBricks() + " G:" + card.getCost().getGems() + " R:"
				+ card.getCost().getRecruiters());
		List<Action> actions = card.getActions();
		for (Action action : actions) {
			str.append("\r\nA:" + action.getType().toString().substring(0, 10));
		}
		Text text = new Text(str.toString());
		text.setFont(font);
		text.setFill(Color.WHITE);
		stack.getChildren().addAll(cardRect, text);
		stack.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				if (ownPlayerState.hasTurn()) {
					cardSelected(numberInHand);
				}
			};
		});
		return stack;
	}

	private void updateCardList() {
		cardList.setAlignment(Pos.CENTER);
		cardList.getChildren().clear();
		for (int i = 0; i < getCardListSize(); i++) {
			if (cards[i] != null) {
				cardList.getChildren().add(createCard(cards[i], i));
			}
		}
	}

	public void update(PlayerState ownState, PlayerState enemyState) {
		ownPlayerState = ownState;
		enemyPlayerState = enemyState;
		turnSign.setText(ownPlayerState.hasTurn() ? "YOUR TURN" : "WAIT FOR TURN");
		leftBricksNumber.setText(Integer.toString(ownState.getBricks()));
		leftDungeonNumber.setText(Integer.toString(ownState.getDungeon()));
		leftGemsNumber.setText(Integer.toString(ownState.getGems()));
		leftMagicNumber.setText(Integer.toString(ownState.getMagic()));
		leftQuerryNumber.setText(Integer.toString(ownState.getQuarry()));
		leftRecruitsNumber.setText(Integer.toString(ownState.getRecruiters()));
		leftTowerNumber.setText(Integer.toString(ownState.getTower()));
		leftWallNumber.setText(Integer.toString(ownState.getWall()));
		rightBricksNumber.setText(Integer.toString(enemyState.getBricks()));
		rightDungeonNumber.setText(Integer.toString(enemyState.getDungeon()));
		rightGemsNumber.setText(Integer.toString(enemyState.getGems()));
		rightMagicNumber.setText(Integer.toString(enemyState.getMagic()));
		rightQuerryNumber.setText(Integer.toString(enemyState.getQuarry()));
		rightRecruitsNumber.setText(Integer.toString(enemyState.getRecruiters()));
		rightTowerNumber.setText(Integer.toString(enemyState.getTower()));
		rightWallNumber.setText(Integer.toString(enemyState.getWall()));
		List<Long> hand = ownState.getHand();
		for (int i = 0; i < cards.length; i++) {
			if (hand.size() > i) {
				cards[i] = cardBase.getCardById(hand.get(i));
			} else {
				cards[i] = null;
			}
		}
		updateCardList();
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
		updateCardList();
		fillUserState();
		fillEnemyState();
		fillCenter();
		mainBorderPane.setBottom(cardList);
		mainBorderPane.setLeft(userState);
		mainBorderPane.setRight(enemyState);
		mainBorderPane.setCenter(center);
		turnSign = new Text();
		Font font = new Font(20);
		turnSign.setFont(font);
		mainBorderPane.setTop(turnSign);
		stage.setScene(scene);
	}

	public Stage getStage() {
		return stage;
	}

	public void start(ClientHandle serverHandle) {
		this.serverHandle = serverHandle;
		serverHandle.addMessageHandler(this);
		msgService.send(serverHandle, new Message(new RequestGameTableUpdate()));
	}
	
	public void cardSelected(int numberInHand) {
		GameUserAction action = new GameUserAction();
		action.setChoosenCardInHandNumber(numberInHand);
		action.setHasTarget(false);
		msgService.send(serverHandle, new Message(action));
	}

	@Override
	public boolean accept(Message message, ClientHandle caller) {
		logger.info("GameTableFx accepts msg=" + message);
		if (message.getClassName().equals(GameTableUpdate.class.getName())) {
			final GameTableUpdate update = message.unpackMessage();
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					update(update.getOwnState(), update.getEnemyState());
				}
			});
		}
		return false;
	}

	public static class TestFXApp extends Application {

		@Override
		public void start(Stage arg0) throws Exception {
			GameTableFX ui = new GameTableFX();
			arg0.setScene(ui.getScene());
			arg0.show();
			CardBase cardBase = new CardBaseImpl();
			List<Long> ownHand = new ArrayList<Long>();
			List<Long> enemyHand = new ArrayList<Long>();
			List<Card> allCards = cardBase.getAllCards();
			for (int i=0;i<5;i++) {
				ownHand.add(allCards.get(i).getId());
				enemyHand.add(allCards.get(i).getId());
			}
			PlayerState ownState = new PlayerState();
			ownState.setHand(ownHand);
			ownState.setHasTurn(true);
			PlayerState enemyState = new PlayerState();
			enemyState.setHand(enemyHand);
			ui.update(ownState, enemyState);
		}
	}

	public static void main(String[] args) {
		Application.launch(TestFXApp.class, args);
	}
}

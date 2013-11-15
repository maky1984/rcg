package com.rcg.game.model.server.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rcg.game.model.server.Player;
import com.rcg.game.model.server.PlayerBase;

public class PlayerBaseImpl implements PlayerBase {

	public static final String DEFAULT_FILENAME = "players.xml"; 
	
	private static final String TAG_PLAYERS = "players";
	private static final String TAG_PLAYER = "player";
	private static final String TAG_PLAYER_ATTRIBUTE = "attribute";
	private static final String TAG_PLAYER_NAME = "name";
	private static final String TAG_PLAYER_ID = "id";
	private static final String TAG_PLAYER_CARDS = "cards";
	private static final String TAG_PLAYER_CARDS_ID = "id";
	private static final String TAG_PLAYER_DECKS = "decks";
	private static final String TAG_PLAYER_DECKS_ID = "id";

	private String filename;
	private Map<Long, Player> players;

	public PlayerBaseImpl() {
		this(DEFAULT_FILENAME);
	}
	
	public PlayerBaseImpl(String filename) {
		this.filename = filename;
		readPlayers();
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public void refresh() {
		readPlayers();
	}

	@Override
	public Player getPlayerById(long id) {
		return players.get(id);
	}

	@Override
	public void addPlayer(Player player) {
		players.put(player.getId(), player);
		writePlayers();
	}

	@Override
	public void removePlayer(Player player) {
		players.remove(player.getId());
		writePlayers();
	}

	@Override
	public void updatePlayer(Player player) {
		players.put(player.getId(), player);
		writePlayers();
	}

	private Player createPlayer(long id, String name, List<Long> cardIds, List<Long> deckIds) {
		return new PlayerImpl(id, name, cardIds, deckIds);
	}

	private void readPlayers() {
		players = new HashMap<Long, Player>();
		try {
			File file = new File(filename);
			if (file.exists()) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(file);
				// optional, but recommended
				// read this -
				// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
				doc.getDocumentElement().normalize();
				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				NodeList nodes = doc.getElementsByTagName(TAG_PLAYER);
				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);
					System.out.println("Current Element :" + node.getNodeName());
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						long id = Long.parseLong(element.getElementsByTagName(TAG_PLAYER_ID).item(0).getTextContent());
						String name = element.getElementsByTagName(TAG_PLAYER_NAME).item(0).getTextContent();
						NodeList cards = ((Element)element.getElementsByTagName(TAG_PLAYER_CARDS).item(0)).getElementsByTagName(TAG_PLAYER_CARDS_ID);
						List<Long> cardIds = new ArrayList<>();
						for (int j = 0; j < cards.getLength(); j++) {
							Node cardNode = cards.item(j);
							if (cardNode.getNodeType() == Node.ELEMENT_NODE) {
								Element cardEl = (Element) cardNode;
								Long cardId = new Long(cardEl.getTextContent());
								cardIds.add(cardId);
							}
						}
						NodeList decks = ((Element)element.getElementsByTagName(TAG_PLAYER_DECKS).item(0)).getElementsByTagName(TAG_PLAYER_DECKS_ID);
						List<Long> deckIds = new ArrayList<>();
						for (int j = 0; j < decks.getLength(); j++) {
							Node deckNode = decks.item(j);
							if (deckNode.getNodeType() == Node.ELEMENT_NODE) {
								Element deckEl = (Element) deckNode;
								Long deckId = new Long(deckEl.getTextContent());
								deckIds.add(deckId);
							}
						}
						System.out.println("Player id:" + id + " name:" + name);
						Player player = createPlayer(id, name, cardIds, deckIds);
						players.put(id, player);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writePlayers() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			// root elements
			Document doc = docBuilder.newDocument();
			Element players = doc.createElement(TAG_PLAYERS);
			doc.appendChild(players);

			for (Player currentPlayer : this.players.values()) {

				// player elements
				Element player = doc.createElement(TAG_PLAYER);
				players.appendChild(player);

				// set attribute to player element
				Attr attr = doc.createAttribute(TAG_PLAYER_ATTRIBUTE);
				attr.setValue("reserved");
				player.setAttributeNode(attr);
				// shorten way
				// player.setAttribute("id", "1");

				// name element
				Element name = doc.createElement(TAG_PLAYER_NAME);
				name.appendChild(doc.createTextNode(currentPlayer.getName()));
				player.appendChild(name);

				// id element
				Element id = doc.createElement(TAG_PLAYER_ID);
				id.appendChild(doc.createTextNode(Long.toString(currentPlayer.getId())));
				player.appendChild(id);

				Element cards = doc.createElement(TAG_PLAYER_CARDS);
				for (Long cardId : currentPlayer.getAllCardIds()) {
					Element cardIdEl = doc.createElement(TAG_PLAYER_CARDS_ID);
					cardIdEl.appendChild(doc.createTextNode(Long.toString(cardId)));
					cards.appendChild(cardIdEl);
				}
				player.appendChild(cards);

				Element decks = doc.createElement(TAG_PLAYER_DECKS);
				for (Long deckId : currentPlayer.getAllDeckIds()) {
					Element deckIdEl = doc.createElement(TAG_PLAYER_DECKS_ID);
					deckIdEl.appendChild(doc.createTextNode(Long.toString(deckId)));
					decks.appendChild(deckIdEl);
				}
				player.appendChild(decks);
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult resultFile = new StreamResult(new File(filename));
			// Output to console for testing
			StreamResult resultConsole = new StreamResult(System.out);

			transformer.transform(source, resultFile);
			transformer.transform(source, resultConsole);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

}

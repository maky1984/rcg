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

import com.rcg.game.model.Card;
import com.rcg.game.model.Deck;
import com.rcg.game.model.impl.DeckImpl;
import com.rcg.game.model.server.CardBase;
import com.rcg.game.model.server.DeckBase;

public class DeckBaseImpl implements DeckBase {

	public static final String DEFAULT_FILENAME = "decks.xml";

	private static final String TAG_DECKS = "decks";
	private static final String TAG_DECK = "deck";
	private static final String TAG_DECK_ATTRIBUTE = "attribute";
	private static final String TAG_DECK_NAME = "name";
	private static final String TAG_DECK_ID = "id";
	private static final String TAG_DECK_CARDS = "cards";
	private static final String TAG_DECK_CARDS_ID = "id";

	private String filename;
	private Map<Long, Deck> decks;

	private CardBase cardBase;

	public DeckBaseImpl(CardBase cardBase) {
		this(DEFAULT_FILENAME, cardBase);
	}

	public DeckBaseImpl(String filename, CardBase cardBase) {
		this.filename = filename;
		this.cardBase = cardBase;
		readDecks();
	}

	@Override
	public void addDeck(Deck deck) {
		decks.put(deck.getId(), deck);
		writeDecks();
	}

	@Override
	public Deck getDeckById(long id) {
		return decks.get(id);
	}

	public Card getCardById(long cardId) {
		return cardBase.getCardById(cardId);
	}

	@Override
	public void removeDeck(Deck deck) {
		decks.remove(deck.getId());
		writeDecks();
	}

	@Override
	public void updateDeck(Deck deck) {
		decks.put(deck.getId(), deck);
	}

	private Deck createDeck(long id, String name, List<Long> cardIds) {
		Deck deck = new DeckImpl(id, name, cardIds);
		deck.setCardBase(cardBase);
		return deck;
	}

	private void readDecks() {
		decks = new HashMap<Long, Deck>();
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
				NodeList nodes = doc.getElementsByTagName(TAG_DECK);
				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);
					System.out.println("Current Element :" + node.getNodeName());
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						long id = Long.parseLong(element.getElementsByTagName(TAG_DECK_ID).item(0).getTextContent());
						String name = element.getElementsByTagName(TAG_DECK_NAME).item(0).getTextContent();
						NodeList cards = ((Element) element.getElementsByTagName(TAG_DECK_CARDS).item(0)).getElementsByTagName(TAG_DECK_CARDS_ID);
						List<Long> cardIds = new ArrayList<>();
						for (int j = 0; j < cards.getLength(); j++) {
							Node cardNode = cards.item(j);
							if (cardNode.getNodeType() == Node.ELEMENT_NODE) {
								Element cardEl = (Element) cardNode;
								Long cardId = new Long(cardEl.getTextContent());
								cardIds.add(cardId);
							}
						}
						System.out.println("Deck id:" + id + " name:" + name);
						Deck deck = createDeck(id, name, cardIds);
						decks.put(id, deck);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeDecks() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			// root elements
			Document doc = docBuilder.newDocument();
			Element decks = doc.createElement(TAG_DECKS);
			doc.appendChild(decks);

			for (Deck currentDeck : this.decks.values()) {

				// player elements
				Element deck = doc.createElement(TAG_DECK);
				decks.appendChild(deck);

				// set attribute to player element
				Attr attr = doc.createAttribute(TAG_DECK_ATTRIBUTE);
				attr.setValue("reserved");
				deck.setAttributeNode(attr);
				// shorten way
				// player.setAttribute("id", "1");

				// name element
				Element name = doc.createElement(TAG_DECK_NAME);
				name.appendChild(doc.createTextNode(currentDeck.getName()));
				deck.appendChild(name);

				// id element
				Element id = doc.createElement(TAG_DECK_ID);
				id.appendChild(doc.createTextNode(Long.toString(currentDeck.getId())));
				deck.appendChild(id);

				Element cards = doc.createElement(TAG_DECK_CARDS);
				for (Long cardId : currentDeck.getAllCardIds()) {
					Element cardIdEl = doc.createElement(TAG_DECK_CARDS_ID);
					cardIdEl.appendChild(doc.createTextNode(Long.toString(cardId)));
					cards.appendChild(cardIdEl);
				}
				deck.appendChild(cards);
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

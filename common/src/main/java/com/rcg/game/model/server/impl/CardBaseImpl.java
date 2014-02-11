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

import com.rcg.game.model.Action;
import com.rcg.game.model.Card;
import com.rcg.game.model.CardCost;
import com.rcg.game.model.Action.ActionType;
import com.rcg.game.model.impl.ActionImpl;
import com.rcg.game.model.impl.CardCostImpl;
import com.rcg.game.model.impl.CardImpl;
import com.rcg.game.model.server.CardBase;

public class CardBaseImpl implements CardBase {

	public static final String DEFAULT_FILENAME = "cards.xml";

	private static final String TAG_CARDS = "cards";
	private static final String TAG_CARD = "card";
	private static final String TAG_CARD_ATTRIBUTE = "attribute";
	private static final String TAG_CARD_NAME = "name";
	private static final String TAG_CARD_ID = "id";
	private static final String TAG_CARD_COST = "cost";
	private static final String TAG_CARD_COST_BRICKS = "bricks";
	private static final String TAG_CARD_COST_GEMS = "gems";
	private static final String TAG_CARD_COST_RECRUITERS = "recruiters";
	private static final String TAG_CARD_ACTIONS = "actions";
	private static final String TAG_CARD_ACTION = "action";
	private static final String TAG_CARD_ACTION_TYPE = "type";
	private static final String TAG_CARD_ACTION_VALUE = "value";

	private String filename;

	private Map<Long, Card> cards;

	public CardBaseImpl() {
		this(DEFAULT_FILENAME);
	}
	
	public CardBaseImpl(String filename) {
		this.filename = filename;
		readCards();
	}

	@Override
	public Card getCardById(long id) {
		return cards.get(id);
	}

	@Override
	public void removeCard(Card card) {
		cards.remove(card);
		writeCards();
	}

	@Override
	public void addCard(Card card) {
		cards.put(card.getId(), card);
		writeCards();
	}

	@Override
	public void updateCard(Card card) {
		cards.put(card.getId(), card);
		writeCards();
	}

	@Override
	public void addCard(Card... cards) {
		for (Card card : cards) {
			this.cards.put(card.getId(), card);
		}
		writeCards();
	}

	@Override
	public List<Card> getAllCards() {
		return new ArrayList<>(cards.values());
	}
	
	private CardCost createCost(int bricks, int gems, int recruiters) {
		CardCost cost = new CardCostImpl(bricks, gems, recruiters);
		return cost;
	}
	
	private Action createAction(ActionType actionType, List<Integer> actionValues) {
		Action action = new ActionImpl(actionType, actionValues);
		return action;
	}
	
	private Card createCard(long id, String name, CardCost cost, List<Action> actions) {
		Card card = new CardImpl(id, name, cost, actions);
		return card;
	}

	private void readCards() {
		cards = new HashMap<Long, Card>();
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
				NodeList nodes = doc.getElementsByTagName(TAG_CARD);
				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);
					System.out.println("Current Element :" + node.getNodeName());
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						CardCost cost = null;
						List<Action> actions = new ArrayList<>();
						Element element = (Element) node;
						long id = Long.parseLong(element.getElementsByTagName(TAG_CARD_ID).item(0).getTextContent());
						String name = element.getElementsByTagName(TAG_CARD_NAME).item(0).getTextContent();
						Node costNode = element.getElementsByTagName(TAG_CARD_COST).item(0);
						if (costNode.getNodeType() == Node.ELEMENT_NODE) {
							Element costElement = (Element) costNode;
							int bricks = Integer.parseInt(costElement.getElementsByTagName(TAG_CARD_COST_BRICKS).item(0).getTextContent());
							int gems = Integer.parseInt(costElement.getElementsByTagName(TAG_CARD_COST_GEMS).item(0).getTextContent());
							int recruiters = Integer.parseInt(costElement.getElementsByTagName(TAG_CARD_COST_RECRUITERS).item(0).getTextContent());
							cost = createCost(bricks, gems, recruiters);
						}

						NodeList actionNodes = ((Element) element.getElementsByTagName(TAG_CARD_ACTIONS).item(0)).getElementsByTagName(TAG_CARD_ACTION);
						for (int j = 0; j < actionNodes.getLength(); j++) {
							Node actionNode = actionNodes.item(j);
							if (actionNode.getNodeType() == Node.ELEMENT_NODE) {
								Element actionEl = (Element) actionNode;
								String actionType = actionEl.getElementsByTagName(TAG_CARD_ACTION_TYPE).item(0).getTextContent();
								NodeList actionValueNodes = actionEl.getElementsByTagName(TAG_CARD_ACTION_VALUE);
								List<Integer> actionValues = new ArrayList<>();
								for (int k = 0; k < actionValueNodes.getLength(); k++) {
									Node actionValueNode = actionValueNodes.item(k);
									if (actionValueNode.getNodeType() == Node.ELEMENT_NODE) {
										Element valueEl = (Element)actionValueNode;
										Integer value = new Integer(valueEl.getTextContent());
										actionValues.add(value);
									}
								}
								Action action = createAction(ActionType.valueOf(actionType), actionValues);
								actions.add(action);
							}
						}
						System.out.println("Card id:" + id + " name:" + name);
						Card card = createCard(id, name, cost, actions);
						cards.put(id, card);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeCards() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			// root elements
			Document doc = docBuilder.newDocument();
			Element decks = doc.createElement(TAG_CARDS);
			doc.appendChild(decks);

			for (Card currentCard : this.cards.values()) {

				// player elements
				Element card = doc.createElement(TAG_CARD);
				decks.appendChild(card);

				// set attribute to player element
				Attr attr = doc.createAttribute(TAG_CARD_ATTRIBUTE);
				attr.setValue("reserved");
				card.setAttributeNode(attr);
				// shorten way
				// player.setAttribute("id", "1");

				// name element
				Element name = doc.createElement(TAG_CARD_NAME);
				name.appendChild(doc.createTextNode(currentCard.getName()));
				card.appendChild(name);

				// id element
				Element id = doc.createElement(TAG_CARD_ID);
				id.appendChild(doc.createTextNode(Long.toString(currentCard.getId())));
				card.appendChild(id);

				Element cost = doc.createElement(TAG_CARD_COST);

				Element bricks = doc.createElement(TAG_CARD_COST_BRICKS);
				bricks.appendChild(doc.createTextNode(Integer.toString(currentCard.getCost().getBricks())));
				cost.appendChild(bricks);

				Element gems = doc.createElement(TAG_CARD_COST_GEMS);
				gems.appendChild(doc.createTextNode(Integer.toString(currentCard.getCost().getGems())));
				cost.appendChild(gems);

				Element recruiters = doc.createElement(TAG_CARD_COST_RECRUITERS);
				recruiters.appendChild(doc.createTextNode(Integer.toString(currentCard.getCost().getRecruiters())));
				cost.appendChild(recruiters);

				card.appendChild(cost);

				Element actions = doc.createElement(TAG_CARD_ACTIONS);
				for (Action action : currentCard.getActions()) {

					Element actionEl = doc.createElement(TAG_CARD_ACTION);

					Element type = doc.createElement(TAG_CARD_ACTION_TYPE);
					type.appendChild(doc.createTextNode(action.getType().name()));
					actionEl.appendChild(type);

					List<Integer> actionValues = action.getValues();
					for (Integer value : actionValues) {
						Element valueEl = doc.createElement(TAG_CARD_ACTION_VALUE);
						valueEl.appendChild(doc.createTextNode(value.toString()));
						actionEl.appendChild(valueEl);
					}

					actions.appendChild(actionEl);
				}
				card.appendChild(actions);
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

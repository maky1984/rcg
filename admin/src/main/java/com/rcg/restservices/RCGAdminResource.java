package com.rcg.restservices;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.game.model.Card;
import com.rcg.game.model.server.CardBase;

@Path("/")
public class RCGAdminResource {

	private static final Logger logger = LoggerFactory.getLogger(RCGAdminResource.class);

	private CardBase cardBase;

	@GET
	@Path("/card/{id}")
	public Card getCard(@PathParam(value = "id") String id) {
		Card card = cardBase.getCardById(Long.parseLong(id));
		return card;
	}

	@POST
	@Path("/card")
	public void putCard(Card card) {
		if (cardBase.getCardById(card.getId()) == null) {
			cardBase.addCard(card);
		} else {
			cardBase.updateCard(card);
		}
	}

	@DELETE
	@Path("/card/{id}")
	public void deleteCard(@PathParam(value = "id") String id) {
		Card card = cardBase.getCardById(Long.parseLong(id));
		if (card != null) {
			cardBase.removeCard(card);
		}
	}

}

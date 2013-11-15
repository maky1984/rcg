package com.rcg.restservices.view;

import java.util.List;

public class CardView {
	
	private String name;
	private long id;
	private List<ActionView> actions;
	private CardCostView cost;
	
	public List<ActionView> getActions() {
		return actions;
	}
	
	public CardCostView getCost() {
		return cost;
	}
	
	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setActions(List<ActionView> actions) {
		this.actions = actions;
	}
	
	public void setCost(CardCostView cost) {
		this.cost = cost;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}

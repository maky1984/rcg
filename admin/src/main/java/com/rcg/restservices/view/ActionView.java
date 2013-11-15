package com.rcg.restservices.view;

import java.util.List;

public class ActionView {

	private int type;
	private List<Integer> values;

	public void setType(int type) {
		this.type = type;
	}
	
	public void setValues(List<Integer> values) {
		this.values = values;
	}
	
	public int getType() {
		return type;
	}
	
	public List<Integer> getValues() {
		return values;
	}
	
}

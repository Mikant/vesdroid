package com.app.vesdroid.Model;

import java.util.ArrayList;
import java.util.UUID;

public class Protocol {

	
	private UUID _id;
	private String name;
	private ArrayList<ABMN> ABMNs;
	
	public UUID getId() {
		return _id;
	}

	public void setId(UUID id) {
		this._id = id;
	}
	
	public void setId(String id) {
		this._id = UUID.fromString(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<ABMN> getABMNs() {
		return ABMNs;
	}

	public Protocol(){
		name = "Новый протокол";
		ABMNs = new ArrayList<ABMN>();
	}
	
	@Override
	public String toString(){
		return name;
	}
	

}

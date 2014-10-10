package com.app.vesdroid.Model;

import java.util.ArrayList;
import java.util.UUID;

public class Picket extends NameComment {
	private UUID profileId;
	private ArrayList<Record> records;
	
	public UUID getProfileId() {
		return profileId;
	}

	public void setProfileId(UUID profileId) {
		this.profileId = profileId;
	}
	
	public void setProfileId(String profileId) {
		this.profileId = UUID.fromString(profileId);
	}
/*
	public ArrayList<Record> getRecords() {
		return records;
	}
*/
	public Picket(){
		setName("Расположение пикета");
		setComment("Комментарий для пикета");
		records = new ArrayList<Record>();
	}
}

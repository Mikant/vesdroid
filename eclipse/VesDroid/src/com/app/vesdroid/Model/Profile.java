package com.app.vesdroid.Model;

import java.util.ArrayList;
import java.util.UUID;

public class Profile extends NameComment {
	private UUID projectId;
	private ArrayList<Picket> pickets;
	
	public UUID getProjectId() {
		return projectId;
	}

	public void setProjectId(UUID projectId) {
		this.projectId = projectId;
	}
	
	public void setProjectId(String projectId) {
		this.projectId = UUID.fromString(projectId);
	}
	/*
	public ArrayList<Picket> getPickets(){
		return pickets;
	}
*/
	public Profile(){
		setName("Новый профиль");
		setComment("Комментарий для профиля");
		pickets = new ArrayList<Picket>();
	}
}

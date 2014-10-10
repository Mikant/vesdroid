package com.app.vesdroid.Model;

import java.util.ArrayList;

public class Project extends NameComment {
	private ArrayList<Profile> profiles;
	/*
	public ArrayList<Profile> getProfiles(){
		return profiles;
	}*/
	
	public Project(){
		setName("Новый проект");
		setComment("Комментарий для проекта");
		profiles = new ArrayList<Profile>();
	}
}

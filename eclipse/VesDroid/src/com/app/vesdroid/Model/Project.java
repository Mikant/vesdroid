package com.app.vesdroid.Model;

import java.util.ArrayList;

public class Project extends NameComment {
	private ArrayList<Profile> profiles;
	/*
	public ArrayList<Profile> getProfiles(){
		return profiles;
	}*/
	
	public Project(){
		setName("����� ������");
		setComment("����������� ��� �������");
		profiles = new ArrayList<Profile>();
	}
}

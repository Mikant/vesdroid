package com.app.vesdroid.Model;

import java.util.UUID;

public class NameComment {
	private UUID _id;
	private String name;
	private String comment;
	
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return name;
	}
}

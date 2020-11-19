package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "comment")
public class Comment extends AbstractEntity implements Serializable {

	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

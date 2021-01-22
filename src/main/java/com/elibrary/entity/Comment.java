package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "Comment")
public class Comment extends AbstractEntity implements Serializable {

	@JsonView(Views.Thin.class)
	private String description;

	public String getDescription() {
		if (description == null)
			return "";
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "comment")
<<<<<<< Updated upstream
public class Comment extends AbstractEntity implements Serializable {

	private String description;
=======
public class Comment {
	
	private String comment;
>>>>>>> Stashed changes

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}

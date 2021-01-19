package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "feedback")
public class Feedback extends AbstractEntity implements Serializable {

	@JsonView(Views.Thin.class)
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User userId;

	@JsonView(Views.Thin.class)
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "replyId")
	private Reply replyId;

	@JsonView(Views.Thin.class)
	private String message;

	public Reply getReplyId() {
		if (replyId == null)
			replyId = new Reply();
		return replyId;
	}

	public void setReplyId(Reply replyId) {
		this.replyId = replyId;
	}

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

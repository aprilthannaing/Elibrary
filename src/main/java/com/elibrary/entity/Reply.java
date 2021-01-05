package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "reply")
public class Reply extends AbstractEntity implements Serializable {

	@JsonView(Views.Thin.class)
	private String dateTime;

	@JsonView(Views.Thin.class)
	private String message;

	@org.hibernate.annotations.Type(type="true_false")
	@NotNull
	@JsonView(Views.Thin.class)
	private boolean viewStatus;

	public boolean isViewStatus() {
		return viewStatus;
	}

	public void setViewStatus(boolean viewStatus) {
		this.viewStatus = viewStatus;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "history")
public class History extends AbstractEntity implements Serializable {

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User userId;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "bookId")
	private Book bookId;

	@JsonView(Views.Thin.class)
	private long ratingId;

	private String dateTime;

	@Column(name = "actionStatus")
	@Enumerated(EnumType.STRING)
	private ActionStatus actionStatus;

	public long getRatingId() {
		return ratingId;
	}

	public void setRatingId(long ratingId) {
		this.ratingId = ratingId;
	}

	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

	public Book getBookId() {
		return bookId;
	}

	public void setBookId(Book bookId) {
		this.bookId = bookId;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public static boolean isValidAction(ActionStatus status) {
		return status == ActionStatus.BOOKMARK || status == ActionStatus.READ || status == ActionStatus.FAVOURITE || status == ActionStatus.RATING || status == ActionStatus.DOWNLOAD || status == ActionStatus.UNBOOKMARK || status == ActionStatus.UNFAVOURITE;
	}

	public static boolean isRating(ActionStatus status) {
		return status == ActionStatus.RATING;
	}

	public static boolean isUnFavourite(ActionStatus status) {
		return status == ActionStatus.UNFAVOURITE;
	}

	public static boolean isUnBookMark(ActionStatus status) {
		return status == ActionStatus.UNBOOKMARK;
	}

}

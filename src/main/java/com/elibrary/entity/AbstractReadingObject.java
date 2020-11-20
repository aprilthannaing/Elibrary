package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonView;

@MappedSuperclass
public class AbstractReadingObject extends AbstractEntity implements Serializable {

	@Column(name = "title")
	@JsonView(Views.Thin.class)
	private String title;

	@Column(name = "publishedDate")
	@JsonView(Views.Thin.class)
	private String publishedDate;

	@Column(name = "volume")
	@JsonView(Views.Thin.class)
	private String volume;

	@Column(name = "state")
	@JsonView(Views.Thin.class)
	@Enumerated(EnumType.STRING)
	private State state;

	@Column(name = "modifiedDate")
	@JsonView(Views.Thin.class)
	private String modifiedDate;

	@JsonView(Views.Thin.class)
	@Column(name = "createdDate")
	private String createdDate;

	@JsonView(Views.Thin.class)
	@Column(name = "coverPhoto")
	private String coverPhoto;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCoverPhoto() {
		return coverPhoto;
	}

	public void setCoverPhoto(String coverPhoto) {
		this.coverPhoto = coverPhoto;
	}

}
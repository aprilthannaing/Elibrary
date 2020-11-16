package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractReadingObject extends AbstractEntity implements Serializable{
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "publishedDate")
	private String publishedDate;
	
	@Column(name = "volume")
	private int volume;
	
	@Column(name = "state")
	private State state;
	
	@Column(name = "modifiedDate")
	private String modifiedDate;
	
	@Column(name = "createdDate")
	private String createdDate;
	
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

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
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
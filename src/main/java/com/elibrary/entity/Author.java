package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "author")
public class Author extends AbstractEntity implements Serializable{
	private String engName;
	
	private String myanmarName;

	public String getEngName() {
		return engName;
	}

	public void setEngName(String engName) {
		this.engName = engName;
	}

	public String getMyanmarName() {
		return myanmarName;
	}

	public void setMyanmarName(String myanmarName) {
		this.myanmarName = myanmarName;
	}
	
	
}

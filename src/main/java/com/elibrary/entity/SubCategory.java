package com.elibrary.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "subCategory")
public class SubCategory extends AbstractEntity implements Serializable {

	@JsonView(Views.Thin.class)
	private String myanmarName;

	@JsonView(Views.Thin.class)
	private String engName;

	@JsonView(Views.Thin.class)
	private double priority;

	@Transient
	@JsonView(Views.Thin.class)
	private long bookCount;

	@JsonView(Views.Thin.class)
	private String display;

	public long getBookCount() {
		return bookCount;
	}

	public void setBookCount(long bookCount) {
		this.bookCount = bookCount;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public double getPriority() {
		return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public String getMyanmarName() {
		return myanmarName;
	}

	public void setMyanmarName(String myanmarName) {
		this.myanmarName = myanmarName;
	}

	public String getEngName() {
		return engName;
	}

	public void setEngName(String engName) {
		this.engName = engName;
	}

	public boolean isDisplay() {
		return Boolean.valueOf(getDisplay());
	}

}

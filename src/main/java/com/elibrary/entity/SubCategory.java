package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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

	@JsonView(Views.Thin.class)
	@JoinColumn(name = "categoryId")
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Category category;

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

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
		if (myanmarName == null)
			return "";
		return myanmarName;
	}

	public void setMyanmarName(String myanmarName) {
		this.myanmarName = myanmarName;
	}

	public String getEngName() {
		if (engName == null)
			return "";
		return engName;
	}

	public void setEngName(String engName) {
		this.engName = engName;
	}

	public boolean isDisplay() {
		return Boolean.valueOf(getDisplay());
	}

}

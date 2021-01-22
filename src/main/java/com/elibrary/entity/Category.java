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
@Table(name = "Category")
public class Category extends AbstractEntity implements Serializable {

	@JsonView(Views.Thin.class)
	private String myanmarName;

	@JsonView(Views.Thin.class)
	private String engName;

	@JsonView(Views.Summary.class)
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "Category_SubCategory", joinColumns = @JoinColumn(name = "categoryId"), inverseJoinColumns = @JoinColumn(name = "subCategoryId"))
	private List<SubCategory> subCategories;

	@JsonView(Views.Thin.class)
	private double priority;

	@Transient
	@JsonView(Views.Thin.class)
	private long bookCount;

	@JsonView(Views.Thin.class)
	private String icon;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public long getBookCount() {
		return bookCount;
	}

	public void setBookCount(long bookCount) {
		this.bookCount = bookCount;
	}

	public double getPriority() {
		return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public List<SubCategory> getSubCategories() {
		if (subCategories == null)
			subCategories = new ArrayList<SubCategory>();
		return subCategories;
	}

	public void setSubCategories(List<SubCategory> subCategories) {
		this.subCategories = subCategories;
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

}

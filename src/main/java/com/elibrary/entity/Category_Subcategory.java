package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "category_subcategory")
public class Category_Subcategory{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private long Id;
	
	@Column(name = "categoryId")
	@JsonView(Views.Thin.class)
	private long categoryId;
	
	@Column(name = "subcategoryId")
	@JsonView(Views.Thin.class)
	private long subcategoryId;

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getSubcategoryId() {
		return subcategoryId;
	}

	public void setSubcategoryId(Long subcategoryId) {
		this.subcategoryId = subcategoryId;
	}

	
	
	
}

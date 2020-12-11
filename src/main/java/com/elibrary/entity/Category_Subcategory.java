package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Category_SubCategory")
public class Category_SubCategory implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private long Id;

	@Column(name = "categoryId", unique = true, nullable = false)
	private long categoryId;

	@Column(name = "subCategoryId", unique = true, nullable = false)
	private long subCategoryId;

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public long getSubCategoryId() {
		return subCategoryId;
	}

	public void setSubCategoryId(long subCategoryId) {
		this.subCategoryId = subCategoryId;
	}

}

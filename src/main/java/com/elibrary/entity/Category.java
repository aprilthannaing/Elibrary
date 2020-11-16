package com.elibrary.entity;

import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

import java.io.Serializable;

import javax.persistence.Entity;

@Entity
@Table(name = "category")
public class Category extends AbstractEntity implements Serializable {

    @JsonView(Views.Thin.class)
	private CategoryType type;

	public CategoryType getType() {
		return type;
	}

	public void setType(CategoryType type) {
		this.type = type;
	}

}

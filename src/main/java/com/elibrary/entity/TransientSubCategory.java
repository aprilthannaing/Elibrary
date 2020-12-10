package com.elibrary.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

public class TransientSubCategory {

	@JsonView(Views.Thin.class)
	private String alphabet;

	@JsonView(Views.Thin.class)
	private List<SubCategory> subcategories;

	public String getAlphabet() {
		return alphabet;
	}

	public void setAlphabet(String alphabet) {
		this.alphabet = alphabet;
	}

	public List<SubCategory> getSubcategories() {
		return subcategories;
	}

	public void setSubcategories(List<SubCategory> subcategories) {
		this.subcategories = subcategories;
	}

}

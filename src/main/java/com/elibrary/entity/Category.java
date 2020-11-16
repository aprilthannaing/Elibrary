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

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "category")
public class Category extends AbstractEntity implements Serializable {

	@JsonView(Views.Thin.class)
	private String name;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "category_subcategory", joinColumns = @JoinColumn(name = "categoryBoId"), inverseJoinColumns = @JoinColumn(name = "subCategoryBoId"))
	private List<SubCategory> subCategories;

	public List<SubCategory> getSubCategories() {
		if(subCategories == null)
			subCategories = new ArrayList<SubCategory>();
		return subCategories;
	}

	public void setSubCategories(List<SubCategory> subCategories) {		
		this.subCategories = subCategories;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

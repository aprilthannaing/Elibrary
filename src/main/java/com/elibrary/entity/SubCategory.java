package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "subCategory")
public class SubCategory extends AbstractEntity implements Serializable {

	@JsonView(Views.Thin.class)
	private String name;

	@JsonView(Views.Thin.class)
	private double priority;

	public double getPriority() {
		return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

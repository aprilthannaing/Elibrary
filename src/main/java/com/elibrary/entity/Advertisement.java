package com.elibrary.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "advertisement")
public class Advertisement extends AbstractEntity{
	
	@JsonView(Views.Thin.class)
	private String name;

	@JsonView(Views.Thin.class)
	private String pdf;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPdf() {
		return pdf;
	}

	public void setPdf(String pdf) {
		this.pdf = pdf;
	}
	
}

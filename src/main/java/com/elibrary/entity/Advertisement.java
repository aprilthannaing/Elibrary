package com.elibrary.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "Advertisement")
public class Advertisement extends AbstractEntity {

	@JsonView(Views.Thin.class)
	private String name;

	@JsonView(Views.Thin.class)
	private String pdf;

	@JsonView(Views.Thin.class)
	@Column(name = "linkType")
	@Enumerated(EnumType.STRING)
	private LinkType linkType;

	@JsonView(Views.Thin.class)
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private Type type;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

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

	public LinkType getLinkType() {
		return linkType;
	}

	public void setLinkType(LinkType linkType) {
		this.linkType = linkType;
	}

}

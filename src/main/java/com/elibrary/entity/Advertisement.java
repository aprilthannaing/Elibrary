package com.elibrary.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "advertisement")
public class Advertisement extends AbstractEntity {

	@JsonView(Views.Thin.class)
	private String name;

	@JsonView(Views.Thin.class)
	private String pdf;

	@JsonView(Views.Thin.class)
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private AdvertisementType type;

	@JsonView(Views.Thin.class)
	@Column(name = "linkType")
	private String linkType;

	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
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

	public AdvertisementType getType() {
		return type;
	}

	public void setType(AdvertisementType type) {
		this.type = type;
	}

}

package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "author")
public class Author extends AbstractEntity implements Serializable {

	@JsonView(Views.Thin.class)
	private String name;

	@JsonView(Views.Thin.class)
	private String sort;

	@JsonView(Views.Thin.class)
	private String profilePicture;

	@Column(name = "authorType")
	@Enumerated(EnumType.STRING)
	private AuthorType authorType;

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AuthorType getAuthorType() {
		return authorType;
	}

	public void setAuthorType(AuthorType authorType) {
		this.authorType = authorType;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

}

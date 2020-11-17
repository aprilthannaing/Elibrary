package com.elibrary.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "author")
public class Author extends AbstractEntity implements Serializable {

	private String name;

	private AuthorType authorType;

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

}

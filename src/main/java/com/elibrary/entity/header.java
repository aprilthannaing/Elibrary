package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "header")
public class header extends AbstractEntity implements Serializable{
	@Column(name = "name")
	public String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

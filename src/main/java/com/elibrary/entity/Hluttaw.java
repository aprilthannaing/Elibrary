package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "hluttaw")
public class Hluttaw extends AbstractEntity implements Serializable{
	
	private String code;
	private String name;
	private String hboId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getHboId() {
		return hboId;
	}

	public void setHboId(String hboId) {
		this.hboId = hboId;
	}
	
}

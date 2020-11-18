package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "department")
public class Department extends AbstractEntity implements Serializable{
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "hluttawboId")
	private Hluttaw hluttaw;
	
	private String name;
	
	private String code;
	
	public Hluttaw getHluttaw() {
		return hluttaw;
	}

	public void setHluttaw(Hluttaw hluttaw) {
		this.hluttaw = hluttaw;
	}

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
}

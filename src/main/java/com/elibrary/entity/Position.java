package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Position")
public class Position extends AbstractEntity implements Serializable {

	// @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @JoinColumn(name = "departmentboId")
	// private Department department;

	private String name;

	private String code;

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

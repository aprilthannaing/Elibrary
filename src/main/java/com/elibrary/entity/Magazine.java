package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Magazine")
public class Magazine extends AbstractReadingObject implements Serializable {

	private String monthlyNo;

	public String getMonthlyNo() {
		return monthlyNo;
	}

	public void setMonthlyNo(String monthlyNo) {
		this.monthlyNo = monthlyNo;
	}

}

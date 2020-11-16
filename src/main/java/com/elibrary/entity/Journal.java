package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "journal")
public class Journal extends AbstractReadingObject implements Serializable{
	
	private String weeklyNo;

	public String getWeeklyNo() {
		return weeklyNo;
	}

	public void setWeeklyNo(String weeklyNo) {
		this.weeklyNo = weeklyNo;
	}
	
	
	

}

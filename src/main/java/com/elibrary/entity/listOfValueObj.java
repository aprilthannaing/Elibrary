package com.elibrary.entity;

public class listOfValueObj {
	String code;
	String description;
	listOfValue[] lov;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public listOfValue[] getLov() {
		return lov;
	}
	public void setLov(listOfValue[] lov) {
		this.lov = lov;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
}

package com.elibrary.entity;

import com.fasterxml.jackson.annotation.JsonView;

public class Result {
	@JsonView(Views.Thin.class)
	private String description;
	
	@JsonView(Views.Thin.class)
	private String code;
	
	@JsonView(Views.Thin.class)
	private String result = "";
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}

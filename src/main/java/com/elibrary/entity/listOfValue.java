package com.elibrary.entity;

import com.fasterxml.jackson.annotation.JsonView;

public class listOfValue {
	@JsonView(Views.Thin.class)
	String value;
	@JsonView(Views.Thin.class)
	String caption;
	@JsonView(Views.Thin.class)
	String code;
	@JsonView(Views.Thin.class)
	String status;
	
	public listOfValue() {
		clearProperty();
	}

	void clearProperty() {
		value = "";
		caption = "";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String key) {
		this.value = key;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String value) {
		this.caption = value;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}

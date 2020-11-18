package com.elibrary.entity;

public class listOfValue {
	String key;
	String value;
	String id;
	String status;
	
	public listOfValue() {
		clearProperty();
	}

	void clearProperty() {
		key = "";
		value = "";
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}

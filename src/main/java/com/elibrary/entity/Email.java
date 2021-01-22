package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Email")
public class Email extends AbstractEntity implements Serializable {

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "hluttawboId")
	private Hluttaw hluttaw;

	private String emailAddress;

	public Hluttaw getHluttaw() {
		return hluttaw;
	}

	public void setHluttaw(Hluttaw hluttaw) {
		this.hluttaw = hluttaw;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

}

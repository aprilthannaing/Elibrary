package com.elibrary.entity;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonView;

@MappedSuperclass
public class AbstractEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private long Id;

	@JsonView(Views.Thin.class)
	@Column(name = "boId",nullable = false)
	private String boId;

	@Column(name = "entityStatus")
	@Enumerated(EnumType.STRING)
	private EntityStatus entityStatus;

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public String getBoId() {
		return boId;
	}

	public void setBoId(String boId) {
		this.boId = boId;
	}

	public EntityStatus getEntityStatus() {
		return entityStatus;
	}

	public void setEntityStatus(EntityStatus entityStatus) {
		this.entityStatus = entityStatus;
	}

	public boolean isBoIdRequired(String boId) {
		return SystemConstant.BOID_REQUIRED.equals(boId);
	}
	
	public boolean isIdRequired(Long id) {
		return id == null || id == 0 || SystemConstant.ID_REQUIRED.equals(id);
	}
}

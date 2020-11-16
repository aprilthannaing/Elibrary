package com.elibrary.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractEntity {

	@Id
	@Column(name = "Id", unique = true, nullable = false)
	private long Id;

	@Column(name = "boId", unique = true, nullable = false)
	private String boId;

	@Column(name = "entityStatus")
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
}

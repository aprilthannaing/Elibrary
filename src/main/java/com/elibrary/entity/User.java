package com.elibrary.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "user")
public class User extends AbstractEntity implements Serializable {
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "constituency")
	private Constituency constituency;
	
	@Transient
	@JsonView(Views.Thin.class)
	private long constituencyType;
	
	@JsonView(Views.Thin.class)
	@Column(name = "permanentAddress")
	private String permanentAddress;
	
	@JsonView(Views.Thin.class)
	@Column(name = "currentAddress")
	private String currentAddress;
	
	@JsonView(Views.Thin.class)
	@Column(name = "name")
	private String name;

	@JsonView(Views.Thin.class)
	@Column(name = "email")
	private String email;

	@JsonView(Views.Thin.class)
	@Column(name = "phoneNo")
	private String phoneNo;

	@Column(name = "password")
	private String password;

	@JsonView(Views.Thin.class)
	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private UserRole role;

	@JsonView(Views.Thin.class)
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private UserType type;

	@Column(name = "createdDate")
	private String createdDate;

	@Column(name = "modifiedDate")
	@JsonView(Views.Thin.class)
	private String modifiedDate;
	
	@Column(name = "expiredDate")
	private String expiredDate;
	
	@Column(name = "sessionStatus")
	private EntityStatus sessionStatus;
	
	@Column(name = "fromUserId")
	private String fromUserId;
	
	@Column(name = "verificationCode")
	private String verificationCode;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "hluttawboId")
	private Hluttaw hluttaw;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "departmentboId")
	private Department department;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "positionboId")
	private Position position;
	
	@Transient
	@JsonView(Views.Thin.class)
	private String hlutawName;
	
	@Transient
	@JsonView(Views.Thin.class)
	private String deptName;
	
	@Transient
	@JsonView(Views.Thin.class)
	private String positionName;
	
	@Transient
	@JsonView(Views.Thin.class)
	private String constituencyName;
	
	@Transient
	@JsonView(Views.Thin.class)
	private long hlutawType;
	
	@Transient
	@JsonView(Views.Thin.class)
	private long deptType;
	
	@Transient
	@JsonView(Views.Thin.class)
	private long positionType;
	
	@Transient
	@JsonView(Views.Thin.class)
	private String roleType;
	
	@Transient
	@JsonView(Views.Thin.class)
	private String status;
	
	@Transient
	@JsonView(Views.Thin.class)
	private String sessionId="";
	
	@Transient
	@JsonView(Views.Thin.class)
	private String fromUser;
	
	
	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromId) {
		this.fromUserId = fromId;
	}

	public Hluttaw getHluttaw() {
		return hluttaw;
	}

	public void setHluttaw(Hluttaw hluttaw) {
		this.hluttaw = hluttaw;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getHlutawType() {
		return hlutawType;
	}

	public void setHlutawType(long hlutawType) {
		this.hlutawType = hlutawType;
	}

	public long getDeptType() {
		return deptType;
	}

	public void setDeptType(long deptType) {
		this.deptType = deptType;
	}

	public long getPositionType() {
		return positionType;
	}

	public void setPositionType(long positionType) {
		this.positionType = positionType;
	}

	public String getHlutawName() {
		return hlutawName;
	}

	public void setHlutawName(String hlutawName) {
		this.hlutawName = hlutawName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public EntityStatus getSessionStatus() {
		return sessionStatus;
	}

	public void setSessionStatus(EntityStatus sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
	}

	public String getPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	public String getCurrentAddress() {
		return currentAddress;
	}

	public void setCurrentAddress(String currentAddress) {
		this.currentAddress = currentAddress;
	}

	public long getConstituencyType() {
		return constituencyType;
	}

	public void setConstituencyType(long constituencyType) {
		this.constituencyType = constituencyType;
	}

	public Constituency getConstituency() {
		return constituency;
	}

	public void setConstituency(Constituency constituency) {
		this.constituency = constituency;
	}

	public String getConstituencyName() {
		return constituencyName;
	}

	public void setConstituencyName(String constituencyName) {
		this.constituencyName = constituencyName;
	}

}

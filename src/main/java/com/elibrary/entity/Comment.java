package com.elibrary.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "comment")
public class Comment {
	private String comment;

}

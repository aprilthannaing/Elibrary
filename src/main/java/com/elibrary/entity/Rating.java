package com.elibrary.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "rating")
public class Rating {
	
	private double rating;

}

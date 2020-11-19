package com.elibrary.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

@Service
public class AbstractController {
	public String dateFormat() {
    	DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	LocalDateTime now = LocalDateTime.now();
    	return dateFormat.format(now);
    }
}

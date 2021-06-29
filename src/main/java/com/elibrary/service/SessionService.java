package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.Session;
import com.elibrary.entity.User;

public interface SessionService {

	public Session findByBoId(String boId);

	public List<User> getEnteredUserIdList(String startDate, String endDate);

}

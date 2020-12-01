package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.Request;
import com.elibrary.entity.Session;
import com.elibrary.entity.User;
import com.mchange.rmi.ServiceUnavailableException;

public interface UserService {
	public void save(User user) throws ServiceUnavailableException;

	public User findByBoId(String boId);

	public List<User> selectUser(Request req);

	public User selectUserByKey(String key);

	public User getLogin(String email, String password);

	public String checkSession(User user) throws ServiceUnavailableException;

	public String save(Session session);

	public String sessionActive(String sessionId);

	public User selectUserbyId(String key);
}

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

	public String save(Session session);

	public String sessionActive(String sessionId);

	public User selectUserbyId(String key);
	
	public List<User> getLibrarians();
	
	public User selectUserbyEmail(String email);
	
	public List<User> selectUserbyStatus(Request req);
	
	public User selectUserbyVerCode(String loginid,String verificationCode,String email);
	
	public User getLoginByAdmin(String email, String password);
	
	public Session sessionActiveById(String sessionId,String userid);
	
	public User getLoginByWebsite(String email, String password);
	
	public User selectUserbyEmailActive(String email) ;
}

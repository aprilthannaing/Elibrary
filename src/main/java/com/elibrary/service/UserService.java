package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.User;
import com.mchange.rmi.ServiceUnavailableException;

public interface UserService{
	public void save(User user)throws ServiceUnavailableException;
	public User findByBoId(String boId);
	public List<User> selectUser();
	public User selectUserByKey(String key);

}

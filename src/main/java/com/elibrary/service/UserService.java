package com.elibrary.service;

import com.elibrary.entity.User;
import com.mchange.rmi.ServiceUnavailableException;

public interface UserService{
	public void save(User user)throws ServiceUnavailableException;
	
	public User findUserById(String boId)throws ServiceUnavailableException;
}

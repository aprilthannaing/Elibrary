package com.elibrary.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.dao.UserDao;
import com.elibrary.dao.impl.UserDaoImpl;
import com.elibrary.entity.User;
import com.elibrary.service.UserService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("userService")
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserDao userDao;
	
	public static Logger logger = Logger.getLogger(UserDaoImpl.class);

	public void save(User user) throws ServiceUnavailableException {
		try {
			userDao.saveOrUpdate(user);
		}catch(com.mchange.rmi.ServiceUnavailableException e){
			logger.error("Error: "+ e.getMessage());
			
		}
	}

}

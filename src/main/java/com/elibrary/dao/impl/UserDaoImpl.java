package com.elibrary.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.elibrary.dao.UserDao;
import com.elibrary.entity.User;
import com.mchange.rmi.ServiceUnavailableException;

@Repository
public class UserDaoImpl extends AbstractDaoImpl<User, String> implements UserDao{

	protected UserDaoImpl() {
		super(User.class);
	}

	public List<String> findByDateRange(String queryString, String start, String end) {
		// TODO Auto-generated method stub
		return null;
	}

	public String validpwd(String queryString) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean saveUpdate(User e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		return false;
	}

	public long findLong(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void delete(User e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		
	}

	public double findDouble(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

}

package com.elibrary.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.elibrary.dao.AuthorDao;
import com.elibrary.entity.Author;
import com.mchange.rmi.ServiceUnavailableException;

@Repository
public class AuthorDaoImpl extends AbstractDaoImpl<Author ,String> implements AuthorDao{

	protected AuthorDaoImpl() {
		super(Author.class);
	}

	public List<String> findByDateRange(String queryString, String start, String end) {
		// TODO Auto-generated method stub
		return null;
	}

	public String validpwd(String queryString) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean saveUpdate(Author e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		return false;
	}

	public long findLong(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void delete(Author e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		
	}

	public double findDouble(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

}

package com.elibrary.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.elibrary.dao.SessionDao;
import com.elibrary.entity.Session;
import com.mchange.rmi.ServiceUnavailableException;

@Repository
public class SessionDaoImpl extends AbstractDaoImpl<Session, String> implements SessionDao {

	protected SessionDaoImpl() {
		super(Session.class);
	}

	@Override
	public List<String> findByDateRange(String queryString, String start, String end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String validpwd(String queryString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean saveUpdate(Session e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long findLong(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(Session e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double findDouble(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

}

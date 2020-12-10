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
		return null;
	}

	@Override
	public String validpwd(String queryString) {
		return null;
	}

	@Override
	public boolean saveUpdate(Session e) throws ServiceUnavailableException {
		return false;
	}

	@Override
	public long findLong(String queryString) {
		return 0;
	}

	@Override
	public void delete(Session e) throws ServiceUnavailableException {

	}

	@Override
	public double findDouble(String queryString) {
		return 0;
	}

}

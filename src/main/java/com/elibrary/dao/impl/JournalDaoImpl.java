package com.elibrary.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.elibrary.dao.JournalDao;
import com.elibrary.entity.Journal;
import com.mchange.rmi.ServiceUnavailableException;

@Repository
public class JournalDaoImpl extends AbstractDaoImpl<Journal, String> implements JournalDao{

	protected JournalDaoImpl() {
		super(Journal.class);
	}

	public List<String> findByDateRange(String queryString, String start, String end) {
		// TODO Auto-generated method stub
		return null;
	}

	public String validpwd(String queryString) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean saveUpdate(Journal e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		return false;
	}

	public long findLong(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void delete(Journal e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		
	}

	public double findDouble(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

}

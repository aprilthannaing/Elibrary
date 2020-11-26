package com.elibrary.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.elibrary.dao.HistoryDao;
import com.elibrary.entity.History;
import com.mchange.rmi.ServiceUnavailableException;

@Repository
public class HistoryDaoImpl extends AbstractDaoImpl<History, String> implements HistoryDao {

	protected HistoryDaoImpl() {
		super(History.class);
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
	public boolean saveUpdate(History e) throws ServiceUnavailableException {
		return false;
	}

	@Override
	public long findLong(String queryString) {
		return 0;
	}

	@Override
	public void delete(History e) throws ServiceUnavailableException {

	}

	@Override
	public double findDouble(String queryString) {
		return 0;
	}

}
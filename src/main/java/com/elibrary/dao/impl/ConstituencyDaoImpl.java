package com.elibrary.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.elibrary.dao.ConstituencyDao;
import com.elibrary.dao.DepartmentDao;
import com.elibrary.entity.Constituency;
import com.mchange.rmi.ServiceUnavailableException;

@Repository
public class ConstituencyDaoImpl extends AbstractDaoImpl<Constituency, String> implements ConstituencyDao{

	protected ConstituencyDaoImpl() {
		super(Constituency.class);
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
	public boolean saveUpdate(Constituency e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long findLong(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(Constituency e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double findDouble(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

}

package com.elibrary.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import com.elibrary.dao.HluttawDao;
import com.elibrary.entity.Hluttaw;
import com.mchange.rmi.ServiceUnavailableException;

@Repository
public class HluttawDaoImpl extends AbstractDaoImpl<Hluttaw, String> implements HluttawDao{

	protected HluttawDaoImpl() {
		super(Hluttaw.class);
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
	public boolean saveUpdate(Hluttaw e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long findLong(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(Hluttaw e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double findDouble(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}

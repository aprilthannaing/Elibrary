package com.elibrary.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.elibrary.dao.MagazineDao;
import com.elibrary.entity.Magazine;
import com.mchange.rmi.ServiceUnavailableException;

@Repository
public class MagazineDaoImpl extends AbstractDaoImpl<Magazine, String> implements MagazineDao {

	protected MagazineDaoImpl() {
		super(Magazine.class);
	}

	public List<String> findByDateRange(String queryString, String start, String end) {
		// TODO Auto-generated method stub
		return null;
	}

	public String validpwd(String queryString) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean saveUpdate(Magazine e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		return false;
	}

	public long findLong(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void delete(Magazine e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub

	}

	public double findDouble(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

}

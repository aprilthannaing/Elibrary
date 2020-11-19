package com.elibrary.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.elibrary.dao.HluttawDao;
import com.elibrary.dao.PositionDao;
import com.elibrary.entity.Hluttaw;
import com.elibrary.entity.Position;
import com.mchange.rmi.ServiceUnavailableException;

@Repository
public class PositionDaoImpl extends AbstractDaoImpl<Position, String> implements PositionDao{

	protected PositionDaoImpl() {
		super(Position.class);
	}{

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
	public boolean saveUpdate(Position e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long findLong(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(Position e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double findDouble(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}
}

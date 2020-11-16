package com.elibrary.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.elibrary.dao.CategoryDao;
import com.elibrary.entity.Category;
import com.mchange.rmi.ServiceUnavailableException;

@Repository
public class CategoryDaoImpl extends AbstractDaoImpl<Category, String> implements CategoryDao{

	protected CategoryDaoImpl() {
		super(Category.class);
	}

	public List<String> findByDateRange(String queryString, String start, String end) {
		// TODO Auto-generated method stub
		return null;
	}

	public String validpwd(String queryString) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean saveUpdate(Category e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		return false;
	}

	public long findLong(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void delete(Category e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		
	}

	public double findDouble(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

}

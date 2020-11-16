package com.elibrary.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.elibrary.dao.BookDao;
import com.elibrary.entity.Book;
import com.mchange.rmi.ServiceUnavailableException;

@Repository
public class BookDaoImpl extends AbstractDaoImpl<Book, String> implements BookDao{

	protected BookDaoImpl() {
		super(Book.class);
	}

	public List<String> findByDateRange(String queryString, String start, String end) {
		// TODO Auto-generated method stub
		return null;
	}

	public String validpwd(String queryString) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean saveUpdate(Book e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		return false;
	}

	public long findLong(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void delete(Book e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		
	}

	public double findDouble(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}
}

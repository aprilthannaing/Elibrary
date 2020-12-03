package com.elibrary.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.elibrary.dao.BookAuthorDao;
import com.elibrary.entity.Book_Author;
import com.elibrary.entity.SubCategory;
import com.mchange.rmi.ServiceUnavailableException;

@Repository
public class BookAuthorDaoImpl extends AbstractDaoImpl<Book_Author, String> implements BookAuthorDao {

	protected BookAuthorDaoImpl() {
		super(Book_Author.class);
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
	public boolean saveUpdate(Book_Author e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long findLong(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(Book_Author e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub

	}

	@Override
	public double findDouble(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

}

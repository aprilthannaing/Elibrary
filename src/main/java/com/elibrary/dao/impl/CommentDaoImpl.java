package com.elibrary.dao.impl;

import java.util.List;

import com.elibrary.dao.CommentDao;
import com.elibrary.entity.Comment;
import com.mchange.rmi.ServiceUnavailableException;

public class CommentDaoImpl extends AbstractDaoImpl<Comment, String> implements CommentDao{

	protected CommentDaoImpl() {
		super(Comment.class);
		// TODO Auto-generated constructor stub
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
	public boolean saveUpdate(Comment e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long findLong(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(Comment e) throws ServiceUnavailableException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double findDouble(String queryString) {
		// TODO Auto-generated method stub
		return 0;
	}

}

package com.elibrary.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.elibrary.dao.CommentDao;
import com.elibrary.entity.Comment;
import com.mchange.rmi.ServiceUnavailableException;

@Repository
public class CommentDaoImpl extends AbstractDaoImpl<Comment, String> implements CommentDao{

	protected CommentDaoImpl() {
		super(Comment.class);
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
	public boolean saveUpdate(Comment e) throws ServiceUnavailableException {
		return false;
	}

	@Override
	public long findLong(String queryString) {
		return 0;
	}

	@Override
	public void delete(Comment e) throws ServiceUnavailableException {
		
	}

	@Override
	public double findDouble(String queryString) {
		return 0;
	}

}

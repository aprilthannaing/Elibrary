package com.elibrary.service;

import com.elibrary.entity.Comment;
import com.mchange.rmi.ServiceUnavailableException;

public interface CommentService {
	public void save(Comment comment) throws ServiceUnavailableException;
}

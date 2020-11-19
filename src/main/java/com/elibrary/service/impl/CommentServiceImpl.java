package com.elibrary.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.dao.CommentDao;
import com.elibrary.dao.impl.CommentDaoImpl;
import com.elibrary.entity.Comment;
import com.elibrary.service.CommentService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("CommentService")
public class CommentServiceImpl implements CommentService{

	@Autowired
	private CommentDao commentDao;
	
	public static Logger logger = Logger.getLogger(CommentDaoImpl.class);
	
	@Override
	public void save(Comment comment) throws ServiceUnavailableException {
		
		try {
			commentDao.saveOrUpdate(comment);
		}catch(com.mchange.rmi.ServiceUnavailableException e){
			logger.error("Error: "+ e.getMessage());
			
		}
		
	}

}

package com.elibrary.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.dao.CommentDao;
import com.elibrary.dao.impl.CommentDaoImpl;
import com.elibrary.entity.Category;
import com.elibrary.entity.Comment;
import com.elibrary.service.CommentService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("commentService")
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentDao commentDao;

	public static Logger logger = Logger.getLogger(CommentDaoImpl.class);

	public void save(Comment comment) throws ServiceUnavailableException {
		try {
			if (comment.isIdRequired(comment.getId()))
				comment.setId(getId());

			if (comment.isBoIdRequired(comment.getBoId()))
				comment.setBoId(getBoId());

			commentDao.saveOrUpdate(comment);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}
	}

	private long getId() {
		return CountComment() + 1;
	}

	private Long plus() {
		return CountComment() + 10000;
	}

	public long CountComment() {
		String query = "select count(*) from Comment";
		return commentDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "COMMENT" + plus();
	}

}

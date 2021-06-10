package com.elibrary.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.dao.ReplyDao;
import com.elibrary.dao.impl.CategoryDaoImpl;
import com.elibrary.entity.Reply;
import com.elibrary.service.ReplyService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("replyService")
public class ReplyServiceImpl implements ReplyService {

	@Autowired
	private ReplyDao replyDao;

	public static Logger logger = Logger.getLogger(CategoryDaoImpl.class);

	@Override
	public void save(Reply reply) throws ServiceUnavailableException {
		try {

			if (reply.isBoIdRequired(reply.getBoId()))
				reply.setBoId(getBoId());

			replyDao.saveOrUpdate(reply);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}
	}

	private Long plus() {
		return countReply() + 10000;
	}

	private long countReply() {
		String query = "select count(*) from Reply";
		return replyDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "REPLY" + plus();
	}

	@Override
	public Reply getReply(String replyId) {

		String query = "From Reply where boId='" + replyId + "'";
		List<Reply> replies = replyDao.byQuery(query);
		if (CollectionUtils.isEmpty(replies))
			return null;
		return replies.get(0);
	}

}

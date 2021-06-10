package com.elibrary.service;

import com.elibrary.entity.Reply;
import com.mchange.rmi.ServiceUnavailableException;

public interface ReplyService {

	public void save(Reply reply) throws ServiceUnavailableException;

	public Reply getReply(String replyId);

}

package com.elibrary.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.elibrary.dao.SessionDao;
import com.elibrary.entity.EntityStatus;
import com.elibrary.entity.Session;
import com.elibrary.service.SessionService;

@Service("sessionService")
public class SessionServiceImpl implements SessionService {

	@Autowired
	private SessionDao sessionDao;

	public Session findByBoId(String boId) {
		String query = "from Session where boId='" + boId + "' and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Session> sessionList = sessionDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(sessionList))
			return null;
		return sessionList.get(0);
	}

}

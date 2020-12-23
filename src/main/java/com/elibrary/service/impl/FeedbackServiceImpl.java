package com.elibrary.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.dao.FeedbackDao;
import com.elibrary.dao.impl.CategoryDaoImpl;
import com.elibrary.entity.Feedback;
import com.elibrary.service.FeedbackService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("feedbackService")
public class FeedbackServiceImpl implements FeedbackService {

	@Autowired
	private FeedbackDao feedbackDao;

	public static Logger logger = Logger.getLogger(CategoryDaoImpl.class);

	@Override
	public void save(Feedback feedback) throws ServiceUnavailableException {
		try {

			if (feedback.isBoIdRequired(feedback.getBoId()))
				feedback.setBoId(getBoId());

			feedbackDao.saveOrUpdate(feedback);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}
	}

	private Long plus() {
		return countFeedback() + 10000;
	}

	private long countFeedback() {
		String query = "select count(*) from Feedback";
		return feedbackDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "FEEDBACK" + plus();
	}

}

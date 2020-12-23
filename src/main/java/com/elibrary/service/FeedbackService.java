package com.elibrary.service;

import com.elibrary.entity.Feedback;
import com.mchange.rmi.ServiceUnavailableException;

public interface FeedbackService {

	public void save(Feedback feedback) throws ServiceUnavailableException;

}

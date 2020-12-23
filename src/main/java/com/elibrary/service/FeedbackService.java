package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.Feedback;
import com.mchange.rmi.ServiceUnavailableException;

public interface FeedbackService {

	public void save(Feedback feedback) throws ServiceUnavailableException;

	public Feedback findByBoId(String boId);

	public List<Feedback> findByUserId(Long userId);

}

package com.elibrary.service;

import com.elibrary.entity.Rating;
import com.mchange.rmi.ServiceUnavailableException;

public interface RatingService {

	public void save(Rating rating) throws ServiceUnavailableException;

	public double getAverageRating(Long bookId);

	public double getOwnRating(Long userId, Long bookId);

	public Rating findByUserandBook(Long userId, Long bookId);

}

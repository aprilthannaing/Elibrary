package com.elibrary.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.elibrary.dao.RatingDao;
import com.elibrary.dao.impl.CommentDaoImpl;
import com.elibrary.entity.ActionStatus;
import com.elibrary.entity.Rating;
import com.elibrary.service.RatingService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("ratingService")
public class RatingServiceImpl implements RatingService {

	@Autowired
	private RatingDao ratingDao;

	public static Logger logger = Logger.getLogger(CommentDaoImpl.class);

	public void save(Rating rating) throws ServiceUnavailableException {
		try {

			if (rating.isBoIdRequired(rating.getBoId()))
				rating.setBoId(getBoId());

			ratingDao.saveOrUpdate(rating);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}
	}

	private Long plus() {
		return CountRating() + 10000;
	}

	public long CountRating() {
		String query = "select count(*) from Rating";
		return ratingDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "RATING" + plus();
	}
	
	public Rating findByUserandBook(Long userId, Long bookId) {
		String query = "select rating from Rating rating where id in (Select ratingId from History history where userId=" + userId + " and bookId=" + bookId + " and actionStatus='" + ActionStatus.RATING + "')";
		List<Rating> ratingList = ratingDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(ratingList))
			return null;
		return ratingList.get(0);
	}

	public double getAverageRating(Long bookId) {
		double total = 0.0;
		String query = "select rating from Rating rating where id in (select br.ratingId from Book_Rating br where br.bookId=" + bookId + ")";
		List<Rating> ratingList = ratingDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(ratingList))
			return total;

		for (Rating rating : ratingList)
			total += rating.getRating();
		return total / (double) ratingList.size();
	}

	public double getOwnRating(Long userId, Long bookId) {
		String query = "select rating from Rating rating where id in (Select ratingId from History history where userId=" + userId + " and bookId=" + bookId + " and actionStatus='" + ActionStatus.RATING + "')";
		List<Rating> ratingList = ratingDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(ratingList))
			return 0;
		return ratingList.get(0).getRating();
	}

}

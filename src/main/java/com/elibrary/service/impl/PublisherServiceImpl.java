package com.elibrary.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.elibrary.dao.PublisherDao;
import com.elibrary.dao.impl.CategoryDaoImpl;
import com.elibrary.entity.Author;
import com.elibrary.entity.Category;
import com.elibrary.entity.EntityStatus;
import com.elibrary.entity.Publisher;
import com.elibrary.service.PublisherService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("publisherService")
public class PublisherServiceImpl implements PublisherService {

	@Autowired
	private PublisherDao publisherDao;

	public static Logger logger = Logger.getLogger(CategoryDaoImpl.class);

	public void save(Publisher publisher) throws ServiceUnavailableException {
		try {

			if (publisher.isBoIdRequired(publisher.getBoId()))
				publisher.setBoId(getBoId());

			publisherDao.saveOrUpdate(publisher);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}
	}

	private Long plus() {
		return countPublisher() + 10000;
	}

	public long countPublisher() {
		String query = "select count(*) from Publisher";
		return publisherDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "PUBLISHER" + plus();
	}

	public List<Publisher> getAll() {
		String query = "select publisher from Publisher publisher where entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Publisher> publishers = publisherDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(publishers))
			return null;
		return publishers;
	}

	public Publisher findByBoId(String boId) {
		String query = "select publisher from Publisher publisher where boId='" + boId + "'and entityStatus='"
				+ EntityStatus.ACTIVE + "'";
		List<Publisher> publishers = publisherDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(publishers))
			return null;
		return publishers.get(0);
	}

}

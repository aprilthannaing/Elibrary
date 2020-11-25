package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.Publisher;
import com.mchange.rmi.ServiceUnavailableException;

public interface PublisherService {

	public void save(Publisher publisher) throws ServiceUnavailableException;

	public List<Publisher> getAll();

	public Publisher findByBoId(String boId);
	
	public long countPublisher();
}

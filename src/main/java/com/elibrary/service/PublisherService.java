package com.elibrary.service;

import com.elibrary.entity.Publisher;
import com.mchange.rmi.ServiceUnavailableException;

public interface PublisherService {

	public void save(Publisher publisher) throws ServiceUnavailableException;


}

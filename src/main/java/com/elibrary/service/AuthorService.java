package com.elibrary.service;

import com.elibrary.entity.Author;
import com.mchange.rmi.ServiceUnavailableException;

public interface AuthorService {
	public void save(Author author)throws ServiceUnavailableException;

}

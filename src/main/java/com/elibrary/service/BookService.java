package com.elibrary.service;

import com.elibrary.entity.Book;
import com.mchange.rmi.ServiceUnavailableException;

public interface BookService {
	public void save(Book book) throws ServiceUnavailableException;

	public boolean isDuplicateProfile(String fullProfile);

	public boolean isDuplicatePDF(String fullProfile);

}

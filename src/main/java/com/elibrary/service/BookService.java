package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.Book;
import com.elibrary.entity.Journal;
import com.mchange.rmi.ServiceUnavailableException;

public interface BookService {
	public void save(Book book)throws ServiceUnavailableException;
	
//	public List<Book> findByDateRange(String startDate, String endDate);

}

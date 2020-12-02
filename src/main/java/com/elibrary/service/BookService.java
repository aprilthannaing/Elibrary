package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.Book;
import com.mchange.rmi.ServiceUnavailableException;

public interface BookService {
	public void save(Book book) throws ServiceUnavailableException;

	public boolean isDuplicateProfile(String fullProfile);

	public Book findByBoId(String boId);

	public boolean isDuplicatePDF(String fullProfile);

	public List<Book> getAll();

	public long countBook();
	
	public List<Book> getBookListByLibrarian(long librarianId);
	
	public long getBookCountByLibrarian(long librarianId);

}

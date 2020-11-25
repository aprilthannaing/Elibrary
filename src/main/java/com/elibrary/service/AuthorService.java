package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.Author;
import com.mchange.rmi.ServiceUnavailableException;

public interface AuthorService {

	public void save(Author author) throws ServiceUnavailableException;

	public boolean isDuplicateProfile(String fullProfile);

	public List<Author> getAll();

	public Author findByBoId(String boId);
	
	public long countAuthor();

}

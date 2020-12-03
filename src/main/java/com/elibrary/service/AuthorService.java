package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.Author;
import com.elibrary.entity.AuthorType;
import com.mchange.rmi.ServiceUnavailableException;

public interface AuthorService {

	public void save(Author author) throws ServiceUnavailableException;

	public boolean isDuplicateProfile(String fullProfile);

	public List<Author> getAll();

	public Author findByBoId(String boId);

	public long countAuthor();

	public List<Author> getAuthorListByCategory(long categoryId, AuthorType authorType);

}

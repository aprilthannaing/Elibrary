package com.elibrary.service;

import java.sql.SQLException;
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

	public Author getAuthorListById(long authorId, AuthorType authorType);

	public List<Long> getAuthorIdByBookCount(long categoryId) throws SQLException, ClassNotFoundException;

	public List<Author> getAuthorListByCategory(long categoryId, AuthorType authorType);

	public List<Long> getAuthorIdByBookCount() throws SQLException, ClassNotFoundException;

}

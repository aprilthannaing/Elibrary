package com.elibrary.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.elibrary.dao.AuthorDao;
import com.elibrary.dao.impl.AuthorDaoImpl;
import com.elibrary.entity.Author;
import com.elibrary.entity.AuthorType;
import com.elibrary.entity.EntityStatus;
import com.elibrary.service.AuthorService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("authorService")
public class AuthorServiceImpl implements AuthorService {

	@Autowired
	private AuthorDao authorDao;

	public static Logger logger = Logger.getLogger(AuthorDaoImpl.class);

	public void save(Author author) throws ServiceUnavailableException {
		try {
			if (author.isBoIdRequired(author.getBoId()))
				author.setBoId(getBoId());
			authorDao.saveOrUpdate(author);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}

	}

	private Long plus() {
		return countAuthor() + 10000;
	}

	public long countAuthor() {
		String query = "select count(*) from Author";
		return authorDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "AUTHOR" + plus();
	}

	public boolean isDuplicateProfile(String profilePicture) {
		String query = "select author from Author author where profilePicture='" + profilePicture.trim() + "'";
		List<Author> authors = authorDao.getEntitiesByQuery(query);
		return !CollectionUtils.isEmpty(authors);
	}

	public List<Author> getAll() {
		String query = "select author from Author author where entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Author> authors = authorDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(authors))
			return null;
		return authors;
	}

	public Author findByBoId(String boId) {
		String query = "select author from Author author where boId='" + boId + "'and entityStatus='"
				+ EntityStatus.ACTIVE + "'";
		List<Author> authors = authorDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(authors))
			return null;
		return authors.get(0);
	}

	public List<Author> getAuthorListByCategory(long categoryId, AuthorType authorType) {
		String query = "select author from Author author where authorType='" + authorType
				+ "' and author.id in (select ba.authorId from Book_Author ba where ba.bookId in(Select bc.id from Book_Category bc where bc.categoryId="
				+ categoryId + " ))";
		List<Author> authors = authorDao.getEntitiesByQuery(query, 12);
		if (CollectionUtils.isEmpty(authors))
			return new ArrayList<Author>();
		return authors;
	}

}

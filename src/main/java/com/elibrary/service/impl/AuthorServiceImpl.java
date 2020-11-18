package com.elibrary.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.dao.AuthorDao;
import com.elibrary.dao.impl.AuthorDaoImpl;
import com.elibrary.entity.Author;
import com.elibrary.entity.SubCategory;
import com.elibrary.service.AuthorService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("authorService")
public class AuthorServiceImpl implements AuthorService {

	@Autowired
	private AuthorDao authorDao;

	public static Logger logger = Logger.getLogger(AuthorDaoImpl.class);

	public void save(Author author) throws ServiceUnavailableException {
		try {
			if (author.isIdRequired(author.getId()))
				author.setId(getId());

			if (author.isBoIdRequired(author.getBoId()))
				author.setBoId(getBoId());
			authorDao.saveOrUpdate(author);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}

	}

	private long getId() {
		return countAuthor() + 1;
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

}

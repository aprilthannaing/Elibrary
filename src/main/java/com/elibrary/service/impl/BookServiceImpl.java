package com.elibrary.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.elibrary.dao.BookDao;
import com.elibrary.dao.impl.BookDaoImpl;
import com.elibrary.entity.Book;
import com.elibrary.entity.Category;

import com.elibrary.entity.EntityStatus;

import com.elibrary.service.BookService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("BookService")
public class BookServiceImpl implements BookService {

	@Autowired
	private BookDao bookDao;

	public static Logger logger = Logger.getLogger(BookDaoImpl.class);

	public void save(Book book) throws ServiceUnavailableException {
		try {		

			if (book.isBoIdRequired(book.getBoId()))
				book.setBoId(getBoId());

			bookDao.saveOrUpdate(book);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}
	}

	private Long plus() {
		return countBook() + 10000;
	}

	public long countBook() {
		String query = "select count(*) from Book";
		return bookDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "BOOK" + plus();
	}

	public boolean isDuplicateProfile(String fullProfile) {
		String query = "select book from Book book where coverPhoto='" + fullProfile.trim() + "'";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		return !CollectionUtils.isEmpty(books);
	}
	
	public boolean isDuplicatePDF(String fullProfile) {
		String query = "select book from Book book where path='" + fullProfile.trim() + "'";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		return !CollectionUtils.isEmpty(books);
	}
	
	public List<Book> getAll() {
		String query = "select book from Book book where entityStatus='" + EntityStatus.ACTIVE + "'";			
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return null;
		return books;
	}


	@Override
	public Book findByBoId(String boId) {
		String query = "select book from Book book where boId='" + boId + "'and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if(CollectionUtils.isEmpty(books))
		return null;
		return books.get(0);
	}

}

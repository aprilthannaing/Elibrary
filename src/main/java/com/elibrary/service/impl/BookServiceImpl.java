package com.elibrary.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.dao.BookDao;
import com.elibrary.dao.JournalDao;
import com.elibrary.dao.impl.BookDaoImpl;
import com.elibrary.entity.Book;
import com.elibrary.entity.Journal;
import com.elibrary.service.BookService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("BookService")
public class BookServiceImpl implements BookService{
	
	@Autowired
	private BookDao bookDao;
	
	public static Logger logger = Logger.getLogger(BookDaoImpl.class);
	
	public void save(Book book)throws ServiceUnavailableException{
		try {
			bookDao.saveOrUpdate(book);
		}catch(com.mchange.rmi.ServiceUnavailableException e){
			logger.error("Error: "+ e.getMessage());
			
		}
	}
	
//	@Override
//	public List<Book> findByDateRange(String startDate, String endDate) {
//		String query = "from Book book where book.createdDate between " + startDate + "' and '" + endDate
//				+ "'";
//		List<Book> book = BookDao.getEntitiesByQuery(query);
//		return book;
//	
//	}
	
}

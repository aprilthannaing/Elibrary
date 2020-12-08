package com.elibrary.service.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.elibrary.dao.BookAuthorDao;
import com.elibrary.dao.BookDao;
import com.elibrary.dao.impl.BookDaoImpl;
import com.elibrary.entity.ActionStatus;
import com.elibrary.entity.Book;
import com.elibrary.entity.EntityStatus;
import com.elibrary.service.BookService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("BookService")
public class BookServiceImpl implements BookService {

	@Autowired
	private BookDao bookDao;

	@Autowired
	private BookAuthorDao bookAuthorDao;

	public static Logger logger = Logger.getLogger(BookDaoImpl.class);

	public void save(Book book) throws ServiceUnavailableException {
		try {

			if (book.isBoIdRequired(book.getBoId()))
				book.setBoId(getBoId());

			book.setEntityStatus(EntityStatus.ACTIVE);
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
		String query = "select book from Book book where coverPhoto='" + fullProfile.trim() + "'and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		return !CollectionUtils.isEmpty(books);
	}

	public boolean isDuplicatePDF(String fullProfile) {
		String query = "select book from Book book where path='" + fullProfile.trim() + "' and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		return !CollectionUtils.isEmpty(books);
	}

	public List<Book> getAll() {
		String query = "select book from Book book where entityStatus='" + EntityStatus.ACTIVE + "' order by book.id desc";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return null;
		return books;
	}

	@Override
	public Book findByBoId(String boId) {
		String query = "select book from Book book where boId='" + boId + "'and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return null;
		return books.get(0);
	}

	@Override
	public Book findById(Long Id) {
		String query = "select book from Book book where Id='" + Id + "'and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return null;
		return books.get(0);
	}

	public List<Book> getBookListByLibrarian(long librarianId) {
		String query = "From Book book where uploader=" + librarianId;
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

	public long getBookCountByLibrarian(long librarianId) {
		String query = "from Book book where uploader=" + librarianId;
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return 0;
		return books.size();
	}

	public List<Book> getBookBySearchTerms(String searchTerms) {
		String query = "From Book book where searchTerms LIKE '%" + searchTerms + "%'";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

	public Long getBookCountWriteByAuthor(long authorId) {
		String query = "select count(*) from Book_Author where authorId=" + authorId;
		List<Long> books = bookAuthorDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(books))
			return (long) 0;
		return (long) books.get(0);
	}

	public List<Book> getLatestBooksByCategoryId(long categoryId) {
		String query = "select book from Book book where entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (Select bookId from Book_Category bc where bc.categoryId=" + categoryId + ") order by book.id desc";
		List<Book> books = bookDao.getEntitiesByQuery(query, 15);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

	public List<Book> getBooksBySubCategoryId(long subcategoryId) {
		String query = "select book from Book book where entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (Select bookId from Book_SubCategory bc where bc.subcategoryId=" + subcategoryId + ")";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

	public List<Book> getBooksByAuthor(long authorId) {
		String query = "select book from Book book where entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (Select ba.bookId from Book_Author ba where ba.authorId=" + authorId + ")";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

	public List<Book> getLatestBooks() {
		String query = "select book from Book book where entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (Select bc.bookId from Book_Category bc where bc.categoryId=1 or bc.categoryId=2 or bc.categoryId=3 or bc.categoryId=4) order by book.id desc";
		List<Book> books = bookDao.getEntitiesByQuery(query, 15);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

	public List<Book> getAllLatestBooks() {
		String query = "select book from Book book where entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (Select bc.bookId from Book_Category bc where bc.categoryId=1 or bc.categoryId=2 or bc.categoryId=3 or bc.categoryId=4)";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

	public Long getAverageRating(long bookId) {
		String query = "select ratingId from Book_Rating where bookId=" + bookId;
		List<Long> ratings = bookDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(ratings))
			return (long) 0;
		return ratings.get(0);
	}

	public Long getBookCountByCategory(long categoryId) {
		String query = "select count(*) from Book_Category where categoryId=" + categoryId;
		List<Long> books = bookAuthorDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(books))
			return (long) 0;
		return (long) books.get(0);
	}

	public List<Book> getAllMostReadingBooks() throws ClassNotFoundException, SQLException {
		List<Book> books = new ArrayList<Book>();
		List<Long> bookIds = getMostReadingBookIds(ActionStatus.READ);
		for (Long bookId : bookIds) {
			Book book = findById(bookId);
			if (book != null)
				books.add(book);
		}
		return books;
	}

	public List<Long> getMostReadingBookIds(ActionStatus actionStatus) throws SQLException, ClassNotFoundException {
		List<Long> bookIds = new ArrayList<Long>();
		String name, pass, url;
		Connection con = null;
		Class.forName("com.mysql.jdbc.Driver");
		url = "jdbc:mysql://localhost:3306/elibrary";
		name = "root";
		pass = "root";
		con = DriverManager.getConnection(url, name, pass);
		String seeachStoredProc = "{call GetBookCountByActionStatus()}";
		CallableStatement myCs = con.prepareCall(seeachStoredProc);

		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next()) {
				if (ActionStatus.valueOf(rs.getString("actionStatus")) == actionStatus)
					bookIds.add(Long.parseLong(rs.getString("BOOK")));
			}

			con.close();
		}
		return bookIds;
	}

	public List<Book> getRecommendBook(Long userId) {
		String query = "select distinct book from Book book where book.Id in (select bs.bookId from Book_SubCategory bs where bs.subcategoryId in (select bsub.subcategoryId from Book_SubCategory bsub where bsub.bookId in (Select h.bookId from History h where h.userId=" + userId + "))) and entityStatus='" + EntityStatus.ACTIVE + "' order by book.id desc";
		List<Book> books = bookDao.getEntitiesByQuery(query, 15);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

	public List<Book> getAllRecommendBooks(Long userId) {
		String query = "select distinct book from Book book where book.Id in (select bs.bookId from Book_SubCategory bs where bs.subcategoryId in (select bsub.subcategoryId from Book_SubCategory bsub where bsub.bookId in (Select h.bookId from History h where h.userId=" + userId + "))) and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

}

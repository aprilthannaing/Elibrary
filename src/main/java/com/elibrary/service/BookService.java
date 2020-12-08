package com.elibrary.service;

import java.sql.SQLException;
import java.util.List;

import com.elibrary.entity.ActionStatus;
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

	public List<Book> getBookBySearchTerms(String searchTerms);

	public Long getBookCountWriteByAuthor(long authorId);

	public List<Book> getLatestBooksByCategoryId(long categoryId);

	public Long getAverageRating(long bookId);

	public Long getBookCountByCategory(long categoryId);

	public List<Book> getLatestBooks();

	public List<Long> getMostReadingBookIds(ActionStatus actionStatus) throws SQLException, ClassNotFoundException;

	public Book findById(Long Id);

	public List<Book> getRecommendBook(Long userId);

	public List<Book> getBooksBySubCategoryId(long subcategoryId);

	public List<Book> getBooksByAuthor(long authorId);

	public List<Book> getAllLatestBooks();

	public List<Book> getAllRecommendBooks(Long userId);

	public List<Book> getAllMostReadingBooks() throws ClassNotFoundException, SQLException;

}

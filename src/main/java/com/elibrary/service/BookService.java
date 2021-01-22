package com.elibrary.service;

import java.sql.SQLException;
import java.util.List;

import com.elibrary.entity.ActionStatus;
import com.elibrary.entity.Book;
import com.mchange.rmi.ServiceUnavailableException;

public interface BookService {

	public List<Long> getAllIds();

	public void save(Book book) throws ServiceUnavailableException;

	public boolean isDuplicateProfile(String fullProfile);

	public Book findByBoId(String boId);

	public boolean isDuplicatePDF(String fullProfile);

	public List<Book> getAll();

	public long countBook();

	public long getBookCountByLibrarian(long librarianId, String startDate, String endDate);

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

	public List<Long> getAllLatestBooks();

	public List<Book> getAllRecommendBooks(Long userId);

	public List<Book> getPendingBooks();

	public List<Book> getAllMostReadingBooks() throws ClassNotFoundException, SQLException;

	public List<Long> getBookBySearchTerms(String searchTerms) throws SQLException, ClassNotFoundException;

	public List<Long> getBookBySearchTerms(Long categoryId, Long authorId, String searchTerms) throws SQLException, ClassNotFoundException;

	public List<Long> getBookBySearchTermsAndCategory(Long categoryId, String searchTerms) throws SQLException, ClassNotFoundException;

	public List<Long> getBookBySearchTermsAndSubCategory(Long subcategoryId, String searchTerms) throws SQLException, ClassNotFoundException;

	public List<Long> getBooksByDate(String startDate, String endDate) throws SQLException, ClassNotFoundException;

	public List<Long> getBooksByDateAndSubCategory(Long subcategoryId, String startDate, String endDate) throws SQLException, ClassNotFoundException;

	public List<Long> getBooksByDate(Long categoryId, String startDate, String endDate) throws SQLException, ClassNotFoundException;

	public List<Long> getBooksByDate(Long categoryId, Long authorId, String startDate, String endDate) throws SQLException, ClassNotFoundException;

	public List<Long> getBooksByAuthor(Long authorId, String startDate, String endDate) throws SQLException, ClassNotFoundException;

	public Long getBookCount(long subcategoryId);

	public List<Long> getBooksBySearchTermsAndActionnStatus(String searchTerms, ActionStatus actionStatus, Long userid) throws SQLException, ClassNotFoundException;

	public List<Long> getBooksByDateAndActionStatus(String startDate, String endDate, ActionStatus actionStatus, Long userId) throws SQLException, ClassNotFoundException;

	public List<Long> getBooksBySearchTermsAndRecommended(String searchTerms, Long userId) throws SQLException, ClassNotFoundException;

	public List<Long> getBooksBySearchTermsAndPopular(String searchTerms) throws SQLException, ClassNotFoundException;

	public Long getPendingBookCount();

	public List<Long> getBookIdsBySubCategoryId(long subcategoryId);

	public List<Long> getPopularBooksByCategory(Long categoryId) throws SQLException, ClassNotFoundException;

	public List<Book> getBooksByCreatedDate(String startDate, String endDate) throws SQLException;

	public List<Book> getBooksByLibrarian(long librarianId);

	public List<Book> getBooksByLibrarian(long librarianId, String startDate, String endDate);

	public List<Book> getPopularBookListByCategory(Long categoryId) throws SQLException, ClassNotFoundException;

	public List<Book> getPopularBookListByCategory(Long categoryId, String startDate, String endDate) throws SQLException, ClassNotFoundException;

	public List<Book> getBookListByDateAndSubCategory(Long subcategoryId, String startDate, String endDate) throws SQLException, ClassNotFoundException;

	public List<Long> getBookBySearchTermsAndUploader(String searchTerms, Long uploader) throws SQLException, ClassNotFoundException;

	public List<Long> getPopularBookBySearchTermsAndCategory(Long categoryId, String searchTerms) throws SQLException, ClassNotFoundException;

	public List<Long> getBookListByLibrarian(long librarianId, String startDate, String endDate);

	public List<Long> getPopularBooksBySubCategory(Long subCategoryId) throws SQLException, ClassNotFoundException;

	public List<Long> getEntriesByLibrarian(String startDate, String endDate) throws SQLException, ClassNotFoundException;

	public List<Book> getBooks();

	public List<Long> getPopularBookBySearchTermsAndSubCategory(Long subCategoryId, String searchTerms) throws SQLException, ClassNotFoundException;

	public List<Book> getPopularBooksBySubCat(Long subCategoryId, String startDate, String endDate) throws SQLException, ClassNotFoundException;

}

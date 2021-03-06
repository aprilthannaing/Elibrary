package com.elibrary.service.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.elibrary.entity.State;
import com.elibrary.entity.User;
import com.elibrary.service.AuthorService;
import com.elibrary.service.BookService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("BookService")
public class BookServiceImpl extends AbstractServiceImpl implements BookService {

	@Autowired
	private BookDao bookDao;

	@Autowired
	private BookAuthorDao bookAuthorDao;

	@Autowired
	private AuthorService authorService;

	public static Logger logger = Logger.getLogger(BookDaoImpl.class);

	@Override
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

	@Override
	public long countBook() {
		String query = "select count(*) from Book where entityStatus='" + EntityStatus.ACTIVE + "'";
		return bookDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "BOOK" + plus();

	}

	@Override
	public boolean isDuplicateAcessionNo(String accessionNo) {
		String query = "select book from Book book where accessionNo='" + accessionNo.trim() + "'and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		return !CollectionUtils.isEmpty(books);

	}

	@Override
	public boolean isDuplicateProfile(String fullProfile) {
		String query = "select book from Book book where coverPhoto='" + fullProfile.trim() + "'and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		return !CollectionUtils.isEmpty(books);
	}

	@Override
	public boolean isDuplicateTitle(String title) {
		String query = "select book from Book book where title='" + title.trim() + "'and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		return !CollectionUtils.isEmpty(books);
	}

	@Override
	public boolean isDuplicatePDF(String fullProfile) {
		String query = "select book from Book book where path='" + fullProfile.trim() + "' and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		return !CollectionUtils.isEmpty(books);
	}

	@Override
	public List<Book> getAll() {
		String query = "select book from Book book where entityStatus='" + EntityStatus.ACTIVE + "' order by book.id desc";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return null;
		return books;
	}

	@Override
	public List<Long> getAllIds() {
		String query = "select book.id from Book book where entityStatus='" + EntityStatus.ACTIVE + "' order by book.id desc";
		List<Long> bookIds = bookDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(bookIds))
			return null;
		return bookIds;
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

	@Override
	public List<Long> getBookListByLibrarian(long librarianId, String startDate, String endDate) {
		String query = "select id from Book where uploader=" + librarianId + " and createdDate between '" + startDate + "' and '" + endDate + "' and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Long> books = bookDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Long>();
		return books;
	}

	@Override
	public List<Book> getBooksByLibrarian(long librarianId) {
		String query = "select book from Book book where uploader=" + librarianId + " and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

	@Override
	public List<Book> getBooksByLibrarian(long librarianId, String startDate, String endDate) {
		String query = "select book from Book book where uploader=" + librarianId + " and createdDate between '" + startDate + "' and '" + endDate + "' and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

	@Override
	public long getBookCountByLibrarian(long librarianId, String startDate, String endDate) {
		String query = "select count(*) from Book where uploader=" + librarianId + " and createdDate between '" + startDate + "' and '" + endDate + "' and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Long> books = bookDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(books))
			return 0;
		return books.get(0);
	}

	@Override
	public Long getBookCountWriteByAuthor(long authorId) {
		String query = "select count(*) from Book_Author where authorId=" + authorId;
		List<Long> books = bookAuthorDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(books))
			return (long) 0;
		return (long) books.get(0);
	}

	@Override
	public List<Book> getLatestBooksByCategoryId(long categoryId) {
		String query = "select book from Book book where entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (Select bookId from Book_Category bc where bc.categoryId=" + categoryId + ") order by book.id desc";
		List<Book> books = bookDao.getEntitiesByQuery(query, 15);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

	@Override
	public List<Book> getBooksBySubCategoryId(long subcategoryId) {
		String query = "select book from Book book where entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (Select bookId from Book_SubCategory bc where bc.subcategoryId=" + subcategoryId + ")";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

	@Override
	public List<Long> getBookIdsBySubCategoryId(long subcategoryId) {
		String query = "select book.id from Book book where entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (Select bookId from Book_SubCategory bc where bc.subcategoryId=" + subcategoryId + ")";
		List<Long> bookIds = bookDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(bookIds))
			return new ArrayList<Long>();
		return bookIds;
	}

	@Override
	public Long getBookCount(long subcategoryId) {
		String query = "select count(*) from Book book where entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (Select bookId from Book_SubCategory bc where bc.subcategoryId=" + subcategoryId + ")";
		List<Long> books = bookDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(books))
			return (long) 0;
		return books.get(0);
	}

	@Override
	public List<Book> getBooksByAuthor(long authorId) {
		String query = "select book from Book book where entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (Select ba.bookId from Book_Author ba where ba.authorId=" + authorId + ")";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

	@Override
	public List<Book> getLatestBooks() {
		List<Book> books = new ArrayList<Book>();
		String query = "select distinct id from Book book where entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (Select bc.bookId from Book_Category bc where bc.categoryId=1 or bc.categoryId=2 or bc.categoryId=3 or bc.categoryId=4) order by book.id desc";
		List<Long> idList = bookDao.findLongByQueryString(query, 15);
		idList.forEach(id -> {
			books.add(findById(id));
		});
		return books;
	}

	@Override
	public List<Long> getFilterLatestBooks() {
		String query = "select distinct book.id from Book book where entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (Select bc.bookId from Book_Category bc where (bc.categoryId!=6 AND bc.categoryId!=5))";
		List<Long> books = bookDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Long>();
		return books;
	}

	@Override
	public List<Long> getAllLatestBooks() {
		String query = "select distinct book.id from Book book where entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Long> books = bookDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Long>();
		return books;
	}

	@Override
	public Long getAverageRating(long bookId) {
		String query = "select ratingId from Book_Rating where bookId=" + bookId;
		List<Long> ratings = bookDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(ratings))
			return (long) 0;
		return ratings.get(0);
	}

	@Override
	public Long getBookCountByCategory(long categoryId) {
		String query = "select count(*) from Book_Category where categoryId=" + categoryId;
		List<Long> books = bookAuthorDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(books))
			return (long) 0;
		return (long) books.get(0);
	}

	@Override
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

	@Override
	public List<Book> getRecommendBook(Long userId) {
		String query = "select distinct book from Book book where book.Id in (select bs.bookId from Book_SubCategory bs where bs.subcategoryId in (select bsub.subcategoryId from Book_SubCategory bsub where bsub.bookId in (Select h.bookId from History h where actionStatus='FAVOURITE' or actionStatus='READ' and h.userId=" + userId + "))) and entityStatus='" + EntityStatus.ACTIVE + "' order by book.id desc";
		List<Book> books = bookDao.getEntitiesByQuery(query, 15);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

	@Override
	public List<Book> getAllRecommendBooks(Long userId) {
		String query = "select distinct book from Book book where book.Id in (select bs.bookId from Book_SubCategory bs where bs.subcategoryId in (select bsub.subcategoryId from Book_SubCategory bsub where bsub.bookId in (Select h.bookId from History h where h.userId=" + userId + "))) and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

	@Override
	public List<Long> getMostReadingBookIds(ActionStatus actionStatus) throws SQLException, ClassNotFoundException {
		List<Long> bookIds = new ArrayList<Long>();
		Connection con = getConnection();
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

	@Override
	public List<Long> getBookBySearchTermsAndSubCategory(Long subcategoryId, String searchTerms) throws SQLException, ClassNotFoundException {
		List<Long> idList = new ArrayList<Long>();
		String storedProc = "{call GET_BookId_BySubCat(?,?)}";
		Connection con = getConnection();
		CallableStatement myCs = con.prepareCall(storedProc);
		myCs.setString(1, searchTerms);
		myCs.setString(2, subcategoryId + "");
		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next()) {
				idList.add(Long.parseLong(rs.getString("id")));
			}
			con.close();
		}
		return idList;
	}

	@Override
	public List<Long> getBookBySearchTerms(String searchTerms) throws SQLException, ClassNotFoundException {
		List<Long> idList = new ArrayList<Long>();
		String storedProc = "{call GET_BookId_ByST(?)}";
		Connection con = getConnection();
		CallableStatement myCs = con.prepareCall(storedProc);
		myCs.setString(1, searchTerms);
		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next()) {
				idList.add(Long.parseLong(rs.getString("id")));
			}
			con.close();
		}
		return idList;
	}

	@Override
	public List<Long> getBookBySearchTerms(Long categoryId, Long authorId, String searchTerms) throws SQLException, ClassNotFoundException {
		List<Long> idList = new ArrayList<Long>();
		String storedProc = "{call GET_BookId_ByAuthor(?,?,?)}";
		Connection con = getConnection();
		CallableStatement myCs = con.prepareCall(storedProc);
		myCs.setString(1, searchTerms);
		myCs.setString(2, authorId + "");
		myCs.setString(3, categoryId + "");
		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next()) {
				idList.add(Long.parseLong(rs.getString("id")));
			}
			con.close();
		}
		return idList;
	}

	@Override
	public List<Long> getBookBySearchTermsAndCategory(Long categoryId, String searchTerms) throws SQLException, ClassNotFoundException {
		List<Long> IdList = new ArrayList<Long>();
		String storedProc = "{call GET_BookId_ByCat(?,?)}";
		Connection con = getConnection();
		CallableStatement myCs = con.prepareCall(storedProc);
		myCs.setString(1, searchTerms);
		myCs.setString(2, categoryId + "");
		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next()) {
				IdList.add(Long.parseLong(rs.getString("id")));
			}
			con.close();
		}
		return IdList;
	}

	@Override
	public List<Long> getBooksBySearchTermsAndActionnStatus(String searchTerms, ActionStatus actionStatus, Long userId) throws SQLException, ClassNotFoundException {
		List<Long> IdList = new ArrayList<Long>();
		String storedProc = "{call GET_BookId_fromHistory(?,?,?)}";
		Connection con = getConnection();
		CallableStatement myCs = con.prepareCall(storedProc);
		myCs.setString(1, searchTerms);
		myCs.setString(2, userId + "");
		myCs.setString(3, actionStatus + "");
		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next()) {
				IdList.add(Long.parseLong(rs.getString("id")));
			}
			con.close();
		}
		return IdList;
	}

	@Override
	public List<Long> getBooksBySearchTermsAndRecommended(String searchTerms, Long userId) throws SQLException, ClassNotFoundException {
		List<Long> IdList = new ArrayList<Long>();
		String storedProc = "{call GET_Recommended_BookId(?,?)}";
		Connection con = getConnection();
		CallableStatement myCs = con.prepareCall(storedProc);
		myCs.setString(1, searchTerms);
		myCs.setString(2, userId + "");
		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next()) {
				IdList.add(Long.parseLong(rs.getString("id")));
			}
			con.close();
		}
		return IdList;
	}

	@Override
	public List<Long> getBooksBySearchTermsAndPopular(String searchTerms) throws SQLException, ClassNotFoundException {
		List<Long> IdList = new ArrayList<Long>();
		String storedProc = "{call GET_Popular_BookId(?)}";
		Connection con = getConnection();
		CallableStatement myCs = con.prepareCall(storedProc);
		myCs.setString(1, searchTerms);
		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next()) {
				IdList.add(Long.parseLong(rs.getString("id")));
			}
			con.close();
		}
		return IdList;
	}

	@Override
	public List<Long> getBooksByAuthor(Long authorId, String startDate, String endDate) throws SQLException, ClassNotFoundException {
		String query = "select book.id from Book book where createdDate between " + startDate + " and " + endDate + " and entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (Select ba.bookId from Book_Author ba where ba.authorId=" + authorId + ") ";
		List<Long> bookIds = bookDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(bookIds))
			return new ArrayList<Long>();
		return bookIds;
	}

	@Override
	public List<Long> getBooksByDate(Long categoryId, Long authorId, String startDate, String endDate) throws SQLException, ClassNotFoundException {
		List<Long> bookIds = getBooksByAuthor(authorId, startDate, endDate);
		List<Long> bookIdList = new ArrayList<Long>();
		bookIds.forEach(id -> {
			Book book = findById(id);
			if (id != null && book.getCategory() != null && book.getCategory().getId() == categoryId)
				bookIdList.add(id);
		});
		return bookIds;
	}

	@Override
	public List<Long> getBooksByDate(Long categoryId, String startDate, String endDate) throws SQLException, ClassNotFoundException {
		String query = "select book.id from Book book where createdDate between '" + startDate + "' and '" + endDate + "' and entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (Select bc.bookId from Book_Category bc where bc.categoryId=" + categoryId + ") ";
		List<Long> IdList = bookDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(IdList))
			return new ArrayList<Long>();
		return IdList;
	}

	@Override
	public List<Long> getBooksByDateAndSubCategory(Long subcategoryId, String startDate, String endDate) throws SQLException, ClassNotFoundException {
		String query = "select book.id from Book book where createdDate between '" + startDate + "' and '" + endDate + "' and entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (Select bs.bookId from Book_SubCategory bs where bs.subcategoryId=" + subcategoryId + ") ";
		List<Long> books = bookDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Long>();
		return books;
	}

	@Override
	public List<Book> getBookListByDateAndSubCategory(Long subcategoryId, String startDate, String endDate) throws SQLException, ClassNotFoundException {
		String query = "select book from Book book where createdDate between '" + startDate + "' and '" + endDate + "' and entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (Select bs.bookId from Book_SubCategory bs where bs.subcategoryId=" + subcategoryId + ") ";
		List<Book> books = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(books))
			return new ArrayList<Book>();
		return books;
	}

	@Override
	public List<Long> getBooksByDate(String startDate, String endDate) throws SQLException, ClassNotFoundException {
		String query = "select book.id from Book book where createdDate between '" + startDate + "' and '" + endDate + "' and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Long> idList = bookDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(idList))
			return new ArrayList<Long>();
		return idList;
	}

	@Override
	public List<Long> getBooksByDateAndActionStatus(String startDate, String endDate, ActionStatus actionStatus, Long userId) throws SQLException, ClassNotFoundException {
		String query = "select book.id from Book book where createdDate between '" + startDate + "' and '" + endDate + "' and entityStatus='" + EntityStatus.ACTIVE + "' and book.id in (select history.bookId.id from History history where history.userId=" + userId + " and actionStatus='" + actionStatus + "')";
		List<Long> idList = bookDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(idList))
			return new ArrayList<Long>();
		return idList;
	}

	@Override
	public List<Book> getPendingBooks() {
		String query = "select book from Book book where state='" + State.PENDING + "' and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Book> bookList = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(bookList))
			return new ArrayList<Book>();
		return bookList;
	}

	@Override
	public Long getPendingBookCount() {
		String query = "select count(*) from Book where state='" + State.PENDING + "' and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Long> counts = bookDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(counts))
			return (long) 0;
		return counts.get(0);
	}

	@Override
	public List<Long> getPopularBooksByCategory(Long categoryId) throws SQLException, ClassNotFoundException {
		List<Long> IdList = new ArrayList<Long>();
		String storedProc = "{call GET_PopularBook_byCatID(?)}";
		Connection con = getConnection();
		CallableStatement myCs = con.prepareCall(storedProc);
		myCs.setString(1, categoryId + "");
		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next() && IdList.size() < 20) {
				IdList.add(Long.parseLong(rs.getString("Book")));
			}
			con.close();
		}
		return IdList;
	}

	@Override
	public List<Long> getPopularBooksBySubCategory(Long subCategoryId) throws SQLException, ClassNotFoundException {
		List<Long> IdList = new ArrayList<Long>();
		String storedProc = "{call GET_PopularBook_bySubCatID(?)}";
		Connection con = getConnection();
		CallableStatement myCs = con.prepareCall(storedProc);
		myCs.setString(1, subCategoryId + "");
		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next() && IdList.size() < 100) {
				IdList.add(Long.parseLong(rs.getString("bookId")));
			}
			con.close();
		}

		return IdList;
	}

	@Override
	public List<Book> getPopularBookListByCategory(Long categoryId) throws SQLException, ClassNotFoundException {
		List<Book> bookList = new ArrayList<Book>();
		String storedProc = "{call GET_PopularBook_byCatID(?)}";
		Connection con = getConnection();
		CallableStatement myCs = con.prepareCall(storedProc);
		myCs.setString(1, categoryId + "");
		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next() && bookList.size() < 20) {
				bookList.add(findById(Long.parseLong(rs.getString("Book"))));
			}
			con.close();
		}
		return bookList;
	}

	@Override
	public List<Book> getPopularBookListByCategory(Long categoryId, String startDate, String endDate) throws SQLException, ClassNotFoundException {
		List<Book> bookList = new ArrayList<Book>();
		String storedProc = "{CALL GET_PopularBook_byCatIDCreateDt(?,?,?)}";
		Connection con = getConnection();
		CallableStatement myCs = con.prepareCall(storedProc);
		myCs.setString(1, categoryId + "");
		myCs.setString(2, startDate);
		myCs.setString(3, endDate);

		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next() && bookList.size() < 20) {
				bookList.add(findById(Long.parseLong(rs.getString("Book"))));
			}
			con.close();
		}
		return bookList;
	}

	@Override
	public List<Book> getBooksByCreatedDate(String startDate, String endDate) throws SQLException {
		String query = "select book from Book book where createdDate between '" + startDate + "' and '" + endDate + "' and entityStatus='" + EntityStatus.ACTIVE + "' order by book.createdDate asc";
		List<Book> bookList = bookDao.getList(query);
		if (CollectionUtils.isEmpty(bookList))
			return new ArrayList<Book>();
		return bookList;
	}

	@Override
	public List<Long> getBookBySearchTermsAndUploader(String searchTerms, Long uploader) throws SQLException, ClassNotFoundException {
		List<Long> IdList = new ArrayList<Long>();
		String storedProc = "{call GET_Book_bySTandUploader(?,?)}";
		Connection con = getConnection();
		CallableStatement myCs = con.prepareCall(storedProc);
		myCs.setString(1, searchTerms);
		myCs.setString(2, uploader + "");
		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next()) {
				IdList.add(Long.parseLong(rs.getString("ID")));
			}
			con.close();
		}
		return IdList;
	}

	@Override
	public List<Long> getPopularBookBySearchTermsAndCategory(Long categoryId, String searchTerms) throws SQLException, ClassNotFoundException {
		List<Long> IdList = new ArrayList<Long>();
		String storedProc = "{call GET_PopularBook_byCatIDandSearchTerm(?,?)}";
		Connection con = getConnection();
		CallableStatement myCs = con.prepareCall(storedProc);
		myCs.setString(1, categoryId + "");
		myCs.setString(2, searchTerms);
		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next()) {
				IdList.add(Long.parseLong(rs.getString("Book")));
			}
			con.close();
		}
		return IdList;
	}

	@Override
	public List<Long> getPopularBookBySearchTermsAndSubCategory(Long subCategoryId, String searchTerms) throws SQLException, ClassNotFoundException {
		List<Long> IdList = new ArrayList<Long>();
		String storedProc = "{call GET_PopularBook_bySubCatIDandSearchTerm(?,?)}";
		Connection con = getConnection();
		CallableStatement myCs = con.prepareCall(storedProc);
		myCs.setString(1, subCategoryId + "");
		myCs.setString(2, searchTerms);
		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next()) {
				IdList.add(Long.parseLong(rs.getString("Book")));
			}
			con.close();
		}
		return IdList;
	}

	@Override
	public List<Long> getEntriesByLibrarian(String startDate, String endDate, List<User> librarianList) throws SQLException, ClassNotFoundException {
		List<Long> totalCount = new ArrayList<Long>();
		Map<Long, Long> map = new HashMap<Long, Long>();
		String storedProc = "{call GET_BookCount_Librarian_byCreateDt(?,?)}";
		Connection con = getConnection();
		CallableStatement myCs = con.prepareCall(storedProc);
		myCs.setString(1, startDate);
		myCs.setString(2, endDate);
		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next()) {
				map.put(Long.parseLong(rs.getString("uploader")), Long.parseLong(rs.getString("BOOK_COUNT")));

			}
			con.close();
		}

		for (User librarian : librarianList) {
			totalCount.add(map.get(librarian.getId()) == null ? 0 : map.get(librarian.getId()));
		}
		return totalCount;
	}

	@Override
	public List<Book> getPopularBooksBySubCat(Long subCategoryId, String startDate, String endDate) throws SQLException, ClassNotFoundException {
		List<Book> bookList = new ArrayList<Book>();
		String storedProc = "{call GET_PopularBook_bySubCatIDCreateDt(?,?,?)}";
		Connection con = getConnection();
		CallableStatement myCs = con.prepareCall(storedProc);
		myCs.setString(1, subCategoryId + "");
		myCs.setString(2, startDate);
		myCs.setString(3, endDate);
		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next()) {
				bookList.add(findById(Long.parseLong(rs.getString("bookId"))));
			}
			con.close();
		}
		return bookList;
	}

	@Override
	public List<Book> getBooks() {
		String query = "select book from Book book where path not like '%watermark%'";
		// String query = "select book from Book book";
		List<Book> bookList = bookDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(bookList))
			return new ArrayList<Book>();
		return bookList;
	}
}

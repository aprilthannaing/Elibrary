package com.elibrary.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.elibrary.dao.BookDao;
import com.elibrary.dao.HistoryDao;
import com.elibrary.dao.impl.CategoryDaoImpl;
import com.elibrary.entity.ActionStatus;
import com.elibrary.entity.Book;
import com.elibrary.entity.History;
import com.elibrary.service.BookService;
import com.elibrary.service.HistoryService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("historyService")
public class HistoryServiceImpl implements HistoryService {

	@Autowired
	private HistoryDao historyDao;

	@Autowired
	private BookDao bookDao;

	@Autowired
	private BookService bookService;

	public static Logger logger = Logger.getLogger(CategoryDaoImpl.class);

	@Override
	public void save(History history) throws ServiceUnavailableException {
		try {

			if (history.isBoIdRequired(history.getBoId()))
				history.setBoId(getBoId());

			historyDao.saveOrUpdate(history);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}
	}

	private Long plus() {
		return countHistory() + 10000;
	}

	public long countHistory() {
		String query = "select count(*) from History";
		return historyDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "HISTORY" + plus();
	}

	@Override
	public List<History> getBookIdReadByUser(Long userId) {
		String query = "from History history where history.userId=" + userId + " order by history.Id desc";
		List<History> historyList = historyDao.getEntitiesByQuery(query, 20);
		if (CollectionUtils.isEmpty(historyList))
			return new ArrayList<History>();
		return historyList;

	}

	@Override
	public List<Book> getBooksByUser(Long userId, ActionStatus actionStatus) {
		String query = "select history from History history where history.userId=" + userId + " and history.actionStatus='" + actionStatus + "' order by history.Id desc";
		List<History> historyList = historyDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(historyList))
			return new ArrayList<Book>();

		List<Book> bookList = new ArrayList<Book>();
		historyList.forEach(history -> {
			Book book = history.getBookId();
			if (!bookList.contains(book)) {
				bookList.add(book);
			}
		});
		return bookList;

	}

	@Override
	public boolean isFavourite(long userId, long bookId) {
		String query = "from History where userId=" + userId + " and bookId=" + bookId + " and actionStatus='" + ActionStatus.FAVOURITE + "'";
		return !CollectionUtils.isEmpty(historyDao.getEntitiesByQuery(query));
	}

	@Override
	public boolean isBookMark(long userId, long bookId) {
		String query = "from History where userId=" + userId + " and bookId=" + bookId + " and actionStatus='" + ActionStatus.BOOKMARK + "'";
		return !CollectionUtils.isEmpty(historyDao.getEntitiesByQuery(query));
	}

	@Override
	public boolean isRead(long userId, long bookId) {
		String query = "from History where userId=" + userId + " and bookId=" + bookId + " and actionStatus='" + ActionStatus.READ + "'";
		return !CollectionUtils.isEmpty(historyDao.getEntitiesByQuery(query));
	}

	@Override
	public void unFavourite(long userId, long bookId) {
		String query = "from History where userId=" + userId + " and bookId=" + bookId + " and actionStatus='" + ActionStatus.FAVOURITE + "'";
		List<History> historyList = historyDao.getEntitiesByQuery(query);
		historyList.forEach(history -> {
			history.setActionStatus(ActionStatus.UNFAVOURITE);
			try {
				save(history);
			} catch (ServiceUnavailableException e) {
				logger.error("Error: " + e);
			}
		});
	}

	@Override
	public void unBookMark(long userId, long bookId) {
		String query = "from History where userId=" + userId + " and bookId=" + bookId + " and actionStatus='" + ActionStatus.BOOKMARK + "'";
		List<History> historyList = historyDao.getEntitiesByQuery(query);
		historyList.forEach(history -> {
			history.setActionStatus(ActionStatus.UNBOOKMARK);
			try {
				save(history);
			} catch (ServiceUnavailableException e) {
				logger.error("Error: " + e);
			}
		});
	}

}

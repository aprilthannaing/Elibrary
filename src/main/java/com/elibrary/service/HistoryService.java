package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.ActionStatus;
import com.elibrary.entity.Book;
import com.elibrary.entity.History;
import com.mchange.rmi.ServiceUnavailableException;

public interface HistoryService {

	public void save(History history) throws ServiceUnavailableException;

	public List<History> getBookIdReadByUser(Long userId);

	public List<Book> getBooksByUser(Long userId, ActionStatus actionStatus);

	public boolean isFavourite(long userId, long bookId);

	public boolean isBookMark(long userId, long bookId);

	public boolean isRead(long userId, long bookId);

	public void unFavourite(long userId, long bookId);

	public void unBookMark(long userId, long bookId);

}

package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.Book;
import com.elibrary.entity.History;
import com.mchange.rmi.ServiceUnavailableException;

public interface HistoryService {

	public void save(History history) throws ServiceUnavailableException;

	public List<History> getBookIdReadByUser(Long userId);

	public List<Book> getBooksBookMarkByUser(Long userId);

	public List<Book> getBooksFavouriteByUser(Long userId);

}

package com.elibrary.controller;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elibrary.entity.Book;
import com.elibrary.entity.Views;
import com.elibrary.service.BookService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("")
public class NationalLibraryController {

	@Autowired
	private BookService bookService;

	/*
	 * 
	 * 
	 * "book_detail": {"title": "Concurrent programming in java ", "author":
	 * "Doug Lea", "publisher": "Addison Wesely Longman", "subject":
	 * "Concurrent programming in java ", "edition": "Second", "content": "",
	 * "summary": "" "thumbnail": "http://18.138.135.236:81/image?biblio-number=848"
	 * 
	 * 
	 * 
	 */

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "biblio-detail", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public JSONObject getBookDetail(@RequestParam("book_id") String bookId) {
		JSONObject result = new JSONObject();

		Book book = bookService.findById(Long.parseLong(bookId));
		if (book == null) {
			result.put("message", "There is no book with this ID.");
			return result;
		}

		JSONObject bookDetail = new JSONObject();
		bookDetail.put("title", book.getTitle());
		bookDetail.put("author", CollectionUtils.isEmpty(book.getAuthors()) ? "" : book.getAuthors().get(0).getName());
		bookDetail.put("publisher", CollectionUtils.isEmpty(book.getPublishers()) ? "" : book.getPublishers().get(0).getName());
		bookDetail.put("subject", book.getSubCategory().getEngName());
		bookDetail.put("edition", book.getEdition());
		bookDetail.put("content", "");
		bookDetail.put("summary", "");
		bookDetail.put("summary", book.getComment().getDescription());
		bookDetail.put("thumbnail", "");
		result.put("book_detai", bookDetail);
		return result;
	}

	/* { "data": { "hits": 12 }}" */

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "hits-by-keyword", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public JSONObject hitsByKeyword(@RequestParam("keyword") String keyword, @RequestParam("idx") String idx, @RequestParam("page") String page) throws ClassNotFoundException, SQLException {
		JSONObject result = new JSONObject();
		JSONObject hits = new JSONObject();

		List<Long> bookIds = bookService.getBookBySearchTerms(keyword);
		hits.put("hits", bookIds.size());
		result.put("data", hits);
		return result;
	}

}

package com.elibrary.controller;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elibrary.entity.Book;
import com.elibrary.entity.Views;
import com.elibrary.service.BookService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("search")
public class SearchController {

	@Autowired
	private BookService bookService;

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "book", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject get(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();
		Object searchTerms = json.get("searchTerms");
		if (searchTerms == null || searchTerms.toString().isEmpty()) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Not Found!");
			return resultJson;
		}

		List<Book> bookList = bookService.getBookBySearchTerms(searchTerms.toString());
		if (CollectionUtils.isEmpty(bookList)) {
			resultJson.put("status", "0");
			resultJson.put("msg", "Not Found!");
			return resultJson;
		}
		resultJson.put("status", "1");
		resultJson.put("bookList", bookList);
		return resultJson;
	}

}

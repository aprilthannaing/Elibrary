package com.elibrary.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elibrary.entity.Author;
import com.elibrary.entity.AuthorType;
import com.elibrary.entity.Book;
import com.elibrary.entity.Category;
import com.elibrary.entity.SubCategory;
import com.elibrary.entity.Views;
import com.elibrary.service.AuthorService;
import com.elibrary.service.BookService;
import com.elibrary.service.CategoryService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("home")
public class HomeController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private AuthorService authorService;

	@Autowired
	private BookService bookService;

	private static Logger logger = Logger.getLogger(HomeController.class);

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "book", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject getHomePage(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();
		Object categoryId = json.get("categoryId");
		if (categoryId == null || categoryId.toString().isEmpty()) {
			resultJson.put("status", "false");
			resultJson.put("err_msg", "CategoryId must not null");
			return resultJson;
		}

		Category category = categoryService.findByBoId(categoryId.toString());
		List<Author> localAuthors = authorService.getAuthorListByCategory(category.getId(), AuthorType.LOCAL);
		List<Author> internationalAuthors = authorService.getAuthorListByCategory(category.getId(),
				AuthorType.INTERNATIONAL);

		resultJson.put("local_author", localAuthors);
		resultJson.put("international_author", internationalAuthors);
		resultJson.put("sub_category", getDisplaySubCategories(category));
		resultJson.put("latest_book", getLatestBooks(category));
		return resultJson;
	}

	private List<Book> getLatestBooks(Category category) {
		List<Book> bookList = bookService.getLatestBooksByCategoryId(category.getId());
		bookList.forEach(book -> {
			book.setAverageRating(bookService.getAverageRating(book.getId()));
		});
		return bookList;
	}

	private List<SubCategory> getDisplaySubCategories(Category category) {
		List<SubCategory> subCategories = new ArrayList<SubCategory>();
		category.getSubCategories().forEach(subCategory -> {
			if (subCategory.isDisplay())
				subCategories.add(subCategory);
		});
		return subCategories;

	}

	private List<Author> getSortedAuthorsWritten(List<Author> authorsByCategory) {
		List<Long> bookCounts = new ArrayList<Long>();
		Map<Long, Author> bookAuthorMap = new HashMap<Long, Author>();

		authorsByCategory.forEach(author -> {
			Long bookCount = bookService.getBookCountWriteByAuthor(author.getId());
			bookCounts.add(bookCount);
			bookAuthorMap.put(bookCount, author);

		});

		Collections.sort(bookCounts);
		logger.info("booKCount:" + bookCounts);
		List<Author> sortedLocalAuthors = new ArrayList<Author>();

		for (int i = bookCounts.size() - 12; i < bookCounts.size(); i++) {
			sortedLocalAuthors.add(bookAuthorMap.get(bookCounts.get(i)));

		}
		return sortedLocalAuthors;
	}

}

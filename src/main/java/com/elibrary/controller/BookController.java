package com.elibrary.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elibrary.entity.Author;
import com.elibrary.entity.Book;
import com.elibrary.entity.SubCategory;
import com.elibrary.entity.User;
import com.elibrary.entity.Views;
import com.elibrary.service.AuthorService;
import com.elibrary.service.BookService;
import com.elibrary.service.HistoryService;
import com.elibrary.service.RatingService;
import com.elibrary.service.SubCategoryService;
import com.elibrary.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("book")
public class BookController extends AbstractController {

	@Autowired
	private BookService bookService;

	@Autowired
	private UserService userService;

	@Autowired
	private SubCategoryService subCategoryService;

	@Autowired
	private AuthorService authorService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private RatingService ratingService;

	private static Logger logger = Logger.getLogger(SubCategoryController.class);

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "all", method = RequestMethod.GET)
	@JsonView(Views.Thin.class)
	public JSONObject getAll() throws ServiceUnavailableException {
		JSONObject result = new JSONObject();
		result.put("books", bookService.getAll());
		return result;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "boId", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject getAll(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject result = new JSONObject();
		result.put("book", bookService.findByBoId(json.get("boId").toString()));
		return result;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "count", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public String getCount() throws ServiceUnavailableException {
		return bookService.countBook() + "";
	}

	/*
	 * ""title"": "", ""page"": , ""author_id"": , ""sub_category_id"":
	 * SUBCATEGORY1000102
	 */

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "", method = RequestMethod.POST) // mobile
	@JsonView(Views.Thin.class)
	public JSONObject getBooks(@RequestHeader("token") String token, @RequestBody JSONObject json) throws ServiceUnavailableException, ClassNotFoundException, SQLException {
		JSONObject resultJson = new JSONObject();

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("message", "Unauthorized Request");
			return resultJson;
		}

		Object page = json.get("page");
		if (page == null || page.toString().isEmpty()) {
			resultJson.put("status", false);
			resultJson.put("message", "This page is not found!");
			return resultJson;
		}
		int pageNo;
		try {
			pageNo = Integer.parseInt(page.toString());
		} catch (Exception e) {
			resultJson.put("status", false);
			resultJson.put("message", "This page is not found!");
			return resultJson;
		}

		List<Book> bookList = getBooks(json); // by subcategory, by author id, latest, popular, all, favourite, bookmark
		if (bookList == null) {
			resultJson.put("status", false);
			resultJson.put("message", "This User or Author or Sub-Category is not found!");
			return resultJson;
		}

		int lastPageNo = bookList.size() % 10 == 0 ? bookList.size() / 10 : bookList.size() / 10 + 1;

		List<Book> books = getBooksByPaganation(json, bookList, pageNo);
		if (books == null) {
			resultJson.put("status", false);
			resultJson.put("message", "This User or Author or Sub-Category is not found!");
			return resultJson;
		}

		resultJson.put("status", true);
		resultJson.put("current_page", pageNo);
		resultJson.put("last_page", lastPageNo);
		resultJson.put("total_count", bookList.size());
		resultJson.put("books", books);
		return resultJson;
	}

	private List<Book> getBooks(JSONObject json) throws ClassNotFoundException, SQLException {
		List<Book> books = new ArrayList<Book>();

		/* latest books */
		Object titleObject = json.get("title");
		String title = titleObject.toString();
		if (title.equals("latest")) {
			return bookService.getAllLatestBooks();
		}

		/* recommend books */
		if (title.equals("recommend")) {
			User user = getUser(json);
			return bookService.getAllRecommendBooks(user.getId());
		}

		/* favourite books by user */
		if (title.equals("favourite")) {
			User user = getUser(json);
			if (user == null)
				return null;
			return historyService.getBooksFavouriteByUser(user.getId());
		}

		/* bookmark books by user */
		if (title.equals("bookmark")) {
			User user = getUser(json);
			if (user == null)
				return null;
			return historyService.getBooksBookMarkByUser(user.getId());
		}

		/* popular books */
		if (title.equals("popular"))
			return bookService.getAllMostReadingBooks();

		/* all books */
		if (title.equals("all"))
			return bookService.getAll();

		/* books by author */
		Object authorObject = json.get("author_id");
		String authorBoId = authorObject.toString();
		if (!authorBoId.isEmpty()) {
			Author author = authorService.findByBoId(authorBoId);
			if (author == null)
				return null;
			return bookService.getBooksByAuthor(author.getId());
		}

		/* books by subcategory */
		Object subCategory = json.get("sub_category_id");
		String subCategoryBoId = subCategory.toString();
		SubCategory subcategory = subCategoryService.findByBoId(subCategoryBoId);
		if (subcategory == null)
			return null;
		return bookService.getBooksBySubCategoryId(subcategory.getId());

	}

}

package com.elibrary.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elibrary.entity.Category;
import com.elibrary.entity.SubCategory;
import com.elibrary.entity.User;
import com.elibrary.entity.Views;
import com.elibrary.service.BookService;
import com.elibrary.service.SubCategoryService;
import com.elibrary.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("dashboard")
public class DashBoardController extends AbstractController {

	@Autowired
	private UserService userService;

	@Autowired
	private BookService bookService;

	@Autowired
	private SubCategoryService subCategoryService;

	private static Logger logger = Logger.getLogger(DashBoardController.class);

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "librarian/bookentry", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject getAll() throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();
		List<String> nameList = new ArrayList<String>();
		List<Long> bookCount = new ArrayList<Long>();
		List<User> librarianList = userService.getLibrarians();

		if (librarianList == null) {
			resultJson.put("status", "0");
			resultJson.put("msg", "There is no Librarian!");
			return resultJson;
		}

		librarianList.forEach(librarian -> {
			nameList.add(librarian.getName());
			bookCount.add(bookService.getBookCountByLibrarian(librarian.getId()));

		});
		resultJson.put("status", "1");
		resultJson.put("bookCount", bookCount);
		resultJson.put("nameList", nameList);
		return resultJson;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "booksbysubcategory", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject getBooksBySubCategory(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();
		Category category = getCategory(json);
		if (category == null) {
			resultJson.put("status", false);
			resultJson.put("message", "Please choose category!");
			return resultJson;
		}

		List<String> nameList = new ArrayList<String>();
		List<Long> bookCount = new ArrayList<Long>();

		List<SubCategory> subs = subCategoryService.byCategory(category.getId());
		subs.forEach(sub -> {
			nameList.add(sub.getMyanmarName() + (sub.getEngName().isEmpty() ? "" : "( " + sub.getEngName() + " )"));
			bookCount.add((long) bookService.getBooksBySubCategoryId(sub.getId()).size());
		});

		resultJson.put("status", true);
		resultJson.put("bookCount", bookCount);
		resultJson.put("nameList", nameList);
		return resultJson;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "librarian/booklist", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject getBookList(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();
		List<List<Long>> bookList = new ArrayList<List<Long>>();
		List<Long> books = new ArrayList<Long>();

		List<User> librarianList = userService.getLibrarians();
		if (librarianList == null) {
			resultJson.put("status", "0");
			resultJson.put("msg", "There is no Librarian!");
			return resultJson;
		}

		librarianList.forEach(librarian -> {
			bookList.add(bookService.getBookListByLibrarian(librarian.getId()));
		});

		if (CollectionUtils.isEmpty(bookList)) {
			resultJson.put("status", "0");
			resultJson.put("msg", "There is no Book!");
			return resultJson;
		}

		int index = Integer.parseInt(json.get("index").toString());
		if (index < 0) {
			resultJson.put("status", "0");
			resultJson.put("msg", "There is no Book!");
			return resultJson;
		}

		int pageNo = getPage(json);
		books = bookList.get(index);
		int lastPageNo = books.size() % 10 == 0 ? books.size() / 10 : books.size() / 10 + 1;

		resultJson.put("bookList", getBooksByPaganationWithBookIds(json, books, pageNo));
		resultJson.put("status", true);
		resultJson.put("current_page", pageNo);
		resultJson.put("last_page", lastPageNo);
		resultJson.put("total_count", books.size());
		return resultJson;
	}

}

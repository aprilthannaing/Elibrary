package com.elibrary.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
import com.elibrary.entity.Book;
import com.elibrary.entity.Category;
import com.elibrary.entity.SubCategory;
import com.elibrary.entity.Views;
import com.elibrary.service.BookService;
import com.elibrary.service.SubCategoryService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("search")
public class SearchController extends AbstractController {

	@Autowired
	private BookService bookService;

	@Autowired
	private SubCategoryService subCategoryService;

	private static Logger logger = Logger.getLogger(SearchController.class);

	/*
	 * {"page":"1", "category_id":"", "sub_category_id":"", "author_id":"",
	 * "start_date":"05-2020", "end_date":"07-2020",
	 * "searchTerms":"ရုပ်ရှင်အကယ်ဒမီဆုများ" }
	 */

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "book", method = RequestMethod.POST) // web, mobile
	@JsonView(Views.Thin.class)
	public JSONObject get(@RequestBody JSONObject json) throws ServiceUnavailableException, ClassNotFoundException, SQLException {
		JSONObject resultJson = new JSONObject();
		Object pageObject = json.get("page");
		if (pageObject == null || pageObject.toString().isEmpty()) {
			resultJson.put("status", false);
			resultJson.put("message", "This page is not valid!");
			return resultJson;
		}

		Object searchTermObject = json.get("searchTerms");
		Object start = json.get("start_date");
		Object end = json.get("end_date");
		if (searchTermObject == null || searchTermObject.toString().isEmpty()) {
			if (start == null || start.toString().isEmpty() || end == null || end.toString().isEmpty()) {
				resultJson.put("status", false);
				resultJson.put("message", "There is no book with this search term!");
				return resultJson;

			}
		} else {
			if (!start.toString().isEmpty() || !end.toString().isEmpty()) {
				resultJson.put("status", false);
				resultJson.put("message", "Please search with Keyword or Date Range!");
				return resultJson;
			}
		}

		logger.info("here !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		List<Long> bookList = getBookList(json);
		int lastPageNo = bookList.size() % 10 == 0 ? bookList.size() / 10 : bookList.size() / 10 + 1;
		int pageNo = Integer.parseInt(pageObject.toString());
		List<Book> books = getBooksByPaganationWithBookIds(json, bookList, pageNo);
		if (books == null) {
			resultJson.put("status", false);
			resultJson.put("message", "This User is not found!");
			return resultJson;
		}

		resultJson.put("status", true);
		resultJson.put("current_page", pageNo);
		resultJson.put("last_page", lastPageNo);
		resultJson.put("total_count", bookList.size());
		resultJson.put("books", books);
		return resultJson;
	}

	private List<Long> getBookList(JSONObject json) throws ClassNotFoundException, SQLException {
		Category category = getCategory(json);
		Author author = getAuthor(json);
		SubCategory subCategory = getSubCategory(json);

		Object searchTermObject = json.get("searchTerms");
		Object start = json.get("start_date");
		Object end = json.get("end_date");

		String searchTerms = searchTermObject.toString();
		String startDate = "";
		String endDate = "";
		if (start != null && !start.toString().isEmpty())
			startDate = start.toString() + "-01";

		if (end != null && !end.toString().isEmpty()) {
			endDate = end.toString() + "-01";
			LocalDate convertedDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			convertedDate = convertedDate.withDayOfMonth(convertedDate.getMonth().length(convertedDate.isLeapYear()));
			endDate = convertedDate.toString();
		}

		if (category != null && author != null)
			return searchTerms.isEmpty() ? bookService.getBooksByDate(category.getId(), author.getId(), startDate, endDate) : bookService.getBookBySearchTerms(category.getId(), author.getId(), searchTerms);
		else if (subCategory != null)
			return searchTerms.isEmpty() ? bookService.getBooksByDateAndSubCategory(subCategory.getId(), startDate, endDate) : bookService.getBookBySearchTermsAndSubCategory(subCategory.getId(), searchTerms);
		else if (category != null)
			return searchTerms.isEmpty() ? bookService.getBooksByDate(category.getId(), startDate, endDate) : bookService.getBookBySearchTermsAndCategory(category.getId(), searchTerms);
		else
			return searchTerms.isEmpty() ? bookService.getBooksByDate(startDate, endDate) : bookService.getBookBySearchTerms(searchTerms);
	}

}

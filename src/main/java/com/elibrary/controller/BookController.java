
package com.elibrary.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elibrary.entity.ActionStatus;
import com.elibrary.entity.Author;
import com.elibrary.entity.Book;
import com.elibrary.entity.Category;
import com.elibrary.entity.State;
import com.elibrary.entity.SubCategory;
import com.elibrary.entity.User;
import com.elibrary.entity.Views;
import com.elibrary.service.AuthorService;
import com.elibrary.service.BookService;
import com.elibrary.service.CategoryService;
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

	@Autowired
	private CategoryService categoryService;

	private static Logger logger = Logger.getLogger(SubCategoryController.class);

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "setDownlaodApproval", method = RequestMethod.POST)
	@JsonView(Views.Thin.class)
	public JSONObject setDownloadApproval(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject result = new JSONObject();
		List<Object> bookBoIds = (List<Object>) json.get("bookBoIds");
		for (Object boId : bookBoIds) {

			logger.info("boid !!!!!!!" + boId);
			Book book = bookService.findById(Long.parseLong(boId.toString()));
			if (book == null)
				continue;

			logger.info("book.getDownloadApproval() !!!!!!!" + book.getDownloadApproval());
			if (book.getDownloadApproval().toString().trim().equals("true")) {
				book.setDownloadApproval("");
			} else
				book.setDownloadApproval("true");
			logger.info("book download approval!!!!!!!!!" + book.getDownloadApproval());
			bookService.save(book);
		}

		result.put("status", "true");
		return result;
	}

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

		logger.info("bookService.countBook():::" + bookService.countBook() + "");
		return bookService.countBook() + "";
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "pending", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject getPendingBook() {
		JSONObject resultJson = new JSONObject();
		resultJson.put("books", bookService.getPendingBooks());
		return resultJson;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "pendingCount", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject getPendingBookCount() {
		JSONObject resultJson = new JSONObject();
		resultJson.put("count", bookService.getPendingBookCount());
		return resultJson;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "approve", method = RequestMethod.POST) // approving without editing
	@JsonView(Views.Summary.class)
	public JSONObject approve(@RequestHeader("token") String token, @RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();

		logger.info("approving !!!!!!!!!!");

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("message", "Unauthorized Request");
			return resultJson;
		}

		List<Object> bookBoIds = (List<Object>) json.get("bookBoIds");
		for (Object boId : bookBoIds) {
			Book book = bookService.findById(Long.parseLong(boId.toString()));
			if (book == null)
				continue;

			book.setState(State.APPROVE);
			bookService.save(book);
		}

		resultJson.put("status", true);
		resultJson.put("message", "Success!");
		return resultJson;
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

		/* latest books */
		Object titleObject = json.get("title");
		String title = titleObject.toString();

		/* all books */
		if (title.equals("all")) {
			List<Long> bookIdList = bookService.getAllIds();
			int lastPageNo = bookIdList.size() % 10 == 0 ? bookIdList.size() / 10 : bookIdList.size() / 10 + 1;

			List<Book> books = getBooksByPaganationWithBookIds(json, bookIdList, pageNo);
			if (books == null) {
				resultJson.put("status", false);
				resultJson.put("message", "This User or Author or Sub-Category is not found!");
				return resultJson;
			}

			resultJson.put("status", true);
			resultJson.put("current_page", pageNo);
			resultJson.put("last_page", lastPageNo);
			resultJson.put("total_count", bookIdList.size());
			resultJson.put("books", books);
			return resultJson;
		}

		if (title.equals("latest")) {
			List<Long> bookIdList = bookService.getFilterLatestBooks();
			int lastPageNo = bookIdList.size() % 10 == 0 ? bookIdList.size() / 10 : bookIdList.size() / 10 + 1;

			List<Book> books = getBooksByPaganationWithBookIds(json, bookIdList, pageNo);
			if (books == null) {
				resultJson.put("status", false);
				resultJson.put("message", "This User or Author or Sub-Category is not found!");
				return resultJson;
			}

			resultJson.put("status", true);
			resultJson.put("current_page", pageNo);
			resultJson.put("last_page", lastPageNo);
			resultJson.put("total_count", bookIdList.size());
			resultJson.put("books", books);
			return resultJson;
		}

		if (title.equals("latestAll")) {
			List<Long> bookIdList = bookService.getAllLatestBooks();
			int lastPageNo = bookIdList.size() % 10 == 0 ? bookIdList.size() / 10 : bookIdList.size() / 10 + 1;

			List<Book> books = getBooksByPaganationWithBookIds(json, bookIdList, pageNo);
			if (books == null) {
				resultJson.put("status", false);
				resultJson.put("message", "This User or Author or Sub-Category is not found!");
				return resultJson;
			}

			resultJson.put("status", true);
			resultJson.put("current_page", pageNo);
			resultJson.put("last_page", lastPageNo);
			resultJson.put("total_count", bookIdList.size());
			resultJson.put("books", books);
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
		Object titleObject = json.get("title");
		String title = titleObject.toString();

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

			Stack<Book> stackBooks = new Stack<Book>();
			List<Book> newBookList = new ArrayList<Book>();
			List<Book> bookList = historyService.getBooksByUser(user.getId(), ActionStatus.FAVOURITE);

			bookList.forEach(book -> {
				if (!stackBooks.contains(book))
					stackBooks.push(book);
			});

			for (int i = 0; i < bookList.size(); i++)
				newBookList.add(stackBooks.pop());

			return newBookList;
		}

		/* bookmark books by user */
		if (title.equals("bookmark")) {
			User user = getUser(json);
			if (user == null)
				return null;

			Stack<Book> stackBooks = new Stack<Book>();
			List<Book> newBookList = new ArrayList<Book>();
			List<Book> bookList = historyService.getBooksByUser(user.getId(), ActionStatus.BOOKMARK);

			bookList.forEach(book -> {
				if (!stackBooks.contains(book))
					stackBooks.push(book);
			});

			for (int i = 0; i < bookList.size(); i++)
				newBookList.add(stackBooks.pop());
			return newBookList;
		}

		/* popular books */
		if (title.equals("popular"))
			return bookService.getAllMostReadingBooks();

		/* books by category and author */
		Object categoryObject = json.get("category_id");
		Object authorObject = json.get("author_id");
		if (categoryObject != null && !categoryObject.toString().isEmpty() && authorObject != null && !authorObject.toString().isEmpty()) {
			Author author = authorService.findByBoId(authorObject.toString());
			List<Book> bookList = bookService.getBooksByAuthor(author.getId());
			bookList.forEach(book -> {
				if (book != null && book.getCategory() != null && book.getCategory().getBoId().equals(categoryObject.toString()))
					books.add(book);
			});
			return books;
		}

		/* books by author */
		if (authorObject != null && !authorObject.toString().isEmpty()) {
			String authorBoId = authorObject.toString();
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

	private String getStartDate(String param) {
		String[] str = param.split(",");
		String startDate = "";
		String[] start = str[0].split(" ");
		startDate = start[3] + "-" + parseMonthToInt(start[1]) + "-" + start[2];
		return startDate;
	}

	private String getEndDate(String param) {
		String[] str = param.split(",");
		String endDate = "";
		String[] start = str[1].split(" ");
		endDate = start[3] + "-" + parseMonthToInt(start[1]) + "-" + start[2];
		return endDate;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "exportEntry", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public JSONObject exportExcel(@RequestParam("input") String param, HttpServletResponse response) throws IOException, SQLException {
		JSONObject resultJson = new JSONObject();

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Book");
		writeTitle(workbook, sheet, "Book Entries By  Librarian");

		List<User> librarianList = userService.getLibrarians();
		String[] str = param.split(",");
		int index = Integer.parseInt(str[2]);
		User user = librarianList.get(index);
		List<Book> books = new ArrayList<Book>();

		/* have not start date and end date */
		if (param.startsWith(","))
			books.addAll(bookService.getBooksByLibrarian(user.getId()));
		else {
			String startDate = getStartDate(param);
			String endDate = getEndDate(param);
			logger.info("startDate !!!!" + startDate);
			logger.info("endDate  !!!!!!!!" + endDate);
			logger.info("user.getId()  !!!!!!!!" + user.getId());

			books.addAll(bookService.getBooksByLibrarian(user.getId(), startDate, endDate));
		}

		Boolean result = writeBookSheet(workbook, books);
		if (result == false) {
			resultJson.put("status", "0");
			return resultJson;
		}

		workbook.write(response.getOutputStream());
		resultJson.put("status", "1");
		return resultJson;

	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "exportPopularBooks", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public JSONObject exportPopularBooks(@RequestParam("input") String param, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
		JSONObject resultJson = new JSONObject();

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Book");
		writeTitle(workbook, sheet, "Popular Books By Main Category");

		List<Category> categories = categoryService.getAll();
		String[] str = param.split(",");
		int index = Integer.parseInt(str[2]);
		Category category = categories.get(index);
		List<Book> books = new ArrayList<Book>();

		/* have not start date and end date */
		if (param.startsWith(","))
			books.addAll(bookService.getPopularBookListByCategory(category.getId()));
		else {
			String startDate = getStartDate(param);
			String endDate = getEndDate(param);
			books.addAll(bookService.getPopularBookListByCategory(category.getId(), startDate, endDate));
		}

		Boolean result = writeBookSheet(workbook, books);
		if (result == false) {
			resultJson.put("status", "0");
			return resultJson;
		}

		workbook.write(response.getOutputStream());
		resultJson.put("status", "1");
		return resultJson;

	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "exportBooksByCategory", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public JSONObject exportBooksByCategory(@RequestParam("input") String param, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
		JSONObject resultJson = new JSONObject();

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Book");
		writeTitle(workbook, sheet, "Popular Books By Main Category");

		String[] str = param.split(",");
		SubCategory subCategory = subCategoryService.findByBoId(str[3]);
		List<Book> books = new ArrayList<Book>();

		/* have not start date and end date */
		if (param.startsWith(","))
			books.addAll(bookService.getBooksBySubCategoryId(subCategory.getId()));
		else {
			String startDate = getStartDate(param);
			String endDate = getEndDate(param);
			books.addAll(bookService.getBookListByDateAndSubCategory(subCategory.getId(), startDate, endDate));
		}

		Boolean result = writeBookSheet(workbook, books);
		if (result == false) {
			resultJson.put("status", "0");
			return resultJson;
		}

		workbook.write(response.getOutputStream());
		resultJson.put("status", "1");
		return resultJson;

	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "exportPopularBooksBySubCategory", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public JSONObject exportPopularBooksBySubCategory(@RequestParam("input") String param, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
		JSONObject resultJson = new JSONObject();

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Book");
		writeTitle(workbook, sheet, "Popular Books By Sub Category");

		String[] str = param.split(",");
		SubCategory subCategory = subCategoryService.findByBoId(str[3]);
		List<Book> books = new ArrayList<Book>();

		/* have not start date and end date */
		if (param.startsWith(",")) {
			bookService.getPopularBooksBySubCategory(subCategory.getId()).forEach(id -> {
				books.add(bookService.findById(id));
			});
		} else {
			String startDate = getStartDate(param);
			String endDate = getEndDate(param);
			books.addAll(bookService.getPopularBooksBySubCat(subCategory.getId(), startDate, endDate));
		}

		if (CollectionUtils.isEmpty(books)) {
			resultJson.put("status", false);
			resultJson.put("message", "There is no books within date range.");
		}

		Boolean result = writeBookSheet(workbook, books);
		if (result == false) {
			resultJson.put("status", "0");
		}

		workbook.write(response.getOutputStream());
		return resultJson;

	}

}

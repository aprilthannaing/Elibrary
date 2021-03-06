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

import com.elibrary.entity.ActionStatus;
import com.elibrary.entity.Author;
import com.elibrary.entity.AuthorType;
import com.elibrary.entity.Book;
import com.elibrary.entity.Category;
import com.elibrary.entity.SubCategory;
import com.elibrary.entity.User;
import com.elibrary.entity.Views;
import com.elibrary.service.AdvertisementService;
import com.elibrary.service.AuthorService;
import com.elibrary.service.BookService;
import com.elibrary.service.CategoryService;
import com.elibrary.service.FeedbackService;
import com.elibrary.service.HistoryService;
import com.elibrary.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("home")
public class HomeController extends AbstractController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private AuthorService authorService;

	@Autowired
	private BookService bookService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private UserService userService;

	@Autowired
	private AdvertisementService advertisementService;

	@Autowired
	private FeedbackService feedbackService;

	private static Logger logger = Logger.getLogger(HomeController.class);

	private List<Book> getMostReadingBooks() throws ClassNotFoundException, SQLException {
		List<Book> books = new ArrayList<Book>();
		List<Long> bookIds = bookService.getMostReadingBookIds(ActionStatus.READ);

		for (Long bookId : bookIds) {
			if (bookId < 0 || books.size() > 11)
				break;
			Book book = bookService.findById(bookId);
			if (book != null)
				books.add(book);
		}
		return books;
	}

	private List<Author> getAuthors(AuthorType authorType) throws ClassNotFoundException, SQLException {
		List<Author> authors = new ArrayList<Author>();

		List<Long> authorIdList = authorService.getAuthorIdByBookCount();
		for (Long authorId : authorIdList) {
			if (authorId < 0 || authors.size() > 11)
				break;
			Author author = authorService.getAuthorListById(authorId, authorType);
			if (author != null)
				authors.add(author);
		}
		return authors;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "", method = RequestMethod.POST) // home
	@JsonView(Views.Thin.class)
	public JSONObject getHomePage(@RequestHeader("token") String token, @RequestBody JSONObject json) throws ServiceUnavailableException, ClassNotFoundException, SQLException {
		JSONObject resultJson = new JSONObject();

		Object userId = json.get("user_id");
		if (userId == null || userId.toString().isEmpty()) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "This user is not found!");
			return resultJson;
		}

		User user = userService.findByBoId(userId.toString());
		if (user == null) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "This user is not found!");
			return resultJson;
		}

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "Unauthorized Request");
			return resultJson;
		}
		List<Category> categoryList = categoryService.getAll();
		categoryList.forEach(category -> {
			if (category.getIcon() != null)
				category.setIcon(category.getIcon().replaceAll("BlackIcon", "CategoryIcon"));
			category.setBookCount(bookService.getBookCountByCategory(category.getId()));
		});
		resultJson.put("status", true);
		resultJson.put("latest_book", setBookInfo(bookService.getLatestBooks(), user)); // 15
		resultJson.put("popular_book", setBookInfo(getMostReadingBooks(), user)); // 6
		resultJson.put("recommend_book", setBookInfo(bookService.getRecommendBook(user.getId()), user)); // 12
		resultJson.put("local_author", getAuthors(AuthorType.LOCAL)); // 12
		resultJson.put("international_author", getAuthors(AuthorType.INTERNATIONAL));
		resultJson.put("advertisements", advertisementService.getAll());
		resultJson.put("main_category", categoryList); // 6
		resultJson.put("notiCount", feedbackService.findByUserId(user.getId()).size());
		return resultJson;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "notiCount", method = RequestMethod.POST) // page by category
	@JsonView(Views.Summary.class)
	public JSONObject countNoti(@RequestHeader("token") String token, @RequestBody JSONObject json) throws ServiceUnavailableException, ClassNotFoundException, SQLException {
		JSONObject resultJson = new JSONObject();
		Object userId = json.get("user_id");
		User user = userService.findByBoId(userId.toString());
		if (user == null) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "This user is not found!");
			return resultJson;
		}

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "Unauthorized Request");
			return resultJson;
		}
		resultJson.put("status", true);
		resultJson.put("notiCount", feedbackService.findByUserId(user.getId()).size());
		return resultJson;

	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "book", method = RequestMethod.POST) // page by category
	@JsonView(Views.Summary.class)
	public JSONObject getHomePageByCategory(@RequestHeader("token") String token, @RequestBody JSONObject json) throws ServiceUnavailableException, ClassNotFoundException, SQLException {
		JSONObject resultJson = new JSONObject();

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "Unauthorized Request");
			return resultJson;
		}

		Object categoryId = json.get("categoryId");
		if (categoryId == null || categoryId.toString().isEmpty()) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "CategoryId must not null");
			return resultJson;
		}

		Object userId = json.get("user_id");
		if (userId == null || userId.toString().isEmpty()) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "This user is not found!");
			return resultJson;
		}

		User user = userService.findByBoId(userId.toString());
		if (user == null) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "This user is not found!");
			return resultJson;
		}

		Category category = categoryService.findByBoId(categoryId.toString());
		List<Book> bookList = setBookInfo(bookService.getLatestBooksByCategoryId(category.getId()), user);
		int bookCount = bookList.size();
		resultJson.put("local_author", getAuthors(category, AuthorType.LOCAL));
		resultJson.put("international_author", getAuthors(category, AuthorType.INTERNATIONAL));
		resultJson.put("sub_category", getDisplaySubCategories(category));
		resultJson.put("latest_book", bookList);
		resultJson.put("book_count", bookCount);
		resultJson.put("status", true);
		return resultJson;
	}

	private List<Author> getAuthors(Category category, AuthorType authorType) throws ClassNotFoundException, SQLException {
		List<Author> authors = new ArrayList<Author>();

		List<Long> authorIdList = authorService.getAuthorIdByCategoryId(category.getId());
		for (Long authorId : authorIdList) {
			if (authorId < 0 || authors.size() > 11)
				break;
			Author author = authorService.getAuthorListById(authorId, authorType);
			if (author != null)
				authors.add(author);
		}
		return authors;
	}

	private List<SubCategory> getDisplaySubCategories(Category category) {
		List<SubCategory> subCategories = new ArrayList<SubCategory>();
		category.getSubCategories().forEach(subCategory -> {
			if (subCategory.isDisplay()) {
				subCategory.setBookCount(bookService.getBookCount(subCategory.getId()));
				subCategories.add(subCategory);
			}
		});
		return subCategories;
	}

}

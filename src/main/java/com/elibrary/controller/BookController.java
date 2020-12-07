package com.elibrary.controller;

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

import com.elibrary.entity.Book;
import com.elibrary.entity.SubCategory;
import com.elibrary.entity.Views;
import com.elibrary.service.AuthorService;
import com.elibrary.service.BookService;
import com.elibrary.service.CategoryService;
import com.elibrary.service.JournalService;
import com.elibrary.service.SubCategoryService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("book")
public class BookController extends AbstractController {

	@Autowired
	private BookService bookService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private SubCategoryService subCategoryService;

	@Autowired
	private AuthorService authorService;

	private static Logger logger = Logger.getLogger(SubCategoryController.class);

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "all", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
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
	@RequestMapping(value = "", method = RequestMethod.POST)
	@JsonView(Views.Thin.class)
	public JSONObject getBooks(@RequestHeader("token") String token, @RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "Unauthorized Request");
			return resultJson;
		}

		Object page = json.get("page");
		if (page == null || page.toString().isEmpty()) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "This page is not found!");
			return resultJson;
		}
		int pageNo;
		try {
			pageNo = Integer.parseInt(page.toString());
		} catch (Exception e) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "This page is not found!");
			return resultJson;
		}

		Object subCategory = json.get("sub_category_id");
		String subCategoryBoId = subCategory.toString();
		SubCategory subcategory = subCategoryService.findByBoId(subCategoryBoId);
		List<Book> bookList = bookService.getLatestBooksBySubCategoryId(subcategory.getId());
		List<Book> resultBookList = new ArrayList<Book>();

		logger.info("page no !!!!!!!!!!!" + pageNo);
		
		int firstIndex = pageNo;
		int lastIndex = firstIndex * pageNo ;
		
		
		for (int i = 0; i < pageNo * 9; i++) {
			resultBookList.add(bookList.get(i));
		}

		resultJson.put("status", true);
		resultJson.put("current_page", pageNo);
		resultJson.put("total_count", bookList.size());
		resultJson.put("books", resultBookList);
	

		return resultJson;
	}

}

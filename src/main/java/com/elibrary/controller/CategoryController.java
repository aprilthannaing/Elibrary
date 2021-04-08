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

import com.elibrary.entity.Category;
import com.elibrary.entity.Views;
import com.elibrary.service.AuthorService;
import com.elibrary.service.BookService;
import com.elibrary.service.CategoryService;
import com.elibrary.service.JournalService;
import com.elibrary.service.SubCategoryService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("category")
public class CategoryController extends AbstractController {

	@Autowired
	private BookService bookService;

	@Autowired
	private JournalService journalService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private SubCategoryService subCategoryService;

	@Autowired
	private AuthorService authorService;

	private static Logger logger = Logger.getLogger(SubCategoryController.class);

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "all", method = RequestMethod.GET) // mobile
	@JsonView(Views.Summary.class)
	public JSONObject getAll(@RequestHeader("token") String token) throws ServiceUnavailableException {

		JSONObject result = new JSONObject();
		if (!isTokenRight(token)) {
			result.put("status", false);
			result.put("err_msg", "Unauthorized Request");
			return result;
		}

		List<Category> categoryList = new ArrayList<Category>();
		categoryList = categoryService.getAll();
		categoryList.forEach(category -> {
			if (category.getIcon() != null)
				category.setIcon(category.getIcon().replaceAll("CategoryIcon", "BlackIcon"));
			category.setBookCount(bookService.getBookCountByCategory(category.getId()));
		});

		result.put("status", true);
		result.put("categories", categoryList);
		return result;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "boId", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject findByCategoryBoId(@RequestBody JSONObject json) throws ServiceUnavailableException {
		logger.info("json: " + json);
		JSONObject result = new JSONObject();
		Category category = categoryService.findByBoId(json.get("category").toString().trim());
		if (category != null)
			result.put("subcategories", category.getSubCategories());
		return result;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "byboId", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject byCategoryBoId(@RequestBody JSONObject json) throws ServiceUnavailableException {
		logger.info("json: " + json);
		JSONObject result = new JSONObject();
		Category category = categoryService.findByBoId(json.get("boId").toString().trim());
		if (category != null)
			result.put("category", category);
		return result;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "count", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public String getCount() throws ServiceUnavailableException {
		return categoryService.countActiveCategory() + "";
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "bySubcategoryId", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject findBySubCategoryBoId(@RequestBody JSONObject json) throws ServiceUnavailableException {
		logger.info("json: " + json);
		JSONObject result = new JSONObject();
		Long category = categoryService.findBySubCategoryId(Long.parseLong(json.get("id").toString()));
		String categoryId = String.valueOf(category);
		if (category != null) {
			result.put("categoryId", categoryId);
		}
		return result;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "byId", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject findByCategoryId(@RequestBody JSONObject json) throws ServiceUnavailableException {
		logger.info("json: " + json);
		JSONObject result = new JSONObject();
		Category category = categoryService.findByCategoryId(Long.parseLong(json.get("id").toString()));
		if (category != null) {
			result.put("category", category);
		}

		return result;
	}
}

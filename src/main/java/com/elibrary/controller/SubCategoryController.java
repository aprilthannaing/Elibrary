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
import com.elibrary.entity.SubCategory;
import com.elibrary.entity.TransientSubCategory;
import com.elibrary.entity.Views;
import com.elibrary.service.AuthorService;
import com.elibrary.service.BookService;
import com.elibrary.service.CategoryService;
import com.elibrary.service.JournalService;
import com.elibrary.service.SubCategoryService;
import com.elibrary.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("subcategory")
public class SubCategoryController extends AbstractController {

	@Autowired
	private BookService bookService;

	@Autowired
	private JournalService journalService;

	@Autowired
	private UserService userService;

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

		result.put("subcategories", subCategoryService.getAll());
		return result;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "byengcategory", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject byEngCategory(@RequestHeader("token") String token, @RequestBody JSONObject json)
			throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("message", "Unauthorized Request");
			return resultJson;
		}

		Object mainCategory = json.get("main_category_id");
		if (mainCategory == null || mainCategory.toString().isEmpty()) {
			resultJson.put("status", false);
			resultJson.put("message", "Main Category is not valid!");
			return resultJson;
		}

		Category category = categoryService.findByBoId(mainCategory.toString());
		List<TransientSubCategory> sub = new ArrayList<TransientSubCategory>();

		for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
			TransientSubCategory subCategory = new TransientSubCategory();
			String alpha = (alphabet + "").toUpperCase();
			subCategory.setAlphabet(alpha);
			subCategory.setSubcategories(subCategoryService.byAlphabet(alpha, category.getId()));
			sub.add(subCategory);
		}

		// subCategories.sort((s1, s2) ->
		// s1.getMyanmarName().compareTo(s2.getMyanmarName()));
		resultJson.put("sub", sub);
		resultJson.put("status", true);
		return resultJson;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "bymaincategory", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject byMainCategory(@RequestHeader("token") String token, @RequestBody JSONObject json)
			throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("message", "Unauthorized Request");
			return resultJson;
		}

		Object mainCategory = json.get("main_category_id");
		if (mainCategory == null || mainCategory.toString().isEmpty()) {
			resultJson.put("status", false);
			resultJson.put("message", "Main Category is not valid!");
			return resultJson;
		}

		Category category = categoryService.findByBoId(mainCategory.toString());
		resultJson.put("status", true);
		resultJson.put("subcategories", category.getSubCategories());
		return resultJson;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "boId", method = RequestMethod.POST) // web
	@JsonView(Views.Thin.class)
	public JSONObject findByBoId(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject result = new JSONObject();
		SubCategory subCategory = subCategoryService.findByBoId(json.get("boId").toString().trim());

		result.put("subCategory", subCategory);
		result.put("books", bookService.getBooksBySubCategoryId(subCategory.getId()));
		return result;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "count", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public String getCount() throws ServiceUnavailableException {
		return subCategoryService.countActiveSubCategory() + "";
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "importcategory", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public String importCategory() throws ServiceUnavailableException {
		subCategoryService.getAll().forEach(sub -> {
			Long categoryId = categoryService.findBySubCategoryId(sub.getId());
			logger.info(categoryId);
			Category category = categoryService.findByCategoryId(categoryId);
			if (category != null)
				sub.setCategoryBoId(category.getBoId());
			try {
				subCategoryService.save(sub);
			} catch (ServiceUnavailableException e) {
				logger.error("Ërror: " + e);
			}
		});

		return "success";
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "setDisplayList", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject setDisplayList(@RequestBody JSONObject reqJson) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject ();
		List<Object> subCategories = (List<Object>) reqJson.get("subcategoryBoId");
		
		for (Object object : subCategories) {
			SubCategory subCategory = subCategoryService.findByBoId(object.toString());
			if (subCategory != null) {
				subCategory.setDisplay("true");
				subCategoryService.save(subCategory);
			}
				
				
		}
		resultJson.put("status","1");
		resultJson.put("msg", "Success!");
		return resultJson;
		
		
	}
	
}

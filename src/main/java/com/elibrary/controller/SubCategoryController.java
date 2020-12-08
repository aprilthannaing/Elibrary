package com.elibrary.controller;

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
	public JSONObject byEngCategory(@RequestHeader("token") String token, @RequestBody JSONObject json) throws ServiceUnavailableException {
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

		Category category = categoryService.findByBoId("CATEGORY10004");
		List<SubCategory> subCategories = category.getSubCategories();
		subCategories.sort((s1, s2) -> s1.getMyanmarName().compareTo(s2.getMyanmarName()));
		resultJson.put("status", true);
		resultJson.put("subcategories", subCategories);
		return resultJson;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "bymaincategory", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject byMainCategory(@RequestHeader("token") String token, @RequestBody JSONObject json) throws ServiceUnavailableException {
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
	@RequestMapping(value = "boId", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject findByBoId(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject result = new JSONObject();
		SubCategory subCategory = subCategoryService.findByBoId(json.get("boId").toString().trim());
		String subCategoryId = String.valueOf(subCategory.getId());

		if (subCategory != null) {
			result.put("subCategory", subCategory);
			result.put("subCategoryId",subCategoryId);
			//subc.fin(boid
			//categoryId from category_subcat where subId= "sub.id
//			result.put("category", cat);

		}
		return result;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "count", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public String getCount() throws ServiceUnavailableException {
		return subCategoryService.countActiveSubCategory() + "";
	}

}

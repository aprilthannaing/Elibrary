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
import com.elibrary.entity.AuthorType;
import com.elibrary.entity.Category;
import com.elibrary.entity.Views;
import com.elibrary.service.AuthorService;
import com.elibrary.service.CategoryService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("author")
public class AuthorController extends AbstractController {

	@Autowired
	private AuthorService authorService;

	@Autowired
	private CategoryService categoryService;

	private static Logger logger = Logger.getLogger(SubCategoryController.class);

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "all", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public JSONObject get() throws ServiceUnavailableException {
		JSONObject result = new JSONObject();

		List<Author> authorList = new ArrayList<Author>();
		authorList = authorService.getAll();

		result.put("status", true);
		result.put("authors", authorList);
		return result;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "allBySelected", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject getAllBySelected(@RequestBody JSONObject reqJson) throws ServiceUnavailableException {
		JSONObject result = new JSONObject();

		String checkToShow = reqJson.get("selected").toString();

		Object page = reqJson.get("page");
		if (page == null || page.toString().isEmpty()) {
			result.put("status", false);
			result.put("message", "This page is not found!");
			return result;
		}
		int pageNo;
		try {
			pageNo = Integer.parseInt(page.toString());
		} catch (Exception e) {
			result.put("status", false);
			result.put("message", "This page is not found!");
			return result;
		}

		List<Author> authorList = new ArrayList<Author>();
		if (checkToShow.equals("1"))

			authorList = authorService.getAuthorList(AuthorType.LOCAL);

		else
			authorList = authorService.getAuthorList(AuthorType.INTERNATIONAL);

		int lastPageNo = authorList.size() % 10 == 0 ? authorList.size() / 10 : authorList.size() / 10 + 1;

		List<Author> authors = getAuthorByPaganation(authorList, pageNo);

		result.put("status", true);
		result.put("current_page", pageNo);
		result.put("last_page", lastPageNo);
		result.put("total_count", authorList.size());
		result.put("authors", authors);
		return result;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "", method = RequestMethod.POST) // mobile
	@JsonView(Views.Summary.class)
	public JSONObject getAllByPaganation(@RequestHeader("token") String token, @RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("message", "Unauthorized Request");
			return resultJson;
		}

		int page = getPage(json);
		if (page <= 0) {
			resultJson.put("status", false);
			resultJson.put("message", "Page is not valid!");
			return resultJson;
		}

		Category category = getCategory(json);
		if (category != null) {
			// by category

			Long categoryId = category.getId();
			List<Author> localAuthorList = authorService.getAuthorListByCategory(categoryId, AuthorType.LOCAL);
			int localLastPageNo = localAuthorList.size() % 10 == 0 ? localAuthorList.size() / 10 : localAuthorList.size() / 10 + 1;

			List<Author> interAuthorList = authorService.getAuthorListByCategory(categoryId, AuthorType.INTERNATIONAL);
			int interLastPageNo = interAuthorList.size() % 10 == 0 ? interAuthorList.size() / 10 : interAuthorList.size() / 10 + 1;

			resultJson.put("status", true);
			resultJson.put("current_page", page);
			resultJson.put("local_last_page", localLastPageNo);
			resultJson.put("inter_last_page", interLastPageNo);
			resultJson.put("local_author", getAuthorByPaganation(localAuthorList, page));
			resultJson.put("international_author", getAuthorByPaganation(interAuthorList, page));
			return resultJson;
		}

		// all
		List<Author> localAuthorList = authorService.getAuthorList(AuthorType.LOCAL);
		int localLastPageNo = localAuthorList.size() % 10 == 0 ? localAuthorList.size() / 10 : localAuthorList.size() / 10 + 1;

		List<Author> interAuthorList = authorService.getAuthorList(AuthorType.INTERNATIONAL);
		int interLastPageNo = interAuthorList.size() % 10 == 0 ? interAuthorList.size() / 10 : interAuthorList.size() / 10 + 1;

		resultJson.put("status", true);
		resultJson.put("current_page", page);
		resultJson.put("local_last_page", localLastPageNo);
		resultJson.put("inter_last_page", interLastPageNo);
		resultJson.put("local_author", getAuthorByPaganation(localAuthorList, page));
		resultJson.put("international_author", getAuthorByPaganation(interAuthorList, page));
		return resultJson;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "boId", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject getAll(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject result = new JSONObject();
		result.put("author", authorService.findByBoId(json.get("boId").toString()));
		return result;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "count", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public String getCount() throws ServiceUnavailableException {
		return authorService.countAuthor() + "";
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "search", method = RequestMethod.POST) // authorsearch
	@JsonView(Views.Summary.class)
	public JSONObject getHomePageByCategory(@RequestHeader("token") String token, @RequestBody JSONObject json) throws ServiceUnavailableException, ClassNotFoundException, SQLException {
		JSONObject resultJson = new JSONObject();

		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "Unauthorized Request");
			return resultJson;
		}

		Object name = json.get("name");
		if (name == null || name.toString().isEmpty()) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "Searchterm must not null");
			return resultJson;
		}

		int page = getPage(json);
		Category category = getCategory(json);

		List<Long> authorIdList = authorService.getAuthorIdByCategoryIdAndName(category == null ? 0 : category.getId(), name.toString());
		int lastPageNo = authorIdList.size() % 10 == 0 ? authorIdList.size() / 10 : authorIdList.size() / 10 + 1;

		resultJson.put("author", getAuthorsByPagination(authorIdList, page));
		resultJson.put("current_page", page);
		resultJson.put("last_page", lastPageNo);
		resultJson.put("total_count", authorIdList.size());
		resultJson.put("status", true);
		return resultJson;
	}
}

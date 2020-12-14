package com.elibrary.controller;

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
import com.elibrary.entity.AuthorType;
import com.elibrary.entity.Category;
import com.elibrary.entity.Views;
import com.elibrary.service.AuthorService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("author")
public class AuthorController extends AbstractController {

	@Autowired
	private AuthorService authorService;

	private static Logger logger = Logger.getLogger(SubCategoryController.class);

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "all", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public JSONObject getAll() throws ServiceUnavailableException {
		JSONObject result = new JSONObject();
		result.put("authors", authorService.getAll());
		return result;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject getAllByPaganation(@RequestBody JSONObject json) throws ServiceUnavailableException {
		JSONObject resultJson = new JSONObject();
		int page = getPage(json);
		if (page <= 0) {
			resultJson.put("status", false);
			resultJson.put("message", "Page is not valid!");
			return resultJson;
		}

		Category category = getCategory(json);
		logger.info("categoryObject wereofodifidfcategory !!!!!!!!!!!!!!!!!!" + category);
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
}

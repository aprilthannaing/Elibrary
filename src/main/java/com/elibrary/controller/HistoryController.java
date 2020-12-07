package com.elibrary.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import com.elibrary.entity.Book;
import com.elibrary.entity.History;
import com.elibrary.entity.SystemConstant;
import com.elibrary.entity.User;
import com.elibrary.entity.Views;
import com.elibrary.service.BookService;
import com.elibrary.service.HistoryService;
import com.elibrary.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("history")
public class HistoryController extends AbstractController {

	@Autowired
	private HistoryService historyService;

	@Autowired
	private UserService userService;

	@Autowired
	private BookService bookService;

	private static Logger logger = Logger.getLogger(SubCategoryController.class);

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "action", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject read(@RequestHeader("token") String token, @RequestBody JSONObject json) throws ServiceUnavailableException {

		JSONObject resultJson = new JSONObject();
		if (!isTokenRight(token)) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "Unauthorized Request");
			return resultJson;
		}

		Object userId = json.get("user_id");
		Object bookId = json.get("book_id");
		Object status = json.get("action_status");
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

		if (bookId == null || bookId.toString().isEmpty()) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "This book is not found!");
			return resultJson;
		}

		Book book = bookService.findByBoId(bookId.toString());
		if (book == null) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "This book is not found!");
			return resultJson;
		}

		if (status == null || status.toString().isEmpty()) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "Action Status is not valid!");
			return resultJson;
		}

		ActionStatus actionStatus = null;
		try {
			actionStatus = ActionStatus.valueOf(status.toString().toUpperCase());
			if (!History.isValidAction(actionStatus)) {
				resultJson.put("status", false);
				resultJson.put("err_msg", "Action Status is not valid!");
				return resultJson;
			}
		} catch (Exception e) {
			resultJson.put("status", false);
			resultJson.put("err_msg", "Action Status is not valid!");
			return resultJson;
		}

		History history = new History();
		history.setBoId(SystemConstant.BOID_REQUIRED);
		history.setUserId(user);
		history.setBookId(book);
		history.setActionStatus(actionStatus);
		Date date = new Date();
		String createdDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
		history.setDateTime(createdDate);
		historyService.save(history);

		resultJson.put("status", true);
		resultJson.put("message", "Success");
		return resultJson;
	}

}

package com.elibrary.controller;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elibrary.entity.Views;
import com.elibrary.service.HistoryService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("history")
public class HistoryController {

	@Autowired
	private HistoryService historyService;

	private static Logger logger = Logger.getLogger(SubCategoryController.class);

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public JSONObject get() throws ServiceUnavailableException {
		JSONObject result = new JSONObject();
		return result;
	}
	
	
	
	

}

package com.elibrary.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elibrary.entity.Department;
import com.elibrary.entity.EntityStatus;
import com.elibrary.entity.Hluttaw;
import com.elibrary.entity.Position;
import com.elibrary.entity.Request;
import com.elibrary.entity.User;
import com.elibrary.entity.UserRole;
import com.elibrary.entity.Views;
import com.elibrary.service.ListOfValueService;
import com.elibrary.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("user")
public class UserController  extends AbstractController{
	@Autowired
	private ListOfValueService listOfValueService;
	
	@Autowired
	private UserService userservice;
	
	@RequestMapping(value = "setuserinfo", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public String setuserinfo(@RequestBody User req){
		try {
			if (Validation(req)) {
				Hluttaw htaw = listOfValueService.checkHluttawById(req.getHlutawType());
				Department dept = listOfValueService.checkDepartmentbyId(req.getDeptType());
				Position pos = listOfValueService.getPositionbyId(req.getPositionType());
				if(!req.getBoId().equals("")) {
					User user  = userservice.selectUserByKey(req.getBoId());
					if(user != null) {
						user.setHluttaw(htaw);
						user.setDepartment(dept);
						user.setPosition(pos);
						user.setRole(UserRole.User);
						user.setCreatedDate(dateFormat());
						user.setModifiedDate(dateFormat());
						user.setName(req.getName());
						user.setEmail(req.getEmail());
						user.setPhoneNo(req.getPhoneNo());
						user.setType(req.getType());
						String status = req.getStatus();
						user.setStatus(status);
						if(status.equals("NEW"))
							user.setEntityStatus(EntityStatus.NEW);
						else if(status.equals("ACTIVE"))
							user.setEntityStatus(EntityStatus.ACTIVE);
						else if(status.equals("EXPIRED"))
							user.setEntityStatus(EntityStatus.EXPIRED);
						userservice.save(user);
						return "Update Successfully";
					}
				}
				req.setHluttaw(htaw);
				req.setDepartment(dept);
				req.setPosition(pos);
				req.setRole(UserRole.User);
				req.setCreatedDate(dateFormat());
				req.setModifiedDate(dateFormat());
				String status = req.getStatus();
				if(status.equals("NEW"))
					req.setEntityStatus(EntityStatus.NEW);
				else if(status.equals("ACTIVE"))
					req.setEntityStatus(EntityStatus.ACTIVE);
				else if(status.equals("EXPIRED"))
					req.setEntityStatus(EntityStatus.EXPIRED);
					userservice.save(req);
				return "Insert Successfully";
			}
		} catch (ServiceUnavailableException e) {
			e.printStackTrace();
		}
		return "fail";
	}
	
	public boolean Validation(User user) {
		if(user.getName().equals("") || user.getName().equals(null)) {
			return false;
		}
		if(user.getEmail().equals("") || user.getEmail().equals(null)) {
			return false;
		}
		if(user.getPhoneNo().equals("") || user.getPhoneNo().equals(null)) {
			return false;
		}
		if(user.getType().equals("") || user.getType().equals(null)) {
			return false;
		}
		if(user.getHlutawType() <= 0) {
			return false;
		}
		if(user.getDeptType() <= 0) {
			return false;
		}
		
		if(user.getPositionType() <= 0) {
			return false;
		}
		
		if(user.getStatus().equals("") || user.getStatus().equals(null)) {
			return false;
		}
		
		return true;
	}
	@RequestMapping(value = "selectUserInfo", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public List<User> selectUserInfo(@RequestBody Request req){
		List<User> resList = new ArrayList<User>();
		resList = userservice.selectUser(req);
		if(resList.size() > 0) {
			User user = resList.get(0);
		}
		return  resList;
	}
	
	@RequestMapping(value = "selectUserbykey", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public User selectUserbykey(@RequestBody String key){
		User user = userservice.selectUserByKey(key);
		return  user;
	}
}

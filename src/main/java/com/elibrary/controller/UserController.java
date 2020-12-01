package com.elibrary.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.elibrary.entity.Session;
import com.elibrary.entity.User;
import com.elibrary.entity.UserRole;
import com.elibrary.entity.Views;
import com.elibrary.service.HistoryService;
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
	
	@Autowired
	private HistoryService historyService;
	
	@RequestMapping(value = "setuserinfo", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public String setuserinfo(@RequestBody User req){
		String msg = "";
		User user = new User();
		try {
			if (Validation(req)) {
				String loginUserid = userservice.sessionActive(req.getSessionId());
				if(loginUserid.equals("")) {
					msg = "Session Fail";
					return msg;
				}
				Hluttaw htaw = listOfValueService.checkHluttawById(req.getHlutawType());
				Department dept = new Department();
				Position pos = new Position();
				dept = listOfValueService.checkDepartmentbyId(req.getDeptType());
				pos = listOfValueService.getPositionbyId(req.getPositionType());
				if(!req.getBoId().equals("")) {
					user  = userservice.selectUserByKey(req.getBoId());
					msg = "Update Successfully";
				}else {
					user.setSessionStatus(EntityStatus.NEW);
					user.setCreatedDate(dateFormat());
					user.setPassword(getRandomNumberString());
					user.setFromUserId(loginUserid);
					msg = "Insert Successfully";
				}
				user.setModifiedDate(dateFormat());
				user.setHluttaw(htaw);
				user.setDepartment(dept);
				user.setPosition(pos);
				user.setName(req.getName());
				user.setEmail(req.getEmail());
				user.setPhoneNo(req.getPhoneNo());
				user.setType(req.getType());
				String status = req.getStatus();
				user.setStatus(status);
				//Role
				if(req.getRoleType().equals("Admin"))
					user.setRole(UserRole.Admin);
				if(req.getRoleType().equals("Librarian"))
					user.setRole(UserRole.Librarian);
				if(req.getRoleType().equals("SuperLibrarian"))
					user.setRole(UserRole.SuperLibrarian);
				if(req.getRoleType().equals("User"))
					user.setRole(UserRole.User);
				//Status
				if(status.equals("NEW"))
					user.setEntityStatus(EntityStatus.NEW);
				else if(status.equals("ACTIVE"))
					user.setEntityStatus(EntityStatus.ACTIVE);
				else if(status.equals("EXPIRED"))
					user.setEntityStatus(EntityStatus.EXPIRED);
				userservice.save(user);
				//saveHistory(user.getBoId(),loginUserid);
				return msg;
			}
		} catch (ServiceUnavailableException e) {
			e.printStackTrace();
		}
		return "fail";
	}
//	public void saveHistory(String id,String loginUserid) {
//		try {
//			User user = userservice.selectUserbyId(loginUserid);
//			userservice.sessionActive(loginUserid);
//			History his = new History();
//			his.setEntityStatus(EntityStatus.ACTIVE);
//			his.setWorkStatus(WorkStatus.);
//			his.setDateTime(dateFormat());
//			his.setUser(user);
//			his.setToUserId(id);
//			historyService.save(his);
//		} catch (ServiceUnavailableException e) {
//			e.printStackTrace();
//		}
//	}
	
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
	
	@RequestMapping(value = "getLogin", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin(origins = "*")
	@JsonView(Views.Summary.class)
	public JSONObject getLogin(@RequestBody JSONObject reqJson) throws ServiceUnavailableException{
		JSONObject resJson = new JSONObject();
		String message = "";
		String email 	= reqJson.get("_email").toString();
		String password = reqJson.get("_psw").toString();
		message = this.goValidation(email, password); 
		if(!message.equals("")) {
			resJson.put("desc", message);
			resJson.put("code", "001");
			return resJson;
		}
		User user = userservice.getLogin(email, password);
		if(user != null) {
			if(user.getSessionStatus().equals(EntityStatus.NEW)) {
				resJson.put("desc", "first time login");
				resJson.put("code", "002");
				resJson.put("userId", user.getBoId());
				return resJson;
			}
			//String sessionid = userservice.checkSession(user);
			//session
			String sessionId = saveSession(user);
			resJson.put("desc", message);
			resJson.put("code", "000");
			resJson.put("role", user.getRole().name());
			resJson.put("name", user.getName());
			resJson.put("sessionId", sessionId);
			resJson.put("userId", user.getBoId());
			return resJson;
		}
		resJson.put("desc", "User not found!");
		resJson.put("code", "001");
			
	return resJson;
	}
	public String saveSession(User user) {
		Session session = new Session();
		session.setEntityStatus(EntityStatus.ACTIVE);
		session.setStartDate(dateFormat());
		session.setEndDate(dateFormat());
		session.setUser(user);
		return userservice.save(session);
	}
	@RequestMapping(value = "goChangepwd", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject goChangepwd(@RequestBody JSONObject reqJson) throws ServiceUnavailableException {
		JSONObject resJson = new JSONObject();
		String oldpwd 	= reqJson.get("oldpwd").toString();
		String newpwd = reqJson.get("newpwd").toString();
		String userId = reqJson.get("userId").toString();
		User user  = userservice.selectUserByKey(userId);
		if(user != null) {
			if(user.getPassword().equals(newpwd) || !user.getPassword().equals(oldpwd)) {
				resJson.put("message", "Your new password cannot be the same as your old password. Please enter a different password");
				resJson.put("code", "001");
				return resJson;
			}
			user.setPassword(newpwd);
			user.setSessionStatus(EntityStatus.ACTIVE);
			userservice.save(user);
			String sessionId = saveSession(user);
			resJson.put("desc", "Update Successfully");
			resJson.put("code", "000");
			resJson.put("role", user.getRole().name());
			resJson.put("name", user.getName());
			resJson.put("sessionId", sessionId);
			resJson.put("userId", user.getBoId());
		}
		return resJson;
	}
	
	
	
	public String goValidation(String email,String password) {
		if(email.equals("") && password.equals(""))
		    return "Please enter your email address and password";
		if(email.equals(""))
			return "Please enter your email address";
		if(!email.contains("@"))
			return "Your email address is incorrect";
		if(password.equals(""))
			return "Please enter your password";
		return "";
	}
}

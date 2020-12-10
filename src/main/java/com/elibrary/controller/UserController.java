package com.elibrary.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.elibrary.service.impl.MailServiceImpl;
import com.fasterxml.jackson.annotation.JsonView;
import com.mchange.rmi.ServiceUnavailableException;

@RestController
@RequestMapping("user")
public class UserController extends AbstractController {
	@Autowired
	private ListOfValueService listOfValueService;

	@Autowired
	private UserService userservice;

	private static Logger logger = Logger.getLogger(UserController.class);

	@RequestMapping(value = "setuserinfo", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject setuserinfo(@RequestBody User req) {
		JSONObject jsonRes = new JSONObject();
		String msg = "";
		User user = new User();
		try {
			jsonRes = Validation(req);
			if (jsonRes.get("code").equals("000")) {
				String loginUserid = userservice.sessionActive(req.getSessionId().trim());
				if (!loginUserid.equals("") || loginUserid.equals("000")) {
				} else {

					jsonRes.put("code", "001");
					jsonRes.put("desc", "Session Fail");
					return jsonRes;
				}
				Hluttaw htaw = listOfValueService.checkHluttawById(req.getHlutawType());
				Department dept = new Department();
				Position pos = new Position();
				dept = listOfValueService.checkDepartmentbyId(req.getDeptType());
				pos = listOfValueService.getPositionbyId(req.getPositionType());
				if (!req.getBoId().equals("")) {
					user = userservice.selectUserByKey(req.getBoId());
					msg = "Update Successfully";
				} else {
					User user1 = userservice.selectUserbyEmail(req.getEmail().trim());
					if (user1 != null) {

						msg = "Email is already exit";
						jsonRes.put("code", "001");
						jsonRes.put("desc", msg);
						return jsonRes;
					}
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
				user.setEmail(req.getEmail().trim());
				user.setPhoneNo(req.getPhoneNo().trim());
				user.setType(req.getType());
				user.setRoleType(req.getRoleType());
				// Role
				if (req.getRoleType().equals("Admin"))
					user.setRole(UserRole.Admin);
				if (req.getRoleType().equals("Librarian"))
					user.setRole(UserRole.Librarian);
				if (req.getRoleType().equals("SuperLibrarian"))
					user.setRole(UserRole.SuperLibrarian);
				if (req.getRoleType().equals("User"))
					user.setRole(UserRole.User);
				// Status
				String status = req.getStatus();
				user.setStatus(status);
				if (status.equals("NEW"))

					user.setEntityStatus(EntityStatus.NEW);
				else if (status.equals("ACTIVE"))
					user.setEntityStatus(EntityStatus.ACTIVE);
				else if (status.equals("EXPIRED"))
					user.setEntityStatus(EntityStatus.EXPIRED);
				userservice.save(user);
				// saveHistory(user.getBoId(),loginUserid);
				jsonRes.put("code", "000");
				jsonRes.put("desc", msg);
				jsonRes.put("userList", user);
				return jsonRes;
			}
		} catch (ServiceUnavailableException e) {
			e.printStackTrace();
		}
		return jsonRes;
	}
//	public void saveHistory(String id,String loginUserid) {
//		try {
//			User user = userservice.selectUserbyId(loginUserid);
//			userservice.sessionActive(loginUserid);
//			History his = new History();
//			his.setEntityStatus(EntityStatus.ACTIVE);
//			his.setWorkStatus(WorkStatus.);
//			his.setUser(user);
//			his.setToUserId(id);
//			historyService.save(his);
//		} catch (ServiceUnavailableException e) {
//			e.printStackTrace();
//		}
//	}

	public JSONObject Validation(User user) {
		JSONObject jsonRes = new JSONObject();
		String message = "";
		if (user.getName().equals("") || user.getName().equals(null)) {
			message = "Please fill correct User Name";
		}
		if (user.getEmail().trim().equals("") || user.getEmail().trim().equals(null)) {
			message = "Please fill correct User Email";
		}
		if (user.getPhoneNo().trim().equals("") || user.getPhoneNo().trim().equals(null)) {
			message = "Please fill correct User Phone No";
		}
		if (!message.equals(""))

			jsonRes.put("code", "001");
		else
			jsonRes.put("code", "000");
		return jsonRes;
	}

	@RequestMapping(value = "selectUserInfo", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public List<User> selectUserInfo(@RequestBody Request req) {
		List<User> resList = new ArrayList<User>();
		resList = userservice.selectUser(req);
//		if(resList.size() > 0) {
//			User user = resList.get(0);
//		}
		return resList;
	}

	@RequestMapping(value = "selectUserbykey", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public User selectUserbykey(@RequestBody String key) {
		User user = userservice.selectUserByKey(key);
		return user;
	}

	@RequestMapping(value = "goLogin", method = RequestMethod.POST)
	@ResponseBody
	@CrossOrigin(origins = "*")
	@JsonView(Views.Summary.class)
	public JSONObject getLogin(@RequestBody JSONObject reqJson) throws ServiceUnavailableException {
		JSONObject resJson = new JSONObject();
		String message = "";
		String email = reqJson.get("email").toString();
		String password = reqJson.get("password").toString();
		message = this.goValidation(email, password);
		if (!message.equals("")) {
			resJson.put("message", message);
			resJson.put("status", false);
			return resJson;
		}
		User user = userservice.getLogin(email, password);
		if (user != null) {
			// session
			String sessionId = saveSession(user);
			if (user.getSessionStatus().equals(EntityStatus.NEW)) {
				resJson.put("message", "first time login");
				resJson.put("status", true);
				resJson.put("changePwd", true);
				resJson.put("token", sessionId);
				return resJson;
			}
			// String sessionid = userservice.checkSession(user);
			resJson.put("message", message);
			resJson.put("status", true);
			resJson.put("token", sessionId);
			JSONObject json1 = new JSONObject();
			json1.put("id", user.getBoId());
			json1.put("name", user.getName());
			json1.put("email", user.getEmail());
			json1.put("phoneNo", user.getPhoneNo());
			json1.put("hluttaw", listOfValueService.checkHluttawById(user.getHluttaw().getId()).getName());
			json1.put("department", listOfValueService.checkDepartmentbyId(user.getDepartment().getId()).getName());
			json1.put("position", listOfValueService.getPositionbyId(user.getPosition().getId()).getName());
			json1.put("type", user.getType());
			json1.put("role", user.getRole().name());
			json1.put("initialName", initialName(user.getName()));
			resJson.put("data", json1);
			return resJson;
		}
		resJson.put("message", "Your email or passord is incorrect.");
		resJson.put("status", false);

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
		String oldpwd = reqJson.get("old_password").toString().trim();
		String newpwd = reqJson.get("new_password").toString().trim();
		String sessionId = reqJson.get("token").toString();
		String loginUserid = userservice.sessionActive(sessionId);
		if (!loginUserid.equals("") || loginUserid.equals("000")) {
		} else {
			resJson.put("status", false);
			resJson.put("message", "Session Fail");
			return resJson;
		}
		User user = userservice.selectUserbyId(loginUserid);
		if (user != null) {
			if (user.getPassword().equals(newpwd) || !user.getPassword().equals(oldpwd)) {
				resJson.put("message", "Your new password cannot be the same as your old password. Please enter a different password");
				resJson.put("status", false);
				return resJson;
			}
			user.setPassword(newpwd);
			user.setSessionStatus(EntityStatus.ACTIVE);
			userservice.save(user);
			resJson.put("message", "Password changed Successfully");
			resJson.put("status", true);
		}
		return resJson;
	}

	public String goValidation(String email, String password) {
		if (email.equals("") && password.equals(""))
			return "Please enter your email address and password";
		if (email.equals(""))
			return "Please enter your email address";
		if (!email.contains("@"))
			return "Your email address is incorrect";
		if (password.equals(""))
			return "Please enter your password";
		return "";
	}

	@RequestMapping(value = "deleteUserinfo", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject deleteUserinfo(@RequestBody User req) {
		JSONObject jsonRes = new JSONObject();
		String msg = "";
		User user = new User();
		try {
			String loginUserid = userservice.sessionActive(req.getSessionId());
			if (loginUserid.equals("")) {
				jsonRes.put("code", "001");
				jsonRes.put("desc", "Session Fail");
				return jsonRes;
			}
			if (req.getBoId().equals("")) {
				jsonRes.put("code", "001");
				jsonRes.put("desc", "user not found");
			}
			user = userservice.selectUserByKey(req.getBoId());
			user.setModifiedDate(dateFormat());
			user.setEntityStatus(EntityStatus.DELETED);
			userservice.save(user);
			jsonRes.put("code", "000");
			jsonRes.put("desc", "Delete Successfully");
			return jsonRes;
		} catch (ServiceUnavailableException e) {
			e.printStackTrace();
		}
		return jsonRes;
	}

	@RequestMapping(value = "setusers", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject setusers(@RequestBody ArrayList<User> arrayList, @RequestParam("sessionId") String sessionId) {
		JSONObject jsonRes = new JSONObject();
		String rowCount = "";
		try {
			String loginUserid = userservice.sessionActive(sessionId);
			if (!loginUserid.equals("") || loginUserid.equals("000")) {
			} else {
				jsonRes.put("code", "001");
				jsonRes.put("desc", "Session Fail");
				return jsonRes;
			}
			for (int i = 0; i < arrayList.size(); i++) {
				jsonRes = Validation(arrayList.get(i));
				if (jsonRes.get("code").equals("000")) {
					Hluttaw htaw = listOfValueService.checkHluttawById(arrayList.get(i).getHlutawType());
					User user1 = userservice.selectUserbyEmail(arrayList.get(i).getEmail());
					if (user1 != null) {
						jsonRes.put("code", "001");
						jsonRes.put("desc", "Email '" + arrayList.get(i).getEmail() + "' is already registered.");
						return jsonRes;
					}
					Department dept = new Department();
					Position pos = new Position();
					dept = listOfValueService.checkDepartmentbyId(arrayList.get(i).getDeptType());
					pos = listOfValueService.getPositionbyId(arrayList.get(i).getPositionType());
					arrayList.get(i).setSessionStatus(EntityStatus.NEW);
					arrayList.get(i).setCreatedDate(dateFormat());
					arrayList.get(i).setModifiedDate(dateFormat());
					arrayList.get(i).setPassword(getRandomNumberString());
					arrayList.get(i).setFromUserId(loginUserid);
					arrayList.get(i).setHluttaw(htaw);
					arrayList.get(i).setDepartment(dept);
					arrayList.get(i).setPosition(pos);
					// Role
					arrayList.get(i).setRole(UserRole.User);
					// Status
					arrayList.get(i).setEntityStatus(EntityStatus.NEW);
					userservice.save(arrayList.get(i));
					rowCount += i;
				}

			}
			jsonRes.put("code", "000");
			jsonRes.put("desc", rowCount + " Row Inserted Successfully");

		} catch (ServiceUnavailableException e) {
			e.printStackTrace();
		}

		return jsonRes;
	}

	@RequestMapping(value = "selectUserInfobyStatus", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public List<User> selectUserInfobyStatus(@RequestBody Request req) {
		List<User> resList = new ArrayList<User>();
		resList = userservice.selectUserbyStatus(req);
		return resList;
	}

	@RequestMapping(value = "changeStatus", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject changeStatus(@RequestBody ArrayList<String> arrayList, @RequestParam("sessionId") String sessionId) {
		JSONObject jsonRes = new JSONObject();
		String rowCount = "";
		try {
			String loginUserid = userservice.sessionActive(sessionId);
			if (!loginUserid.equals("") || loginUserid.equals("000")) {
			} else {
				jsonRes.put("code", "001");
				jsonRes.put("desc", "Session Fail");
				return jsonRes;
			}
			for (int i = 0; i < arrayList.size(); i++) {
				User user = userservice.selectUserByKey(arrayList.get(i));
				// Status
				user.setModifiedDate(dateFormat());
				user.setEntityStatus(EntityStatus.ACTIVE);
				userservice.save(user);
				rowCount += i;

			}
			jsonRes.put("code", "000");
			jsonRes.put("desc", rowCount + " Row Approved Successfully");

		} catch (ServiceUnavailableException e) {
			e.printStackTrace();
		}

		return jsonRes;
	}

	@RequestMapping(value = "verifyEmail", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	private JSONObject verifyEmail(@RequestBody String email) throws Exception {
		JSONObject resultJson = new JSONObject();
		try {
			User user = userservice.selectUserbyEmail(email);
			if (user == null) {
				resultJson.put("message", "Email not found!");
				resultJson.put("status", false);
				return resultJson;
			}

			String code = getRandomNumberString();

			// mailService.sendMail(_email, "Email Address Verification", "Please verify
			// your email address for Elibray System.\n"
			// + "Your verification code is " + code);
			user.setVerificationCode(code);
			userservice.save(user);
			String sessionId = saveSession(user);
			resultJson.put("status", true);
			resultJson.put("token", sessionId);
			resultJson.put("message", "verification code sent to your " + user.getEmail());
		} catch (Exception e) {
			logger.error("Error: " + e);
			resultJson.put("message", "Can't send mail");
			resultJson.put("status", false);
			return resultJson;
		}
		return resultJson;
	}

	@RequestMapping(value = "verifyCode", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	private JSONObject verifyCode(@RequestBody JSONObject resJson) {
		JSONObject resultJson = new JSONObject();
		String loginUserid = userservice.sessionActive(resJson.get("token").toString());
		if (!loginUserid.equals("") || loginUserid.equals("000")) {
		} else {
			resJson.put("status", false);
			resJson.put("message", "Session Fail");
			return resJson;
		}
		User user = userservice.selectUserbyVerCode(loginUserid, resJson.get("code").toString());
		if (user == null) {
			resultJson.put("message", "Invalid Verification Code.");
			resultJson.put("status", false);
			return resultJson;
		}

		resultJson.put("status", true);
		resultJson.put("message", "success");
		return resultJson;
	}

	@RequestMapping(value = "goResetPassword", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject goResetPassword(@RequestBody JSONObject reqJson) throws ServiceUnavailableException {
		JSONObject resJson = new JSONObject();
		String newpwd = reqJson.get("password").toString();
		String sessionId = reqJson.get("token").toString();
		String loginUserid = userservice.sessionActive(sessionId);
		if (!loginUserid.equals("") || loginUserid.equals("000")) {
		} else {
			resJson.put("status", false);
			resJson.put("message", "Session Fail");
			return resJson;
		}
		User user = userservice.selectUserbyId(loginUserid);
		if (user != null) {
			user.setPassword(newpwd);
			user.setSessionStatus(EntityStatus.ACTIVE);
			userservice.save(user);
			resJson.put("message", "success");
			resJson.put("status", true);
		}
		return resJson;
	}
}

package com.elibrary.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elibrary.entity.AES;
import com.elibrary.entity.Constituency;
import com.elibrary.entity.Department;
import com.elibrary.entity.EntityStatus;
import com.elibrary.entity.Hluttaw;
import com.elibrary.entity.Position;
import com.elibrary.entity.Request;
import com.elibrary.entity.Session;
import com.elibrary.entity.User;
import com.elibrary.entity.UserRole;
import com.elibrary.entity.Views;
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

	@Autowired
	private MailServiceImpl mailService;
	
	private static Logger logger = Logger.getLogger(UserController.class);

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "mail", method = RequestMethod.GET)
	@JsonView(Views.Summary.class)
	public JSONObject getAll() throws ServiceUnavailableException {
		JSONObject result = new JSONObject();
		mailService.sendMail("htethtetsan57@gmail.com", "hihihihi", "helllo hello hello hello");
		return result;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "setuserinfo", method = RequestMethod.POST)
	@JsonView(Views.Summary.class)
	public JSONObject setuserinfo(@RequestBody User req) throws Exception {
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
				Constituency consti = new Constituency();
				dept = listOfValueService.checkDepartmentbyId(req.getDeptType());
				pos = listOfValueService.getPositionbyId(req.getPositionType());
				consti = listOfValueService.getConstituencyById(req.getConstituencyType());
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
					//user.setPassword(getRandomNumberString());
					user.setPassword(generatePassword());
					user.setFromUserId(loginUserid);
					msg = "Insert Successfully";
				}
				user.setCurrentAddress(req.getCurrentAddress());
				user.setPermanentAddress(req.getPermanentAddress());
				user.setModifiedDate(dateFormat());
				user.setHluttaw(htaw);
				user.setHlutawType(req.getHlutawType());// response
				user.setDepartment(dept);
				user.setDeptType(req.getDeptType());// response
				user.setPosition(pos);
				user.setPositionType(req.getPositionType());// response
				user.setConstituency(consti);
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
				user.setRoleType(req.getRoleType());// response
				// Status
				String status = req.getStatus();
				user.setStatus(status);// response
				if (status.equals("NEW"))
					user.setEntityStatus(EntityStatus.NEW);
				else if (status.equals("ACTIVE")) {
					if(user.getEntityStatus()!= null) {
						if(!status.equals(user.getEntityStatus().name())) {
							 mailService.sendMail(req.getEmail().trim(), "Elibrary : Your New Account",
							 "Welcome!Please verify your email address for Elibray System.\n"
							 + "Your password is "+ user.getPassword() + ".");
						}
					}
					user.setEntityStatus(EntityStatus.ACTIVE);
				} else if (status.equals("EXPIRED"))
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

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "selectUserInfo", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject selectUserInfo(@RequestBody Request req) {
		JSONObject resJson = new JSONObject();
		List<User> resList = new ArrayList<User>();
		resList = userservice.selectUser(req);

		int lastPageNo = resList.size() % 10 == 0 ? resList.size() / 10 : resList.size() / 10 + 1;
		List<User> users = getUsersByPagination(req, resList, req.getCurrentpage());
		if (resList.size() > 0) {
			User user = resList.get(0);
		}
		resJson.put("users", resList);
		resJson.put("currentPage", req.getCurrentpage());
		// resJson.put("lastPage", lastPageNo);
		resJson.put("totalCount", resList.size());
		return resJson;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "selectUserbykey", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public User selectUserbykey(@RequestBody String key) {
		User user = userservice.selectUserByKey(key);
		return user;
	}

	@ResponseBody
	@CrossOrigin(origins = "*")
	@JsonView(Views.Summary.class)
	@RequestMapping(value = "goLogin", method = RequestMethod.POST)
	public JSONObject getLogin(@RequestBody JSONObject reqJson) throws Exception {
		JSONObject resJson = new JSONObject();
		String message = "";
		String email = reqJson.get("email").toString();

		String password = AES.decryptWithMobile(reqJson.get("password").toString(), secretKeyByMobile);
		// To use AES/ECB(decrypt)
		message = this.goValidation(email, password);
		if (!message.equals("")) {
			resJson.put("message", message);
			resJson.put("status", false);
			return resJson;
		}

		User user = userservice.getLogin(email, password);
		if (user != null) {
			// session
			String sessionId = saveSession(user);// diff
			if (user.getSessionStatus().equals(EntityStatus.NEW)) {
				resJson.put("message", "first time login");
				resJson.put("status", true);
				resJson.put("changePwd", true);
				resJson.put("token", sessionId);
				return resJson;
			}
			// String sessionid = userservice.checkSession(user);
			resJson.put("message", "success");
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

	@ResponseBody
	@CrossOrigin(origins = "*")
	@JsonView(Views.Summary.class)
	@RequestMapping(value = "goLoginByAdmin", method = RequestMethod.POST)
	public JSONObject goLoginByAdmin(@RequestBody JSONObject reqJson) throws Exception {
		JSONObject resJson = new JSONObject();
		String message = "";
		String email = reqJson.get("email").toString();
		byte[] base64DecryptedPassword = Base64.getDecoder().decode(reqJson.get("password").toString());
		String password = AES.decrypt(base64DecryptedPassword, secretKey);

		message = this.goValidation(email, password);
		if (!message.equals("")) {
			resJson.put("message", message);
			resJson.put("status", false);
			return resJson;
		}
		User user = userservice.getLoginByAdmin(email, password);// diff
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
		resJson.put("password", password);
		return resJson;
	}

	public String saveSession(User user) {
		Session session = new Session();
		session.setEntityStatus(EntityStatus.ACTIVE);
		session.setStartDate(dateTimeFormat());
		session.setEndDate(dateTimeFormat());
		session.setUser(user);
		return userservice.save(session);
	}

	@RequestMapping(value = "goChangepwd", method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject goChangepwd(@RequestBody JSONObject reqJson, @RequestHeader("token") String token) throws Exception {
		JSONObject resJson = new JSONObject();

		String oldPassword = AES.decryptWithMobile(reqJson.get("old_password").toString(), secretKeyByMobile);

		String newPassword = AES.decryptWithMobile(reqJson.get("new_password").toString(), secretKeyByMobile);

		String loginUserid = userservice.sessionActive(token);
		if (!loginUserid.equals("") || loginUserid.equals("000")) {
		} else {
			resJson.put("status", false);
			resJson.put("message", "Session Fail");
			return resJson;
		}

		User user = userservice.selectUserbyId(loginUserid);
		if (user != null) {
			if(!user.getPassword().equals(oldPassword)) {
				resJson.put("message", "Your old password is wroung!");
				resJson.put("status", false);
				return resJson;
			}
			if (user.getPassword().equals(newPassword)) {
				resJson.put("message", "Your new password cannot be the same as your old password. Please enter a different password");
				resJson.put("status", false);
				return resJson;
			}
			resJson = checkPasswordPolicyPattern(newPassword);
			boolean status = Boolean.parseBoolean(resJson.get("status").toString());
			if(!status) {
				return resJson;
			}
			user.setPassword(newPassword);
			user.setSessionStatus(EntityStatus.ACTIVE);
			userservice.save(user);
			 mailService.sendMail(user.getEmail(), "Elibrary : Your password was changed",
			 "Please verify your email address for Elibray System.\n"
			 + "Your new password is " + user.getPassword());
			resJson.put("message", "Password changed Successfully");
			resJson.put("status", true);
		}
		return resJson;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "goChangepwdByAdmin", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject goChangepwdByAdmin(@RequestBody JSONObject reqJson, @RequestHeader("token") String token) throws Exception {
		JSONObject resJson = new JSONObject();
		byte[] base64DecryptedOldPassword = Base64.getDecoder().decode(reqJson.get("old_password").toString());
		String oldpwd = AES.decrypt(base64DecryptedOldPassword, secretKey);

		byte[] base64DecryptedNewPassword = Base64.getDecoder().decode(reqJson.get("new_password").toString());
		String newpwd = AES.decrypt(base64DecryptedNewPassword, secretKey);

		String loginUserid = userservice.sessionActive(token);
		if (!loginUserid.equals("") || loginUserid.equals("000")) {
		} else {
			resJson.put("status", false);
			resJson.put("message", "Session Fail");
			return resJson;
		}

		User user = userservice.selectUserbyId(loginUserid);
		if (user != null) {
			if(!user.getPassword().equals(oldpwd)) {
				resJson.put("message", "Your old password is wroung!");
				resJson.put("status", false);
				return resJson;
			}
			if (user.getPassword().equals(newpwd)) {
				resJson.put("message", "Your new password cannot be the same as your old password. Please enter a different password");
				resJson.put("status", false);
				return resJson;
			}
			resJson = checkPasswordPolicyPattern(newpwd);
			boolean status = Boolean.parseBoolean(resJson.get("status").toString());
			if(!status) {
				return resJson;
			}
			user.setPassword(newpwd);
			user.setSessionStatus(EntityStatus.ACTIVE);
			userservice.save(user);
			mailService.sendMail(user.getEmail(), "Elibrary : Your password was changed", "Please verify your email address for Elibray System.\n" + "Your new password is " + user.getPassword());
			resJson.put("message", "Password changed Successfully");
			resJson.put("status", true);
		}
		return resJson;
	}
	public String goValidation(String email, String password) {
		if (email.isEmpty() && password.isEmpty())
			return "Please enter your email address and password!";
		if (email.isEmpty())
			return "Please enter your email address!";
		if (!email.contains("@"))
			return "Your email address is incorrect!";

		if (password != null && password.isEmpty())
			return "Please enter your password!";
		return "";
	}

	@CrossOrigin(origins = "*")
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

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "setusers", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject setusers(@RequestBody ArrayList<User> arrayList, @RequestParam("sessionId") String sessionId) {
		JSONObject jsonRes = new JSONObject();
		int rowCount = 0;
		try {
			String loginUserid = userservice.sessionActive(sessionId);
			if (!loginUserid.equals("") || loginUserid.equals("000")) {
			} else {
				jsonRes.put("code", "001");
				jsonRes.put("desc", "Session Fail");
				return jsonRes;
			}
			for (int i = 0; i < arrayList.size(); i++) {
				rowCount = i + 1;
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
					Constituency consti = new Constituency();
					dept = listOfValueService.checkDepartmentbyId(arrayList.get(i).getDeptType());
					pos = listOfValueService.getPositionbyId(arrayList.get(i).getPositionType());
					consti = listOfValueService.getConstituencyById(arrayList.get(i).getConstituencyType());
					arrayList.get(i).setSessionStatus(EntityStatus.NEW);
					arrayList.get(i).setCreatedDate(dateFormat());
					arrayList.get(i).setModifiedDate(dateFormat());
					arrayList.get(i).setPassword(getRandomNumberString());
					arrayList.get(i).setFromUserId(loginUserid);
					arrayList.get(i).setHluttaw(htaw);
					arrayList.get(i).setDepartment(dept);
					arrayList.get(i).setPosition(pos);
					arrayList.get(i).setConstituency(consti);
					// Role
					arrayList.get(i).setRole(UserRole.User);
					// Status
					arrayList.get(i).setEntityStatus(EntityStatus.NEW);
					userservice.save(arrayList.get(i));
				}

			}
			jsonRes.put("code", "000");
			jsonRes.put("desc", rowCount + " Row Inserted Successfully");

		} catch (ServiceUnavailableException e) {
			e.printStackTrace();
		}

		return jsonRes;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "selectUserInfobyStatus", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public List<User> selectUserInfobyStatus(@RequestBody Request req) {
		List<User> resList = new ArrayList<User>();
		resList = userservice.selectUserbyStatus(req);

		return resList;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "changeStatus", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject changeStatus(@RequestBody ArrayList<String> arrayList, @RequestParam("sessionId") String sessionId) {
		JSONObject jsonRes = new JSONObject();
		int rowCount = 0;
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
				rowCount = i + 1;

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
	@CrossOrigin(origins = "*")
	private JSONObject verifyEmail(@RequestBody JSONObject json) throws Exception {

		JSONObject resultJson = new JSONObject();
		try {
			User user = userservice.selectUserbyEmailActive(json.get("email").toString());
			if (user == null) {
				resultJson.put("message", "Email not found!");
				resultJson.put("status", false);
				return resultJson;
			}

			String code = getRandomNumberString();
			mailService.sendMail(json.get("email").toString(), "Elibrary : Email Address Verification", "Please verify your email address for Elibray System.\n" + "Your verification code is " + code);
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

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "verifyCode", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	private JSONObject verifyCode(@RequestBody JSONObject resJson, @RequestHeader("token") String token) {
		JSONObject resultJson = new JSONObject();
		String loginUserid = userservice.sessionActive(token);
		if (!loginUserid.equals("") || loginUserid.equals("000")) {
		} else {
			resJson.put("status", false);
			resJson.put("message", "Session Fail");
			return resJson;
		}
		User user = userservice.selectUserbyVerCode(loginUserid, resJson.get("code").toString(), resJson.get("email").toString());
		if (user == null) {
			resultJson.put("message", "Invalid Verification Code.");
			resultJson.put("status", false);
			return resultJson;
		}

		resultJson.put("status", true);
		resultJson.put("message", "success");
		return resultJson;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "goResetPassword", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject goResetPassword(@RequestBody JSONObject reqJson, @RequestHeader("token") String token) throws Exception {
		JSONObject resJson = new JSONObject();
		String newPassword = AES.decryptWithMobile(reqJson.get("password").toString(), secretKeyByMobile);

		String email = reqJson.get("email").toString();
		String code = reqJson.get("code").toString();
		String loginUserid = userservice.sessionActive(token);
		if (!loginUserid.equals("") || loginUserid.equals("000")) {
		} else {
			resJson.put("status", false);
			resJson.put("message", "Session Fail");
			return resJson;
		}
		resJson = checkPasswordPolicyPattern(newPassword);
		boolean status = Boolean.parseBoolean(resJson.get("status").toString());
		if(!status) {
			return resJson;
		}
		User user = userservice.selectUserbyVerCode(loginUserid, code, email);
		if (user != null) {

			user.setPassword(newPassword);
			user.setSessionStatus(EntityStatus.ACTIVE);
			userservice.save(user);
			 mailService.sendMail(user.getEmail(), "Elibrary : Your password was changed",
			 "Please verify your email address for Elibray System.\n"
			 + "Your new password is " + user.getPassword());
			resJson.put("message", "success");
			resJson.put("status", true);
		}
		return resJson;
	}

	@RequestMapping(value = "goResetPasswordByAdmin", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	@CrossOrigin(origins = "*")
	public JSONObject goResetPasswordByAdmin(@RequestBody JSONObject reqJson, @RequestHeader("token") String token) throws Exception {
		JSONObject resJson = new JSONObject();
		byte[] base64DecryptedPassword = Base64.getDecoder().decode(reqJson.get("password").toString());
		String newpwd = AES.decrypt(base64DecryptedPassword, secretKey);
		String email = reqJson.get("email").toString();
		String code = reqJson.get("code").toString();
		String loginUserid = userservice.sessionActive(token);
		if (!loginUserid.equals("") || loginUserid.equals("000")) {
		} else {
			resJson.put("status", false);
			resJson.put("message", "Session Fail");
			return resJson;
		}
		resJson = checkPasswordPolicyPattern(newpwd);
		boolean status = Boolean.parseBoolean(resJson.get("status").toString());
		if(!status) {
			return resJson;
		}
		User user = userservice.selectUserbyVerCode(loginUserid, code, email);
		if (user != null) {

			user.setPassword(newpwd);
			user.setSessionStatus(EntityStatus.ACTIVE);
			userservice.save(user);
			 mailService.sendMail(user.getEmail(), "Elibrary : Your password was changed",
			 "Please verify your email address for Elibray System.\n"
			 + "Your new password is " + user.getPassword());
			resJson.put("message", "success");
			resJson.put("status", true);
		}
		return resJson;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "signout", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public void signout(@RequestBody String userid, @RequestHeader("token") String token) throws ServiceUnavailableException {
		Session session = userservice.sessionActiveById(token, userid);
		if (session != null) {
			session.setEntityStatus(EntityStatus.INACTIVE);
			session.setEndDate(dateTimeFormat());
			userservice.save(session);
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "encrypt", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject encrypt(@RequestBody JSONObject reqJson) throws Exception {
		JSONObject resultJson = new JSONObject();
		String stringToEncrypt = reqJson.get("toEncrypt").toString();
		byte[] encryptedMsg = AES.encrypt(stringToEncrypt, secretKey);
		String base64EncryptedPassword = Base64.getEncoder().encodeToString(encryptedMsg);

		resultJson.put("Status", "1");
		resultJson.put("Encrypted string", base64EncryptedPassword);
		return resultJson;

	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "decrypt", method = RequestMethod.POST)
	@ResponseBody
	@JsonView(Views.Summary.class)
	public JSONObject decrypt(@RequestBody JSONObject reqJson) throws Exception {
		JSONObject resultJson = new JSONObject();
		String stringToDecrypt = reqJson.get("toDecrypt").toString();
		byte[] base64DecryptedPassword = Base64.getDecoder().decode(reqJson.get("toDecrypt").toString());
		String decryptedPassword = AES.decrypt(base64DecryptedPassword, secretKey);
		resultJson.put("Status", "1");
		resultJson.put("Decrypted string", decryptedPassword);
		return resultJson;

	}
		@ResponseBody
		@CrossOrigin(origins = "*")
		@JsonView(Views.Summary.class)
		@RequestMapping(value = "goLoginByWebsite", method = RequestMethod.POST)
		public JSONObject goLoginByWebsite(@RequestBody JSONObject reqJson) throws Exception {
			JSONObject resJson = new JSONObject();
			String message = "";
			String email = reqJson.get("email").toString();
			byte[] base64DecryptedPassword = Base64.getDecoder().decode(reqJson.get("password").toString());
			String password = AES.decrypt(base64DecryptedPassword, secretKey);

			message = this.goValidation(email, password);
			if (!message.equals("")) {
				resJson.put("message", message);
				resJson.put("status", false);
				return resJson;
			}
			User user = userservice.getLoginByWebsite(email, password);// diff
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
			resJson.put("password", password);
			return resJson;
		}

}
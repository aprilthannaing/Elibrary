package com.elibrary.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.elibrary.controller.AbstractController;
import com.elibrary.dao.SessionDao;
import com.elibrary.dao.UserDao;
import com.elibrary.dao.impl.UserDaoImpl;
import com.elibrary.entity.EntityStatus;
import com.elibrary.entity.Request;
import com.elibrary.entity.Session;
import com.elibrary.entity.User;
import com.elibrary.entity.UserRole;
import com.elibrary.service.UserService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("userService")
public class UserServiceImpl extends AbstractController implements UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private SessionDao sessionDao;

	public static Logger logger = Logger.getLogger(UserDaoImpl.class);

	@Override
	public void save(User user) throws ServiceUnavailableException {
		try {
			if (user.isIdRequired(user.getId()))
				user.setBoId("USR" + countId());
			userDao.saveOrUpdate(user);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());

		}
	}

	public long countId() {
		String query = "select max(id) from User";
		List<Long> idList = userDao.findLongByQueryString(query);
		if (idList.get(0) == null)
			return 1;
		return idList.get(0) + 1;
	}

	@Override
	public List<User> selectUser(Request req) {
		List<User> response = new ArrayList<User>();
		String whereclause = "";
		int l_startRecord = (req.getCurrentpage() - 1) * req.getPageSize();
		int l_endRecord = l_startRecord + req.getPageSize();
		if (!req.getSearchText().trim().equals("")) {
			whereclause = " and (" + " name like '%" + req.getSearchText() + "%' or email like '%" + req.getSearchText() + "%' " + "or phoneNo like '%" + req.getSearchText() + "%' or role like '%" + req.getSearchText() + "%' " + "or type like '%" + req.getSearchText() + "%' or entityStatus like '%" + req.getSearchText() + "%' )";
		}
		if (!req.getText1().trim().equals("")) {
			whereclause += " and hluttawboId=" + req.getText1();
		}
		if (!req.getText2().trim().equals("")) {
			whereclause += " and departmentboId=" + req.getText2();
		}
		if (!req.getText3().trim().equals("")) {
			whereclause += " and positionboId=" + req.getText3();
		}
//		if(req.getFromDate().equals("") && req.getToDate().equals("")) {
//			
//		}
		if (!req.getFromDate().trim().equals("")) {
			whereclause += " and modifiedDate >='" + req.getFromDate() + "'";
		}
		if (!req.getToDate().trim().equals("")) {
			whereclause += " and modifiedDate <='" + req.getToDate() + "'";
		}
		String query = "from User where entityStatus<>'DELETED' " + whereclause + " order by id desc";
//		String query = "select RowConstrainedResult FROM ( SELECT (@row_number:=@row_number + 1) AS row_num,user" 
//				+ " FROM user user, (SELECT @row_number:=0) AS temp where entityStatus<>'DELETED' " + whereclause 
//				+ " ORDER BY id desc) AS RowConstrainedResult"
//				+ " WHERE ( row_num > "+ l_startRecord +" and row_num <= "+ l_endRecord +" )";
		// String query = "select t from (select @rownum:=@rownum+1 rownumber, user from
		// user user cross join (SELECT user.rownum:=0) r order by id desc)As t" +
		// "WHERE ( rownumber > "+l_startRecord+" and rownumber <= "+l_endRecord+" )";
		List<User> userList = userDao.maxResultbyQuery(query, l_endRecord, l_startRecord);
		for (User row : userList) {
			row.setDeptType(row.getDepartment().getId());
			row.setDeptName(row.getDepartment().getName());
			row.setPositionType(row.getPosition().getId());
			row.setPositionName(row.getPosition().getName());
			row.setHlutawType(row.getHluttaw().getId());
			row.setHlutawName(row.getHluttaw().getName());
			row.setStatus(row.getEntityStatus().name());
			if (row.getConstituency() != null) {
				row.setConstituencyName(row.getConstituency().getName());
				row.setConstituencyType(row.getConstituency().getId());
			}
			response.add(row);
		}
		return response;
	}

	@Override
	public User selectUserByKey(String key) {
		List<User> response = new ArrayList<User>();
		String query = "from User where boid='" + key + "'";
		List<User> userList = userDao.byQuery(query);
		if (CollectionUtils.isEmpty(userList))
			return null;
		for (User row : userList) {
			row.setDeptType(row.getDepartment().getId());
			row.setDeptName(row.getDepartment().getName());
			row.setPositionType(row.getPosition().getId());
			row.setPositionName(row.getPosition().getName());
			row.setHlutawType(row.getHluttaw().getId());
			row.setHlutawName(row.getHluttaw().getName());
			row.setStatus(row.getEntityStatus().name());
			row.setRoleType(row.getRole().name());
			if (row.getConstituency() != null) {
				row.setConstituencyType(row.getConstituency().getId());
				row.setConstituencyName(row.getConstituency().getName());
			}
//				else {
//				row.setConstituencyName(row.getConstituency().getName());
//			}

			response.add(row);
		}

		return response.get(0);
	}

	@Override
	public User selectUserbyId(String key) {
		String query = "Select user from User user where id=" + Long.parseLong(key);
		List<User> userList = userDao.byQuery(query);
		if (CollectionUtils.isEmpty(userList))
			return null;
		return userList.get(0);
	}

	@Override
	public User selectUserbyEmail(String email) {
		String query = "Select user from User user where email='" + email + "' and entityStatus<>'" + EntityStatus.DELETED + "'";
		List<User> userList = userDao.byQuery(query);
		if (userList.size() > 0)
			return userList.get(0);
		return null;
	}

	@Override
	public User selectUserbyEmailAndBoId(String email, String boId) {
		String query = "Select user from User user where email='" + email + "' and entityStatus<>'" + EntityStatus.DELETED + "'" + " And boId<>'" + boId + "'";
		List<User> userList = userDao.byQuery(query);
		if (userList.size() > 0)
			return userList.get(0);
		return null;
	}

	@Override
	public User selectUserbyEmailActive(String email) {
		String query = "Select user from User user where email='" + email + "' and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<User> userList = userDao.byQuery(query);
		if (userList.size() > 0)
			return userList.get(0);
		return null;
	}

	@Override
	public User selectUserbyVerCode(String loginUserid, String verificationCode, String email) {
		String query = "Select user from User user where id=" + loginUserid + " And verificationCode='" + verificationCode + "'" + " And email='" + email + "' And  entityStatus='" + EntityStatus.ACTIVE + "'";
		List<User> userList = userDao.byQuery(query);
		if (userList.size() > 0)
			return userList.get(0);
		return null;
	}

	@Override
	public User findByBoId(String boId) {
		String query = "select user from User user where boId='" + boId + "' and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<User> users = userDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(users))
			return null;
		return users.get(0);
	}

	@Override
	public User getLoginByAdmin(String email, String password) {
		String query = "from User where email='" + email + "' And password='" + password + "' And entityStatus='" + EntityStatus.ACTIVE + "' And role <>'" + UserRole.User + "'";
		List<User> users = userDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(users))
			return null;
		return users.get(0);
	}

	@Override
	public User getLoginByWebsite(String email, String password) {
		String query = "from User where email='" + email + "' And password='" + password + "' And entityStatus='" + EntityStatus.ACTIVE + "'";
		List<User> users = userDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(users))
			return null;
		return users.get(0);
	}

	@Override
	public User getLogin(String email, String password) {
		String query = "from User where email='" + email + "' And password='" + password + "' And entityStatus='" + EntityStatus.ACTIVE + "'";
		List<User> users = userDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(users))
			return null;
		return users.get(0);
	}

	@Override
	public String save(Session session) {
		try {
			if (session.isIdRequired(session.getId()))
				session.setBoId(generateSession(countIdbySession()));
			if (sessionDao.checkSaveOrUpdate(session)) {
				return session.getBoId();
			}
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());

		}
		return "";
	}

	public long countIdbySession() {
		String query = "select max(id) from Session";
		List<Long> idList = userDao.findLongByQueryString(query);
		if (idList.get(0) == null)
			return 1;
		return idList.get(0) + 1;
	}

	@Override
	public List<User> getLibrarians() {
		String query = "From User user where role='" + UserRole.Librarian + "' and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<User> userList = userDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(userList))
			return null;
		return userList;
	}

	@Override
	public String sessionActive(String sessionId) {
		String query = "";
		query = "from Session";
		List<Session> sessionListAll = sessionDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(sessionListAll))
			return "000";
		query = "from Session where boId='" + sessionId + "' And entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Session> sessionList = sessionDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(sessionList))
			return "";
		return String.valueOf(sessionList.get(0).getUser().getId());
	}

	@Override
	public List<User> selectUserbyStatus(Request req) {
		List<User> response = new ArrayList<User>();
		String whereclause = "";
		if (!req.getFromDate().trim().equals("")) {
			whereclause += " and modifiedDate >='" + req.getFromDate() + "'";
		}
		if (!req.getToDate().trim().equals("")) {
			whereclause += " and modifiedDate <='" + req.getToDate() + "'";
		}
		String query = "from User where entityStatus='" + EntityStatus.NEW + "' " + whereclause + " order by id desc";
		List<User> userList = userDao.byQuery(query);
		for (User row : userList) {
			row.setDeptType(row.getDepartment().getId());
			row.setDeptName(row.getDepartment().getName());
			row.setPositionType(row.getPosition().getId());
			row.setPositionName(row.getPosition().getName());
			row.setHlutawType(row.getHluttaw().getId());
			row.setHlutawName(row.getHluttaw().getName());
			row.setStatus(row.getEntityStatus().name());
			// from user
			row.setFromUser(fromUserbyId(row.getFromUserId()));
			response.add(row);
		}
		return response;
	}

	public String fromUserbyId(String fromuserid) {
		String query = "from User where id='" + fromuserid + "'";
		List<User> userList = userDao.byQuery(query);
		if (CollectionUtils.isEmpty(userList))
			return "";
		return userList.get(0).getName();
	}

	public long checkSession() {
		String query = "select max(id) from Session";
		List<Long> idList = userDao.findLongByQueryString(query);
		if (idList.get(0) == null)
			return 1;
		return idList.get(0) + 1;
	}

	@Override
	public Session sessionActiveById(String sessionId, String userid) {
		String query = "from Session where boId='" + sessionId + "' And entityStatus='" + EntityStatus.ACTIVE + "'";// And userid=" + userid;
		List<Session> sessionList = sessionDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(sessionList))
			return null;
		return sessionList.get(0);
	}

}

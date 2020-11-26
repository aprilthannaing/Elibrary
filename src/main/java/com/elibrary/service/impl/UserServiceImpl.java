package com.elibrary.service.impl;

import java.util.ArrayList;
import java.util.List;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.elibrary.dao.UserDao;
import com.elibrary.dao.impl.UserDaoImpl;
import com.elibrary.entity.EntityStatus;
import com.elibrary.entity.Request;
import com.elibrary.controller.AbstractController;
import com.elibrary.dao.SessionDao;
import com.elibrary.entity.Session;
import com.elibrary.entity.User;
import com.elibrary.service.UserService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("userService")
public class UserServiceImpl extends AbstractController implements UserService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SessionDao sessionDao;
	
	public static Logger logger = Logger.getLogger(UserDaoImpl.class);

	public void save(User user) throws ServiceUnavailableException {
		try {
			if (user.isIdRequired(user.getId()))
				user.setBoId("USR" +countId());
			userDao.saveOrUpdate(user);
		}catch(com.mchange.rmi.ServiceUnavailableException e){
			logger.error("Error: "+ e.getMessage());
			
		}
	}
	
	public long countId() {
		String query = "select max(id) from User";
		List<Long> idList = userDao.findLongByQueryString(query);
		if(idList.get(0) == null)
			return 1;
		return idList.get(0) + 1;
	} 
	
	public List<User> selectUser(Request req) {
		List<User> response  = new ArrayList<User>();
		String whereclause = "";
		if(!req.getSearchText().equals("")) {
			whereclause = " and (" + " name like '%" + req.getSearchText() + "%' or email like '%" + req.getSearchText() + "%' "
					+ "or phoneNo like '%" + req.getSearchText() + "%' or role like '%" + req.getSearchText() + "%' "
					+ "or type like '%" + req.getSearchText() + "%' or entityStatus like '%" + req.getSearchText() + "%' )";
		}
		String query = "from User where 1=1 " + whereclause;
		List<User> userList = userDao.byQuery(query);
		for (User row : userList) {
			row.setDeptType(row.getDepartment().getId());
			row.setDeptName(row.getDepartment().getName());
			row.setPositionType(row.getPosition().getId());
			row.setPositionName(row.getPosition().getName());
			row.setHlutawType(row.getHluttaw().getId());
			row.setHlutawName(row.getHluttaw().getName());
			row.setStatus(row.getEntityStatus().name());
			response.add(row);
		}
		return response;
	}
	
	public User selectUserByKey(String key) {
		List<User> response  = new ArrayList<User>();
		String query = "from User where boid='"+ key +"'";
		List<User> userList = userDao.byQuery(query);
		if(CollectionUtils.isEmpty(userList))
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
			response.add(row);
		}
		
		return response.get(0);
	}

	public User findByBoId(String boId) {
		String query = "select user from User user where boId='" + boId + "'and entityStatus='"
				+ EntityStatus.ACTIVE + "'";
		List<User> users = userDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(users))
			return null;
		return users.get(0);
	}

	public User getLogin(String email,String password) {
		String query ="from User where email='"+ email +"' And password='"+ password +"' And entityStatus='" + EntityStatus.ACTIVE + "'";
	List<User> users = userDao.getEntitiesByQuery(query);
	if(CollectionUtils.isEmpty(users))
		return null;
	return users.get(0);
	}
	
	public String checkSession(User user) throws ServiceUnavailableException {
		Session session = new Session();
		String query = "from Session where userid=" + user.getId();
		List<Session> sessionList = sessionDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(sessionList))
			return "";
		session.setEntityStatus(EntityStatus.ACTIVE);
		session.setStartDate(dateFormat());
		session.setEndDate(dateFormat());
		session.setUser(user);
		return save(session);
	}
	
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
		if(idList.get(0) == null)
			return 1;
		return idList.get(0) + 1;
	} 
	
}

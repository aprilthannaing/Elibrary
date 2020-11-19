package com.elibrary.service.impl;

import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.elibrary.dao.UserDao;
import com.elibrary.dao.impl.UserDaoImpl;
import com.elibrary.entity.Department;
import com.elibrary.entity.User;
import com.elibrary.service.UserService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("userService")
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserDao userDao;
	
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
	
	public List<User> selectUser() {
		List<User> response  = new ArrayList<User>();
		String whereclause = "";
		String query = "from User";
		List<User> userList = userDao.byQuery(query);
		for (User row : userList) {
			row.setDeptType(row.getDepartment().getId());
			row.setDeptName(row.getDepartment().getName());
			row.setPositionType(row.getPosition().getId());
			row.setPositionName(row.getPosition().getName());
			row.setHlutawType(row.getHluttaw().getId());
			row.setHlutawName(row.getHluttaw().getName());
			response.add(row);
		}
		return response;
	}
	
	public User selectUserByKey(String key) {
		List<User> response  = new ArrayList<User>();
		String query = "from User where boid='"+ key +"'";
		List<User> userList = userDao.byQuery(query);
		for (User row : userList) {
			row.setDeptType(row.getDepartment().getId());
			row.setDeptName(row.getDepartment().getName());
			row.setPositionType(row.getPosition().getId());
			row.setPositionName(row.getPosition().getName());
			row.setHlutawType(row.getHluttaw().getId());
			row.setHlutawName(row.getHluttaw().getName());
			response.add(row);
		}
		
		return response.get(0);
	}

	@Override
	public User findUserById(String boId) throws ServiceUnavailableException {
		
		String query = "from User where boId='" + boId + "'";
		List<User> userList = userDao.getEntitiesByQuery(query);
		if(CollectionUtils.isEmpty(userList))
			return null;
		return userList.get(0);
	}

}

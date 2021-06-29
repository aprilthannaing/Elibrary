package com.elibrary.dao;

import java.util.List;

import com.elibrary.entity.Session;
import com.elibrary.entity.User;

public interface SessionDao extends AbstractDao<Session, String> {

	List<User> getEntities(String query);

}

package com.elibrary.service;

import java.sql.Connection;
import java.sql.SQLException;

public interface AbstractService {

	public Connection getConnection() throws SQLException, ClassNotFoundException;

}

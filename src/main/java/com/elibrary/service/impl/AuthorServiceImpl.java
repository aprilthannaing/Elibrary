package com.elibrary.service.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.elibrary.dao.AuthorDao;
import com.elibrary.dao.impl.AuthorDaoImpl;
import com.elibrary.entity.Author;
import com.elibrary.entity.AuthorType;
import com.elibrary.entity.EntityStatus;
import com.elibrary.service.AuthorService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("authorService")
public class AuthorServiceImpl implements AuthorService {

	@Autowired
	private AuthorDao authorDao;

	public static Logger logger = Logger.getLogger(AuthorDaoImpl.class);

	public void save(Author author) throws ServiceUnavailableException {
		try {
			if (author.isBoIdRequired(author.getBoId()))
				author.setBoId(getBoId());
			authorDao.saveOrUpdate(author);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}

	}

	private Long plus() {
		return countAuthor() + 10000;
	}

	public long countAuthor() {
		String query = "select count(*) from Author";
		return authorDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "AUTHOR" + plus();
	}

	public boolean isDuplicateProfile(String profilePicture) {
		String query = "select author from Author author where profilePicture='" + profilePicture.trim() + "'";
		List<Author> authors = authorDao.getEntitiesByQuery(query);
		return !CollectionUtils.isEmpty(authors);
	}

	public List<Author> getAll() {
		String query = "select author from Author author where entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Author> authors = authorDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(authors))
			return null;
		return authors;
	}

	public Author findByBoId(String boId) {
		String query = "select author from Author author where boId='" + boId + "'and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Author> authors = authorDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(authors))
			return null;
		return authors.get(0);
	}

	public List<Author> getAuthorListByCategory(long categoryId, AuthorType authorType) {
		String query = "select author from Author author where authorType='" + authorType + "' and author.id in (select ba.authorId from Book_Author ba where ba.bookId in(Select bc.id from Book_Category bc where bc.categoryId=" + categoryId + " ))";
		List<Author> authors = authorDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(authors))
			return new ArrayList<Author>();
		return authors;
	}

	public Author getAuthorListById(long authorId, AuthorType authorType) {
		String query = "select author from Author author where authorType='" + authorType + "' and author.id=" + authorId;
		List<Author> authors = authorDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(authors))
			return null;
		return authors.get(0);
	}

	public List<Long> getAuthorIdByBookCount(long categoryId) throws SQLException, ClassNotFoundException {
		List<Long> authorIds = new ArrayList<Long>();
		String name, pass, url;
		Connection con = null;
		Class.forName("com.mysql.jdbc.Driver");
		url = "jdbc:mysql://localhost:3306/elibrary";
		name = "root";
		pass = "root";
		con = DriverManager.getConnection(url, name, pass);
		String seeachStoredProc = "{call GET_BookCountByAuthor()}";
		CallableStatement myCs = con.prepareCall(seeachStoredProc);

		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next()) {
				if (Long.parseLong(rs.getString("CAT_ID")) == categoryId) {
					authorIds.add(Long.parseLong(rs.getString("AUTHOR_ID")));
				}
			}

			con.close();
		}
		return authorIds;
	}
	
	public List<Long> getAuthorIdByBookCount() throws SQLException, ClassNotFoundException {
		List<Long> authorIds = new ArrayList<Long>();
		String name, pass, url;
		Connection con = null;
		Class.forName("com.mysql.jdbc.Driver");
		url = "jdbc:mysql://localhost:3306/elibrary";
		name = "root";
		pass = "root";
		con = DriverManager.getConnection(url, name, pass);
		String seeachStoredProc = "{call GET_BookCountByAuthor()}";
		CallableStatement myCs = con.prepareCall(seeachStoredProc);

		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next()) {
					authorIds.add(Long.parseLong(rs.getString("AUTHOR_ID")));				
			}

			con.close();
		}
		return authorIds;
	}


}

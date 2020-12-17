package com.elibrary.service.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
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
public class AuthorServiceImpl extends AbstractServiceImpl implements AuthorService {

	@Autowired
	private AuthorDao authorDao;

	public static Logger logger = Logger.getLogger(AuthorDaoImpl.class);

	@Override
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

	@Override
	public long countAuthor() {
		String query = "select count(*) from Author";
		return authorDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "AUTHOR" + plus();
	}

	@Override
	public boolean isDuplicateProfile(String profilePicture) {
		String query = "select author from Author author where profilePicture='" + profilePicture.trim() + "'";
		List<Author> authors = authorDao.getEntitiesByQuery(query);
		return !CollectionUtils.isEmpty(authors);
	}

	@Override
	public List<Author> getAll() {
		String query = "select author from Author author where entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Author> authors = authorDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(authors))
			return null;
		return authors;
	}

	@Override
	public Author findByBoId(String boId) {
		String query = "select author from Author author where boId='" + boId + "'and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Author> authors = authorDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(authors))
			return null;
		return authors.get(0);
	}

	@Override
	public List<Author> getAuthorList(AuthorType authorType) {
		String query = "select author from Author author where authorType='" + authorType + "' and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Author> authors = authorDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(authors))
			return new ArrayList<Author>();
		return authors;
	}

	@Override
	public List<Author> getAuthorListByCategory(long categoryId, AuthorType authorType) {
		String query = "select author from Author author where authorType='" + authorType + "' and author.id in (select ba.authorId from Book_Author ba where ba.bookId in(Select bc.id from Book_Category bc where bc.categoryId=" + categoryId + " ))";
		List<Author> authors = authorDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(authors))
			return new ArrayList<Author>();
		return authors;
	}

	@Override
	public Author getAuthorListById(long authorId, AuthorType authorType) {
		String query = "select author from Author author where authorType='" + authorType + "' and author.id=" + authorId;
		List<Author> authors = authorDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(authors))
			return null;
		return authors.get(0);
	}

	@Override
	public List<Long> getAuthorIdByCategoryId(long categoryId) throws SQLException, ClassNotFoundException {
		List<Long> authorIds = new ArrayList<Long>();
		Connection con = getConnection();

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

	@Override
	public List<Long> getAuthorIdByCategoryIdAndName(long categoryId, String name) throws SQLException, ClassNotFoundException {
		List<Long> authorIds = new ArrayList<Long>();
		Connection con = getConnection();

		String seeachStoredProc = "{call GET_BookCountByAuthor()}";
		CallableStatement myCs = con.prepareCall(seeachStoredProc);

		boolean hasResults = myCs.execute();
		if (hasResults) {
			ResultSet rs = myCs.getResultSet();
			while (rs.next()) {
				if (categoryId <= 0) {
					if (rs.getString("AUTHOR_NAME").contains(name)) {
						Long id = Long.parseLong(rs.getString("AUTHOR_ID"));
						if (!authorIds.contains(id))
							authorIds.add(id);
					}
				} else if (Long.parseLong(rs.getString("CAT_ID")) == categoryId && rs.getString("AUTHOR_NAME").contains(name))
					authorIds.add(Long.parseLong(rs.getString("AUTHOR_ID")));
			}

			con.close();
		}
		return authorIds;
	}

	@Override
	public List<Long> getAuthorIdByBookCount() throws SQLException, ClassNotFoundException {
		List<Long> authorIds = new ArrayList<Long>();
		Connection con = getConnection();
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

	@Override
	public Author findById(Long Id) {
		String query = "select author from Author author where id=" + Id + " and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Author> authors = authorDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(authors))
			return null;
		return authors.get(0);
	}

}

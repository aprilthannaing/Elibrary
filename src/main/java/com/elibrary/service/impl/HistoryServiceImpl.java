package com.elibrary.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.elibrary.dao.HistoryDao;
import com.elibrary.dao.impl.CategoryDaoImpl;
import com.elibrary.entity.History;
import com.elibrary.service.HistoryService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("historyService")
public class HistoryServiceImpl implements HistoryService {

	@Autowired
	private HistoryDao historyDao;

	public static Logger logger = Logger.getLogger(CategoryDaoImpl.class);

	public void save(History history) throws ServiceUnavailableException {
		try {

			if (history.isBoIdRequired(history.getBoId()))
				history.setBoId(getBoId());

			historyDao.saveOrUpdate(history);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}
	}

	private Long plus() {
		return countHistory() + 10000;
	}

	public long countHistory() {
		String query = "select count(*) from History";
		return historyDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "HISTORY" + plus();
	}

	public List<History> getBookIdReadByUser(Long userId) {
		String query = "from History history where history.userId=" + userId + " order by history.Id desc";
		List<History> historyList = historyDao.getEntitiesByQuery(query, 20);
		if (CollectionUtils.isEmpty(historyList))
			return new ArrayList<History>();
		return historyList;

	}

}

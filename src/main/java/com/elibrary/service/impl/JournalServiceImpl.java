package com.elibrary.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.dao.BookDao;
import com.elibrary.dao.JournalDao;
import com.elibrary.dao.impl.JournalDaoImpl;
import com.elibrary.entity.Book;
import com.elibrary.entity.Journal;
import com.elibrary.service.JournalService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("journalService")
public class JournalServiceImpl implements JournalService{

	@Autowired
	private JournalDao journalDao;
	
	public String getProductName() {
	      return "Honey";
	   } 
	
	
	public static Logger logger = Logger.getLogger(JournalDaoImpl.class);
	
	public void save(Journal journal) throws ServiceUnavailableException {
		try {
			journalDao.saveOrUpdate(journal);
		}catch(com.mchange.rmi.ServiceUnavailableException e){
			logger.error("Error: "+ e.getMessage());
		}
		
	}

	@Override
	public void delete(Journal journal) throws ServiceUnavailableException {
		try {
			journalDao.delete(journal);
			
		}catch(com.mchange.rmi.ServiceUnavailableException e){
			logger.error("Error: "+ e.getMessage());
			
		}

	}

//	@Override
//	public List<Journal> findByDateRange(String startDate, String endDate) {
//		String query = "from Journal journal where journal.createdDate between " + startDate + "' and '" + endDate
//				+ "'";
//		List<Journal> journal = JournalDao.getEntitiesByQuery(query);
//		return journal;
//	
//	}

}

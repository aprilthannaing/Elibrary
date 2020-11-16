package com.elibrary.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.dao.MagazineDao;
import com.elibrary.dao.impl.MagazineDaoImpl;
import com.elibrary.entity.Magazine;
import com.elibrary.service.JournalService;
import com.elibrary.service.MagazineService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("magazineService")
public class MagazineServiceImpl implements MagazineService {

	@Autowired
	private MagazineDao magazineDao;
	
	@Autowired
	JournalServiceImpl journalService;
	
	 public String getProductName() {
	      return journalService.getProductName();
	 }

	private static Logger logger = Logger.getLogger(MagazineDaoImpl.class);

	public void save(Magazine magazine) throws ServiceUnavailableException {
		try {
			magazineDao.saveOrUpdate(magazine);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}
	}

//	@Override
//	public List<Magazine> findByDateRange(String startDate, String endDate) {
//		String query = "from Magazine magazine where magazine.createdDate between" + startDate +"'and'"+ endDate;
//		
//		List<Magazine> magazine = MagazineDao.getEntitesByQuery(query);
//		return null;
//	}

}

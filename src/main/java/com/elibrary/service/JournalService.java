package com.elibrary.service;

import com.elibrary.entity.Journal;
import com.mchange.rmi.ServiceUnavailableException;

import java.util.List;

public interface JournalService {
	
	public void save(Journal journal)throws ServiceUnavailableException;
	
	public void delete(Journal journal)throws ServiceUnavailableException;
	
//	public List<Journal> findByDateRange(String startDate, String endDate);

}

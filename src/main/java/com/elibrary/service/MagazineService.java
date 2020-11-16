package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.Magazine;
import com.mchange.rmi.ServiceUnavailableException;


public interface MagazineService {
	public void save(Magazine magazine)throws ServiceUnavailableException;
	
//	public List<Magazine> findByDateRange(String startDate,String endDate);

}

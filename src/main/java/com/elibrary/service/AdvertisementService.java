package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.Advertisement;
import com.mchange.rmi.ServiceUnavailableException;

public interface AdvertisementService {
	
	public void save(Advertisement advertisement) throws ServiceUnavailableException;

	public long countAdvertisement();
	
	public List<Advertisement> getall();
	
}

package com.elibrary.service;

import com.elibrary.entity.Category;
import com.mchange.rmi.ServiceUnavailableException;

public interface CategoryService {
	
	public void save(Category category) throws ServiceUnavailableException;

}

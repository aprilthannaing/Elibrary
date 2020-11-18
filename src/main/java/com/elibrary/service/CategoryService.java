package com.elibrary.service;

import com.elibrary.entity.Category;
import com.mchange.rmi.ServiceUnavailableException;

<<<<<<< Updated upstream
public interface CategoryService {
	
	public void save(Category category) throws ServiceUnavailableException;
=======
public interface CategoryService{
	public void save(Category category)throws ServiceUnavailableException;
	
	public Category findCategoryById(String boId)throws ServiceUnavailableException;
>>>>>>> Stashed changes

}

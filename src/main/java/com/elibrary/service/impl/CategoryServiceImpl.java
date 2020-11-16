package com.elibrary.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.dao.CategoryDao;
import com.elibrary.dao.impl.CategoryDaoImpl;
import com.elibrary.entity.Category;
import com.elibrary.service.CategoryService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("categoryService")
public class CategoryServiceImpl implements CategoryService{
	
	@Autowired
	private CategoryDao categoryDao;
	
	public static Logger logger = Logger.getLogger(CategoryDaoImpl.class);
	
	public void save(Category category) throws ServiceUnavailableException {
		try {
			categoryDao.saveOrUpdate(category);
		}catch(com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}
	}

}

package com.elibrary.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elibrary.dao.SubCategoryDao;
import com.elibrary.dao.impl.CategoryDaoImpl;
import com.elibrary.entity.SubCategory;
import com.elibrary.service.SubCategoryService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("categoryService")
public class SubCategoryServiceImpl implements SubCategoryService {

	@Autowired
	private SubCategoryDao subCategoryDao;

	public static Logger logger = Logger.getLogger(CategoryDaoImpl.class);

	public void save(SubCategory subCategory) throws ServiceUnavailableException {
		try {
			if (subCategory.isIdRequired(subCategory.getId()))
				subCategory.setId(getId());

			if (subCategory.isBoIdRequired(subCategory.getBoId()))
				subCategory.setBoId(getBoId());

			subCategoryDao.saveOrUpdate(subCategory);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}
	}

	private long getId() {
		return countCategory() + 1;
	}

	private Long plus() {
		return countCategory() + 10000;
	}

	public long countCategory() {
		String query = "select count(*) from SubCategory";
		return subCategoryDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "CATEGORY" + plus();
	}

}

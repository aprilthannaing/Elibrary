package com.elibrary.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.elibrary.dao.CategoryDao;
import com.elibrary.dao.impl.CategoryDaoImpl;
import com.elibrary.entity.Category;
import com.elibrary.entity.EntityStatus;
import com.elibrary.service.CategoryService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryDao categoryDao;

	public static Logger logger = Logger.getLogger(CategoryDaoImpl.class);

	public void save(Category category) throws ServiceUnavailableException {
		try {
			if (category.isIdRequired(category.getId()))
				category.setId(getId());

			if (category.isBoIdRequired(category.getBoId()))
				category.setBoId(getBoId());

			categoryDao.saveOrUpdate(category);
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
		String query = "select count(*) from Category";
		return categoryDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "CATEGORY" + plus();
	}

	public List<Category> getAll() {
		String query = "select category from Category category where entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Category> categories = categoryDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(categories))
			return null;
		return categories;
	}

	public Category findByBoId(String boId) {
		String query = "select category from Category category where boId='" + boId + "'and entityStatus='"
				+ EntityStatus.ACTIVE + "'";
		List<Category> categories = categoryDao.getEntitiesByQuery(query);
		if (!CollectionUtils.isEmpty(categories))
			return categories.get(0);
		return null;
	}

}

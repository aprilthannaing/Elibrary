package com.elibrary.service.impl;

import java.util.ArrayList;
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

	@Override
	public void save(Category category) throws ServiceUnavailableException {
		try {

			if (category.isBoIdRequired(category.getBoId()))
				category.setBoId(getBoId());

			categoryDao.saveOrUpdate(category);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}
	}

	private Long plus() {
		return countCategory() + 10000;
	}

	@Override
	public long countCategory() {
		String query = "select count(*) from Category";
		return categoryDao.findLongByQueryString(query).get(0);
	}

	@Override
	public long countActiveCategory() {
		String query = "select count(*) from Category where entityStatus='" + EntityStatus.ACTIVE + "'";
		return categoryDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "CATEGORY" + plus();
	}

	@Override
	public List<Category> getAll() {
		String query = "select category from Category category where entityStatus='" + EntityStatus.ACTIVE + "' order by priority";
		List<Category> categories = categoryDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(categories))
			return new ArrayList<Category>();
		return categories;
	}

	@Override
	public Category findByBoId(String boId) {
		String query = "select category from Category category where boId='" + boId + "'and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<Category> categories = categoryDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(categories))
			return null;
		return categories.get(0);
	}

	@Override
	public Long findBySubCategoryId(Long id) {
		String query = "select categorySub.categoryId from Category_SubCategory categorySub where categorySub.subCategoryId=" + id;
		List<Long> categoryList = categoryDao.findLongByQueryString(query);
		if (CollectionUtils.isEmpty(categoryList))
			return (long) 0;
		return categoryList.get(0);
	}

	@Override
	public Category findByCategoryId(Long id) {
		String query = "select category from Category category where id=" + id;
		List<Category> category = categoryDao.getEntitiesByQuery(query);
		if (!CollectionUtils.isEmpty(category))
			return category.get(0);
		return null;
	}

}

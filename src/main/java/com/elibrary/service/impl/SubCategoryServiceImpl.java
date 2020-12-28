package com.elibrary.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.elibrary.dao.SubCategoryDao;
import com.elibrary.dao.impl.CategoryDaoImpl;
import com.elibrary.entity.EntityStatus;
import com.elibrary.entity.SubCategory;
import com.elibrary.service.SubCategoryService;
import com.mchange.rmi.ServiceUnavailableException;

@Service("subCategoryService")
public class SubCategoryServiceImpl implements SubCategoryService {

	@Autowired
	private SubCategoryDao subCategoryDao;

	public static Logger logger = Logger.getLogger(CategoryDaoImpl.class);

	@Override
	public void save(SubCategory subCategory) throws ServiceUnavailableException {
		try {

			if (subCategory.isBoIdRequired(subCategory.getBoId())) {
				subCategory.setBoId(getBoId());
			}

			subCategoryDao.saveOrUpdate(subCategory);
		} catch (com.mchange.rmi.ServiceUnavailableException e) {
			logger.error("Error: " + e.getMessage());
		}
	}

	private Long plus() {
		return countSubCategory() + 10000;
	}

	@Override
	public long countSubCategory() {
		String query = "select count(*) from SubCategory";
		return subCategoryDao.findLongByQueryString(query).get(0);
	}

	@Override
	public long countActiveSubCategory() {
		String query = "select count(*) from SubCategory where entityStatus='" + EntityStatus.ACTIVE + "'";
		return subCategoryDao.findLongByQueryString(query).get(0);
	}

	public String getBoId() {
		return "SUBCATEGORY" + plus();
	}

	@Override
	public List<SubCategory> getAll() {
		String query = "select sub from SubCategory sub where entityStatus='" + EntityStatus.ACTIVE + "' order by priority";
		List<SubCategory> subCategories = subCategoryDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(subCategories))
			return null;
		return subCategories;
	}

	@Override
	public SubCategory findByBoId(String boId) {
		String query = "select sub from SubCategory sub where boId='" + boId + "' and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<SubCategory> subCategories = subCategoryDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(subCategories))
			return null;
		return subCategories.get(0);
	}

	@Override
	public List<SubCategory> byAlphabet(String alpherbat, long categoryId) {
		String query = "select sub from SubCategory sub where sub.myanmarName like '" + alpherbat + "%' and sub.id in (select cs.subCategoryId from Category_SubCategory cs where cs.categoryId=" + categoryId + ") and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<SubCategory> subCategories = subCategoryDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(subCategories))
			return new ArrayList<SubCategory>();
		return subCategories;
	}

	@Override
	public List<SubCategory> byCategoryAndDisplay(long categoryId) {
		String query = "select sub from SubCategory sub where sub.display='true' and sub.id in (select cs.subCategoryId from Category_SubCategory cs where cs.categoryId=" + categoryId + ") and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<SubCategory> subCategories = subCategoryDao.getEntitiesByQuery(query);
		if (CollectionUtils.isEmpty(subCategories))
			return new ArrayList<SubCategory>();
		return subCategories;
	}

	@Override
	public List<SubCategory> byCategory(long categoryId) {
		String query = "select sub from SubCategory sub where sub.id in (select cs.subCategoryId from Category_SubCategory cs where cs.categoryId=" + categoryId + ") and entityStatus='" + EntityStatus.ACTIVE + "'";
		List<SubCategory> subCategories = subCategoryDao.getEntitiesByQuery(query, 15);
		if (CollectionUtils.isEmpty(subCategories))
			return new ArrayList<SubCategory>();
		return subCategories;
	}

}

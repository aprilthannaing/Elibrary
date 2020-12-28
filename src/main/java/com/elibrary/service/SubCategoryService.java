package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.SubCategory;
import com.mchange.rmi.ServiceUnavailableException;

public interface SubCategoryService {

	public void save(SubCategory subcategory) throws ServiceUnavailableException;

	public List<SubCategory> getAll();

	public SubCategory findByBoId(String boId);

	public long countSubCategory();

	public long countActiveSubCategory();

	public List<SubCategory> byAlphabet(String alpherbat, long categoryId);

	public List<SubCategory> byCategoryAndDisplay(long categoryId);

	public List<SubCategory> byCategory(long categoryId);
}

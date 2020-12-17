package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.Category;
import com.mchange.rmi.ServiceUnavailableException;

public interface CategoryService {
	public void save(Category category) throws ServiceUnavailableException;

	public List<Category> getAll();

	public Category findByBoId(String boId);

	public long countCategory();

	public long countActiveCategory();

	public Category findByCategoryId(Long id);

	public Long findBySubCategoryId(Long id);

}

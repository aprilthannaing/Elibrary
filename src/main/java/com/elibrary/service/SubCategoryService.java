package com.elibrary.service;

import java.util.List;

import com.elibrary.entity.SubCategory;
import com.mchange.rmi.ServiceUnavailableException;

public interface SubCategoryService {

	public void save(SubCategory subcategory) throws ServiceUnavailableException;

	public List<SubCategory> getAll();

}

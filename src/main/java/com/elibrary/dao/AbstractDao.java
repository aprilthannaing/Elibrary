package com.elibrary.dao;

import java.io.Serializable;
import java.util.List;

import com.elibrary.entity.Category_Subcategory;
import com.mchange.rmi.ServiceUnavailableException;

public interface AbstractDao<E, I extends Serializable> {

	public List<Object> findByQueryString(String queryString);

	public List<String> findByQuery(String queryString);

	public List<E> byQuery(String queryString);

	public List<String> findByDateRange(String queryString, String start, String end);

	public List<Object> findByQueryString(String queryString, String dataInput);

	public List<Long> findLongByQueryString(String queryString);

	public List<Long> findLongByQueryString(String queryString, String dataInput);

	public List<Double> findDoubleByQueryString(String queryString);

	public List<Integer> findIntByQueryString(String queryString);

	public void saveOrUpdate(E e) throws ServiceUnavailableException;

	public boolean checkSaveOrUpdate(E e) throws ServiceUnavailableException;

	public List<E> getList(String queryString);

	public String validpwd(String queryString);

	public boolean saveUpdate(E e) throws ServiceUnavailableException;

	public List<E> findDatabyQueryString(String queryString, long dataInput);

	public int findCountByQueryString(String queryString);

	public long findLong(String queryString);

	public void delete(E e) throws ServiceUnavailableException;

	public double findDouble(String queryString);

	public List<E> getEntitiesByQuery(String queryString);

	public List<E> getEntitiesByQuery(String queryString, int count);
	
	public List<Long> findLongByQueryString(String queryString, int count);


}

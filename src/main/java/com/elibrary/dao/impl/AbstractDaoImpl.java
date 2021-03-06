package com.elibrary.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Transactional;

import com.elibrary.dao.AbstractDao;
import com.elibrary.entity.User;
import com.mchange.rmi.ServiceUnavailableException;

@Transactional
public abstract class AbstractDaoImpl<E, I extends Serializable> implements AbstractDao<E, I> {
	private Class<E> entityClass;

	@Autowired
	private EntityManager entityManager;

	private static Logger logger = Logger.getLogger(AbstractDaoImpl.class);

	protected AbstractDaoImpl(Class<E> entityClass) {
		this.entityClass = entityClass;
	}

	@Override
	public List<E> getEntitiesByQuery(String queryString) {
		List<E> entityList;
		Query query = entityManager.createQuery(queryString);
		entityList = query.getResultList();
		for (E entity : entityList) {
			Hibernate.initialize(entity);
		}
		return entityList;
	}

	public List<User> getEntities(String queryString) {
		List<User> entityList;
		Query query = entityManager.createQuery(queryString);
		entityList = query.getResultList();
		for (User entity : entityList) {
			Hibernate.initialize(entity);
		}
		return entityList;
	}

	@Override
	public List<E> getEntitiesByQuery(String queryString, int count) {
		List<E> entityList;
		Query query = entityManager.createQuery(queryString).setMaxResults(count);
		entityList = query.getResultList();
		for (E entity : entityList) {
			Hibernate.initialize(entity);
		}
		return entityList;
	}

	@Override
	public List<Object> findByQueryString(String queryString) {
		List<Object> entityList;
		Query query = entityManager.createQuery(queryString);
		entityList = query.getResultList();
		return entityList;
	}

	@Override
	public List<Long> findLongByQueryString(String queryString) {
		List<Long> entityList;
		Query query = entityManager.createQuery(queryString);
		entityList = query.getResultList();
		return entityList;
	}

	@Override
	public List<Long> findLongByQueryString(String queryString, int count) {
		List<Long> entityList;
		Query query = entityManager.createQuery(queryString).setMaxResults(count);
		entityList = query.getResultList();
		return entityList;
	}

	@Override
	public List<Integer> findIntByQueryString(String queryString) {
		List<Integer> entityList;
		Query query = entityManager.createQuery(queryString);
		entityList = query.getResultList();
		return entityList;
	}

	@Override
	public List<Double> findDoubleByQueryString(String queryString) {
		List<Double> entityList;
		Query query = entityManager.createQuery(queryString);
		entityList = query.getResultList();
		return entityList;
	}

	@Override
	public List<Long> findLongByQueryString(String queryString, String dataInput) {
		List<Long> entityList;
		Query query = entityManager.createQuery(queryString).setParameter("dataInput", dataInput);
		entityList = query.getResultList();
		return entityList;
	}

	@Override
	public List<Object> findByQueryString(String queryString, String dataInput) {
		List<Object> entityList;
		Query query = entityManager.createQuery(queryString).setParameter("dataInput", dataInput);
		entityList = query.getResultList();
		return entityList;
	}

	@Override
	public List<String> findByQuery(String queryString) {
		List<String> entityList;
		Query query = entityManager.createNativeQuery(queryString);
		entityList = query.getResultList();
		return entityList;
	}

	@Override
	public List<E> byQuery(String queryString) {
		List<E> entityList;
		Query query = entityManager.createQuery(queryString);
		entityList = query.getResultList();
		return entityList;
	}

	@Override
	public List<E> maxResultbyQuery(String queryString, int maxValue, int firstValue) {
		List<E> entityList;
		Query query = entityManager.createQuery(queryString);
		query.setMaxResults(maxValue);
		query.setFirstResult(firstValue);
		entityList = query.getResultList();
		return entityList;
	}

	public Session getSession() {
		return entityManager.unwrap(Session.class);
	}

	@Override
	public boolean checkSaveOrUpdate(E e) throws ServiceUnavailableException {
		try {
			Session session = getSession();
			session.saveOrUpdate(e);
			session.flush();
		} catch (CannotCreateTransactionException exception) {
			logger.error("Exception: " + exception);
			throw new ServiceUnavailableException();
		}
		return true;
	}

	@Override
	public List<E> getList(String queryString) {
		List<E> list = new ArrayList<E>();
		try {
			Query query = entityManager.createQuery(queryString);
			list = query.getResultList();
		} catch (NoResultException nre) {

		}
		return list;
	}

	@Override
	public void saveOrUpdate(E e) throws ServiceUnavailableException {
		try {
			Session session = getSession();
			session.saveOrUpdate(e);
			session.flush();
		} catch (CannotCreateTransactionException exception) {
			logger.error("Exception: " + exception);
			throw new ServiceUnavailableException();
		}
	}

	@Override
	public List<E> findDatabyQueryString(String queryString, long dataInput) {
		List<E> entityList;
		Query query = entityManager.createQuery(queryString).setParameter("dataInput", dataInput);
		entityList = query.getResultList();
		return entityList;
	}

	@Override
	public int findCountByQueryString(String queryString) {
		Query query = entityManager.createQuery(queryString);
		return query.getSingleResult() != null ? Integer.parseInt(query.getSingleResult().toString()) : 0;
	}

}

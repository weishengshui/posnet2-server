package com.chinarewards.posnet.ext.dao;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class WsBaseDao<T> {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject
	Provider<EntityManager> emf;

	/**
	 * Returns an instance of entity manager.
	 * 
	 * @return
	 */
	protected EntityManager getEm() {
		return emf.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chinarewards.hr.dao.Dao#insert(java.lang.Object)
	 */
	public T insert(T t) {
		getEm().persist(t);
		return t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chinarewards.hr.dao.Dao#update(java.lang.Object)
	 */
	public T update(T t) {
		T o = getEm().merge(t);
		return o;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chinarewards.hr.dao.Dao#delete(java.lang.Object)
	 */
	public void delete(T t) {
		getEm().remove(t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chinarewards.hr.dao.Dao#findById(java.lang.String)
	 */
	public T findById(Class<T> entityClass, String id) {
		return (T) getEm().find(entityClass, id);
	}

}

/*******************************************************************************
 * Copyright (c) 2006-2015, PayPal Pvt Ltd, All rights reserved
 * Project    : loadnperformanceApi
 * Package    : com.paypal.lnp.dao.mongo.impl
 * Class Name : MongoBaseDAO.java
 * Sub Project: loadnperformanceApi
 * Created on : Apr 9, 2015
 * Created by : gthattiyottu
 ******************************************************************************/
package com.ptaas.repository.mongo.impl;

import java.lang.reflect.ParameterizedType;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.WriteResult;
import com.ptaas.repository.mongo.BaseDAO;
import com.ptaas.repository.mongo.dto.BaseDTO;


// TODO: Auto-generated Javadoc
/**
 * The Class MongoBaseDAO.
 * 
 * @param <E>
 *            the element type
 */
public abstract class MongoBaseDAO<E extends BaseDTO> implements BaseDAO<E> {
	
	/** The collection name. */
	protected String collectionName = this.getClass().getSimpleName().toUpperCase();
	
	/** The mongo ops. */
	protected MongoOperations mongoOps;
	
	/** The dto type. */
	protected Class<? extends BaseDTO> dtoType;
	
	/**
	 * Instantiates a new mongo base dao.
	 * 
	 * @param mongoOps
	 *            the mongo ops
	 * @param collectionName
	 *            the collection name
	 */
	public MongoBaseDAO (MongoOperations mongoOps,String collectionName)
	{
		dtoType = (Class<BaseDTO>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		this.mongoOps=mongoOps;
		this.collectionName=collectionName;
	}

	@Override
	public E create(E p) {
		this.mongoOps.insert(p, collectionName);
		return p;
	}


	@Override
	public E readById(String id) {
		Query query = new Query(Criteria.where("_id").is(id));
		return (E) this.mongoOps.findOne(query, dtoType, collectionName);
	}


	@Override
	public E update(E p) {
		this.mongoOps.save(p, collectionName);
		return p;
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where("_id").is(id));
		WriteResult result = this.mongoOps.remove(query, dtoType,
				collectionName);
		return result.getN();
	}

	/**
	 * Gets the collection name.
	 * 
	 * @return the collection name
	 */
	public String getCollectionName() {
		return collectionName;
	}

	/**
	 * Gets the mongo ops.
	 * 
	 * @return the mongo ops
	 */
	public MongoOperations getMongoOps() {
		return mongoOps;
	}

	/**
	 * Gets the dto type.
	 * 
	 * @return the dto type
	 */
	public Class<? extends BaseDTO> getDtoType() {
		return dtoType;
	}
	

}

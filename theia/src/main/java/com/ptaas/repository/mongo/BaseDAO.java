/*******************************************************************************
 * Copyright (c) 2006-2015, PayPal Pvt Ltd, All rights reserved
 * Project    : loadnperformanceApi
 * Package    : com.paypal.lnp.dao.mongo
 * Class Name : BaseDAO.java
 * Sub Project: loadnperformanceApi
 * Created on : Apr 9, 2015
 * Created by : gthattiyottu
 ******************************************************************************/
package com.ptaas.repository.mongo;

import org.springframework.transaction.annotation.Transactional;

import com.ptaas.repository.mongo.dto.BaseDTO;


// TODO: Auto-generated Javadoc
/**
 * The Interface BaseDAO.
 * 
 * @param <E>
 *            the element type
 */
@Transactional
public interface BaseDAO <E extends BaseDTO> {
	
	/**
	 * Creates the.
	 * 
	 * @param p
	 *            the p
	 */
	public E create(E p);
    
    /**
	 * Read by id.
	 * 
	 * @param id
	 *            the id
	 * @return the e
	 */
    public E readById(String id);
     
    /**
	 * Update.
	 * 
	 * @param p
	 *            the p
	 */
    public E update(E p);
     
    /**
	 * Delete by id.
	 * 
	 * @param id
	 *            the id
	 * @return the int
	 */
    public int deleteById(String id);
   
}

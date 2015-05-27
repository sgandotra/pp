/*******************************************************************************
 * Copyright (c) 2006-2015, PayPal Pvt Ltd, All rights reserved
 * Project    : loadnperformanceApi
 * Package    : com.paypal.lnp.dto.mongo
 * Class Name : UserStatisticsDTO.java
 * Sub Project: loadnperformanceApi
 * Created on : Apr 9, 2015
 * Created by : gthattiyottu
 ******************************************************************************/
package com.ptaas.repository.mongo.dto;

import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class UserStatisticsDTO.
 */
public class StatisticsDTO extends BaseDTO {
	
	/** The created on. */
	Date createdOn=new Date();
	
	/** The last accessed on. */
	Date lastAccessedOn=new Date();
	
	/** The no of times accessed. */
	int noOfTimesAccessed=0;
	
	/**
	 * Gets the created on.
	 * 
	 * @return the created on
	 */
	public Date getCreatedOn() {
		return createdOn;
	}
	
	/**
	 * Sets the created on.
	 * 
	 * @param createdOn
	 *            the new created on
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
	/**
	 * Gets the last accessed on.
	 * 
	 * @return the last accessed on
	 */
	public Date getLastAccessedOn() {
		return lastAccessedOn;
	}
	
	/**
	 * Sets the last accessed on.
	 * 
	 * @param lastAccessedOn
	 *            the new last accessed on
	 */
	public void setLastAccessedOn(Date lastAccessedOn) {
		this.lastAccessedOn = lastAccessedOn;
	}
	
	/**
	 * Gets the no of times accessed.
	 * 
	 * @return the no of times accessed
	 */
	public int getNoOfTimesAccessed() {
		return noOfTimesAccessed;
	}
	
	/**
	 * Sets the no of times accessed.
	 * 
	 * @param noOfTimesAccessed
	 *            the new no of times accessed
	 */
	public void setNoOfTimesAccessed(int noOfTimesAccessed) {
		this.noOfTimesAccessed = noOfTimesAccessed;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserStatisticsDTO [createdOn=" + createdOn
				+ ", lastAccessedOn=" + lastAccessedOn + ", noOfTimesAccessed="
				+ noOfTimesAccessed + "]";
	}
	
}

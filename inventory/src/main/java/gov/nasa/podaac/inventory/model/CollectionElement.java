//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author clwong
 * $Id: CollectionElement.java 4499 2010-01-27 01:19:19Z gangl $
 *
 */
@SuppressWarnings("serial")
public class CollectionElement implements Serializable {
	
	private Integer collectionId;
	private String characterValue;
	private Date datetimeValue;
	private Long datetimeValueLong;
	private Integer integerValue;
	private Float realValue;
	private CollectionElementDD collectionElementDD;
	/**
	 * @return the collectionId
	 */
	public Integer getCollectionId() {
		return collectionId;
	}
	/**
	 * @param collectionId the collectionId to set
	 */
	public void setCollectionId(Integer collectionId) {
		this.collectionId = collectionId;
	}
	/**
	 * @return the characterValue
	 */
	public String getCharacterValue() {
		return characterValue;
	}
	/**
	 * @param characterValue the characterValue to set
	 */
	public void setCharacterValue(String characterValue) {
		this.characterValue = characterValue;
	}
	/**
	 * @return the datetimeValue
	 */
	public Date getDatetimeValue() {
		return datetimeValue;
	}
	/**
	 * @param datetimeValue the datetimeValue to set
	 */
	public void setDatetimeValue(Date datetimeValue) {
		this.datetimeValue = datetimeValue;
		this.datetimeValueLong = new Long(datetimeValue.getTime());
	}
	/**
	 * @return the datetimeValue
	 */
	public Long getDatetimeValueLong() {
		return datetimeValueLong;
	}
	/**
	 * @param datetimeValue the datetimeValue to set
	 */
	public void setDatetimeValueLong(Long datetimeValue) {
		if(datetimeValue == null)
			this.datetimeValue = null;
			else
		this.datetimeValue = new Date(datetimeValue);
		this.datetimeValueLong = datetimeValue;
	}
	
	/**
	 * @return the integerValue
	 */
	public Integer getIntegerValue() {
		return integerValue;
	}
	/**
	 * @param integerValue the integerValue to set
	 */
	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}
	/**
	 * @return the realValue
	 */
	public Float getRealValue() {
		return realValue;
	}
	/**
	 * @param realValue the realValue to set
	 */
	public void setRealValue(Float realValue) {
		this.realValue = realValue;
	}
	/**
	 * @return the collectionElementDD
	 */
	public CollectionElementDD getCollectionElementDD() {
		return collectionElementDD;
	}
	/**
	 * @param collectionElementDD the collectionElementDD to set
	 */
	public void setCollectionElementDD(CollectionElementDD collectionElementDD) {
		this.collectionElementDD = collectionElementDD;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((collectionElementDD == null) ? 0 : collectionElementDD
						.hashCode());
		result = prime * result
				+ ((collectionId == null) ? 0 : collectionId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CollectionElement other = (CollectionElement) obj;
		if (collectionElementDD == null) {
			if (other.collectionElementDD != null)
				return false;
		} else if (!collectionElementDD.equals(other.collectionElementDD))
			return false;
		if (collectionId == null) {
			if (other.collectionId != null)
				return false;
		} else if (!collectionId.equals(other.collectionId))
			return false;
		return true;
	}
	
}

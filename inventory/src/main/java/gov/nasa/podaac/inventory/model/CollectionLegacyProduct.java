//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author clwong
 * $Id: CollectionElement.java 2014 2008-09-29 15:40:32Z clwong $
 *
 */
@SuppressWarnings("serial")
public class CollectionLegacyProduct implements Serializable {
	
	private Integer collectionId;
	private Integer legacyProductId;
	
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
	 * @return the integerValue
	 */
	public Integer getLegacyProductId() {
		return legacyProductId;
	}
	/**
	 * @param integerValue the integerValue to set
	 */
	public void setLegacyProductId(Integer legacyProductId) {
		this.legacyProductId = legacyProductId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		final CollectionLegacyProduct other = (CollectionLegacyProduct) obj;
		if (collectionId == null) {
			if (other.collectionId != null)
				return false;
		} else if (!collectionId.equals(other.collectionId))
			return false;
		else if(legacyProductId.equals(other.legacyProductId))
			return false;
		return true;
	}
	
}

//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.inventory.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author clwong
 * @version
 * $Id: CollectionProduct.java 4499 2010-01-27 01:19:19Z gangl $
 */
@SuppressWarnings("serial")
public class CollectionProduct implements Serializable {
	private Integer collectionId;
	private String productId;
	private Character visibleFlag;
	private Date echoSubmitDate;
	private Date gcmdSubmitDate;
	private Long echoSubmitDateLong;
	private Long gcmdSubmitDateLong;
	
	
	private Collection collection;
	
	public Integer getCollectionId() {
		return collectionId;
	}
	public void setCollectionId(Integer collectionId) {
		this.collectionId = collectionId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public Character getVisibleFlag() {
		return visibleFlag;
	}
	public void setVisibleFlag(Character visibleFlag) {
		this.visibleFlag = visibleFlag;
	}
	public Date getEchoSubmitDate() {
		return echoSubmitDate;
	}
	public void setEchoSubmitDate(Date echoSubmitDate) {
		this.echoSubmitDate = echoSubmitDate;
		this.echoSubmitDateLong = new Long(echoSubmitDate.getTime());
	}
	public Date getGcmdSubmitDate() {
		return gcmdSubmitDate;
	}
	public void setGcmdSubmitDate(Date gcmdSubmitDate) {
		this.gcmdSubmitDate = gcmdSubmitDate;
		this.gcmdSubmitDateLong = new Long(gcmdSubmitDate.getTime());
	}
	
	public Long getEchoSubmitDateLong() {
		return echoSubmitDateLong;
	}
	public void setEchoSubmitDateLong(Long echoSubmitDate) {
		if(echoSubmitDate == null)
			this.echoSubmitDate =null;
		else
			this.echoSubmitDate = new Date(echoSubmitDate);
		this.echoSubmitDateLong = echoSubmitDate;
	}
	public Long getGcmdSubmitDateLong() {
		return gcmdSubmitDateLong;
	}
	public void setGcmdSubmitDateLong(Long gcmdSubmitDate) {
		if(gcmdSubmitDate == null)
			this.gcmdSubmitDate = null;
		else
			this.gcmdSubmitDate = new Date(gcmdSubmitDate);
		this.gcmdSubmitDateLong = gcmdSubmitDate;
	}
	
	
	
	public Collection getCollection() {
		return collection;
	}
	public void setCollection(Collection collection) {
		this.collection = collection;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((collectionId == null) ? 0 : collectionId.hashCode());
		result = prime * result
				+ ((productId == null) ? 0 : productId.hashCode());
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
		final CollectionProduct other = (CollectionProduct) obj;
		if (collectionId == null) {
			if (other.collectionId != null)
				return false;
		} else if (!collectionId.equals(other.collectionId))
			return false;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		return true;
	}
}

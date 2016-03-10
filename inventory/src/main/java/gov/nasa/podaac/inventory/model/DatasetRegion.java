//Copyright 2007, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;

/**
 * @author clwong
 * @version Sep 7, 2007
 * $Id: DatasetParameter.java 2014 2008-09-29 15:40:32Z clwong $
 */
@SuppressWarnings("serial")
public class DatasetRegion implements Serializable {
	
	private Integer datasetId;
	private String region;
	private String regionDetail;
	
	
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	
	public String getRegionDetail() {
		return regionDetail;
	}
	public void setRegionDetail(String regionDetail) {
		this.regionDetail = regionDetail;
	}
	/**
	 * @return the datasetId
	 */
	public Integer getDatasetId() {
		return datasetId;
	}
	/**
	 * @param datasetId the datasetId to set
	 */
	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((region == null) ? 0 : region.hashCode());
		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime * result + ((regionDetail == null) ? 0 : regionDetail.hashCode());
		
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
		final DatasetRegion other = (DatasetRegion) obj;
		if (region == null) {
			if (other.region != null)
				return false;
		} else if (!region.equals(other.region))
			return false;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		if (regionDetail == null) {
			if (other.regionDetail != null)
				return false;
		} else if (!regionDetail.equals(other.regionDetail))
			return false;
		return true;
	}
}

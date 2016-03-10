// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;

/**
 * @author clwong
 * @version Sep 7, 2007
 * $Id: DatasetResource.java 2014 2008-09-29 15:40:32Z clwong $
 */
@SuppressWarnings("serial")
public class DatasetResource implements Serializable {

	private Integer datasetId;
	private String resourceName;
	private String resourceType;
	private String resourcePath;
	private String resourceDescription;
	public Integer getDatasetId() {
		return datasetId;
	}
	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public String getResourcePath() {
		return resourcePath;
	}
	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	public String getResourceDescription() {
		return resourceDescription;
	}
	public void setResourceDescription(String resourceDescription) {
		this.resourceDescription = resourceDescription;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime
				* result
				+ ((resourceDescription == null) ? 0 : resourceDescription
						.hashCode());
		result = prime * result
				+ ((resourceName == null) ? 0 : resourceName.hashCode());
		result = prime * result
				+ ((resourcePath == null) ? 0 : resourcePath.hashCode());
		result = prime * result
				+ ((resourceType == null) ? 0 : resourceType.hashCode());
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
		final DatasetResource other = (DatasetResource) obj;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		if (resourceDescription == null) {
			if (other.resourceDescription != null)
				return false;
		} else if (!resourceDescription.equals(other.resourceDescription))
			return false;
		if (resourceName == null) {
			if (other.resourceName != null)
				return false;
		} else if (!resourceName.equals(other.resourceName))
			return false;
		if (resourcePath == null) {
			if (other.resourcePath != null)
				return false;
		} else if (!resourcePath.equals(other.resourcePath))
			return false;
		if (resourceType == null) {
			if (other.resourceType != null)
				return false;
		} else if (!resourceType.equals(other.resourceType))
			return false;
		return true;
	}

}

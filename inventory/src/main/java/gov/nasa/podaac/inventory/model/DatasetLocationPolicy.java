// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;

/**
 * @author clwong
 * @version $Id: DatasetLocationPolicy.java 1132 2008-05-14 20:40:14Z clwong $
 */
@SuppressWarnings("serial")
public class DatasetLocationPolicy implements Serializable {

	private Integer datasetId;
	private String type;
	private String basePath;
	
	public Integer getDatasetId() {
		return datasetId;
	}
	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBasePath() {
		return basePath;
	}
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((basePath == null) ? 0 : basePath.hashCode());
		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		final DatasetLocationPolicy other = (DatasetLocationPolicy) obj;
		if (basePath == null) {
			if (other.basePath != null)
				return false;
		} else if (!basePath.equals(other.basePath))
			return false;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}

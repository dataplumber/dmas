//Copyright 2007, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author clwong
 * @version Sep 7, 2007
 * $Id: DatasetVersion.java 4499 2010-01-27 01:19:19Z gangl $
 */
@SuppressWarnings("serial")
public class DatasetVersion implements Serializable {

	public static class DatasetVersionPK implements Serializable{
		private Dataset dataset;
		private Integer versionId;

		public Dataset getDataset() {
			return dataset;
		}
		public void setDataset(Dataset dataset) {
			this.dataset = dataset;
		}
		public Integer getVersionId() {
			return versionId;
		}
		public void setVersionId(Integer versionId) {
			this.versionId = versionId;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((versionId == null) ? 0 : versionId.hashCode());
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
			final DatasetVersionPK other = (DatasetVersionPK) obj;
			if (versionId == null) {
				if (other.versionId != null)
					return false;
			} else if (!versionId.equals(other.versionId))
				return false;
			return true;
		}
	}
	
	private DatasetVersionPK datasetVersionPK = new DatasetVersionPK();
	private String version;
	private Date versionDate;
	private Long versionDateLong;
	private String description;
	
	public DatasetVersionPK getDatasetVersionPK() {
		return datasetVersionPK;
	}
	public void setDatasetVersionPK(DatasetVersionPK datasetVersionPK) {
		this.datasetVersionPK = datasetVersionPK;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Date getVersionDate() {
		return versionDate;
	}
	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
		this.versionDateLong = new Long(versionDate.getTime());
	}
	
	public Long getVersionDateLong() {
		return versionDateLong;
	}
	public void setVersionDateLong(Long versionDate) {
		if(versionDate == null)
			this.versionDate = null;
		else
			this.versionDate = new Date(versionDate);
		this.versionDateLong = versionDate;
	}
	
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((datasetVersionPK == null) ? 0 : datasetVersionPK.hashCode());
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
		final DatasetVersion other = (DatasetVersion) obj;
		if (datasetVersionPK == null) {
			if (other.datasetVersionPK != null)
				return false;
		} else if (!datasetVersionPK.equals(other.datasetVersionPK))
			return false;
		return true;
	}
}

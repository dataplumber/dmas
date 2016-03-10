// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;


import java.io.Serializable;
import java.util.Date;

/**
 * @author clwong
 * @version Jul 30, 2007
 * $Id: DatasetSoftware.java 4499 2010-01-27 01:19:19Z gangl $
 */
@SuppressWarnings("serial")
public class DatasetSoftware implements Serializable {
	
	private Integer datasetId;
	private String softwareName;
	private String softwareType;
	private String softwarePath;
	private Date softwareDate;
	private Long softwareDateLong ;
	private String softwareVersion;
	private String softwareLanguage;
	private String softwarePlatform;
	private String softwareDescription;
	public Integer getDatasetId() {
		return datasetId;
	}
	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}
	public String getSoftwareName() {
		return softwareName;
	}
	public void setSoftwareName(String softwareName) {
		this.softwareName = softwareName;
	}
	public String getSoftwareType() {
		return softwareType;
	}
	public void setSoftwareType(String softwareType) {
		this.softwareType = softwareType;
	}
	public String getSoftwarePath() {
		return softwarePath;
	}
	public void setSoftwarePath(String softwarePath) {
		this.softwarePath = softwarePath;
	}
	public Date getSoftwareDate() {
		return softwareDate;
	}
	public void setSoftwareDate(Date softwareDate) {
		this.softwareDate = softwareDate;
		this.softwareDateLong = new Long(softwareDate.getTime());
	}
	
	public Long getSoftwareDateLong() {
		return softwareDateLong;
	}
	public void setSoftwareDateLong(Long softwareDate) {
		if(softwareDate == null)
			this.softwareDate = null;
		else
			this.softwareDate = new Date(softwareDate);
		this.softwareDateLong = softwareDate;
	}
	
	public String getSoftwareVersion() {
		return softwareVersion;
	}
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}
	public String getSoftwareLanguage() {
		return softwareLanguage;
	}
	public void setSoftwareLanguage(String softwareLanguage) {
		this.softwareLanguage = softwareLanguage;
	}
	public String getSoftwarePlatform() {
		return softwarePlatform;
	}
	public void setSoftwarePlatform(String softwarePlatform) {
		this.softwarePlatform = softwarePlatform;
	}
	public String getSoftwareDescription() {
		return softwareDescription;
	}
	public void setSoftwareDescription(String softwareDescription) {
		this.softwareDescription = softwareDescription;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime * result
				+ ((softwareDate == null) ? 0 : softwareDate.hashCode());
		result = prime
				* result
				+ ((softwareDescription == null) ? 0 : softwareDescription
						.hashCode());
		result = prime
				* result
				+ ((softwareLanguage == null) ? 0 : softwareLanguage.hashCode());
		result = prime * result
				+ ((softwareName == null) ? 0 : softwareName.hashCode());
		result = prime * result
				+ ((softwarePath == null) ? 0 : softwarePath.hashCode());
		result = prime
				* result
				+ ((softwarePlatform == null) ? 0 : softwarePlatform.hashCode());
		result = prime * result
				+ ((softwareType == null) ? 0 : softwareType.hashCode());
		result = prime * result
				+ ((softwareVersion == null) ? 0 : softwareVersion.hashCode());
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
		final DatasetSoftware other = (DatasetSoftware) obj;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		if (softwareDate == null) {
			if (other.softwareDate != null)
				return false;
		} else if (!softwareDate.equals(other.softwareDate))
			return false;
		if (softwareDescription == null) {
			if (other.softwareDescription != null)
				return false;
		} else if (!softwareDescription.equals(other.softwareDescription))
			return false;
		if (softwareLanguage == null) {
			if (other.softwareLanguage != null)
				return false;
		} else if (!softwareLanguage.equals(other.softwareLanguage))
			return false;
		if (softwareName == null) {
			if (other.softwareName != null)
				return false;
		} else if (!softwareName.equals(other.softwareName))
			return false;
		if (softwarePath == null) {
			if (other.softwarePath != null)
				return false;
		} else if (!softwarePath.equals(other.softwarePath))
			return false;
		if (softwarePlatform == null) {
			if (other.softwarePlatform != null)
				return false;
		} else if (!softwarePlatform.equals(other.softwarePlatform))
			return false;
		if (softwareType == null) {
			if (other.softwareType != null)
				return false;
		} else if (!softwareType.equals(other.softwareType))
			return false;
		if (softwareVersion == null) {
			if (other.softwareVersion != null)
				return false;
		} else if (!softwareVersion.equals(other.softwareVersion))
			return false;
		return true;
	}

}

// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
/**
 * @author clwong
 * @version Sep 7, 2007
 * $Id: DatasetCitation.java 7940 2011-06-06 17:11:09Z gangl $
 */
public class DatasetCitation implements Serializable {
	
	private Integer datasetId;
	private String title;
	private String creator;
	private String version;
	private String publisher;
	private String seriesName;
	private Date releaseDate;
	private Long releaseDateLong;
	private String releasePlace;
	private String citationDetail;
	private String onlineResource;
	/**
	 * @return the citationDetail
	 */
	public String getCitationDetail() {
		return citationDetail;
	}
	/**
	 * @param citationDetail the citationDetail to set
	 */
	public void setCitationDetail(String citationDetail) {
		this.citationDetail = citationDetail;
	}
	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}
	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
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
	/**
	 * @return the onlineResource
	 */
	public String getOnlineResource() {
		return onlineResource;
	}
	/**
	 * @param onlineResource the onlineResource to set
	 */
	public void setOnlineResource(String onlineResource) {
		this.onlineResource = onlineResource;
	}
	/**
	 * @return the publisher
	 */
	public String getPublisher() {
		return publisher;
	}
	/**
	 * @param publisher the publisher to set
	 */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	/**
	 * @return the releaseDate
	 */
	public Date getReleaseDate() {
		return releaseDate;
	}
	/**
	 * @param releaseDate the releaseDate to set
	 */
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
		this.releaseDateLong = new Long(releaseDate.getTime());
	}
	
	public Long getReleaseDateLong() {
		return releaseDateLong;
	}
	/**
	 * @param releaseDate the releaseDate to set
	 */
	public void setReleaseDateLong(Long releaseDate) {
		if(releaseDate == null)
			this.releaseDate= null;
		else
			this.releaseDate = new Date(releaseDate);
		this.releaseDateLong = releaseDate;
	}
	
	
	/**
	 * @return the releasePlace
	 */
	public String getReleasePlace() {
		return releasePlace;
	}
	/**
	 * @param releasePlace the releasePlace to set
	 */
	public void setReleasePlace(String releasePlace) {
		this.releasePlace = releasePlace;
	}
	/**
	 * @return the seriesName
	 */
	public String getSeriesName() {
		return seriesName;
	}
	/**
	 * @param seriesName the seriesName to set
	 */
	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime * result
				+ ((onlineResource == null) ? 0 : onlineResource.hashCode());
		result = prime * result
				+ ((publisher == null) ? 0 : publisher.hashCode());
		result = prime * result
				+ ((releaseDate == null) ? 0 : releaseDate.hashCode());
		result = prime * result
				+ ((releasePlace == null) ? 0 : releasePlace.hashCode());
		result = prime * result
				+ ((seriesName == null) ? 0 : seriesName.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		final DatasetCitation other = (DatasetCitation) obj;
		if (creator == null) {
			if (other.creator != null)
				return false;
		} else if (!creator.equals(other.creator))
			return false;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		if (onlineResource == null) {
			if (other.onlineResource != null)
				return false;
		} else if (!onlineResource.equals(other.onlineResource))
			return false;
		if (publisher == null) {
			if (other.publisher != null)
				return false;
		} else if (!publisher.equals(other.publisher))
			return false;
		if (releaseDate == null) {
			if (other.releaseDate != null)
				return false;
		} else if (!releaseDate.equals(other.releaseDate))
			return false;
		if (releasePlace == null) {
			if (other.releasePlace != null)
				return false;
		} else if (!releasePlace.equals(other.releasePlace))
			return false;
		if (seriesName == null) {
			if (other.seriesName != null)
				return false;
		} else if (!seriesName.equals(other.seriesName))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
	
}

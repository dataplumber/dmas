//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;

/**
 * @author clwong
 * @version Sep 10, 2007
 * $Id: GranuleReference.java 8454 2011-09-29 19:14:55Z gangl $
 */
@SuppressWarnings("serial")
public class GranuleReference implements Serializable, Comparable {

	private Integer granuleId;
	private String path;
	private String type;
	private String status;
	private String description;
	/**
	 * @return the granuleId
	 */
	public Integer getGranuleId() {
		return granuleId;
	}
	/**
	 * @param granuleId the granuleId to set
	 */
	public void setGranuleId(Integer granuleId) {
		this.granuleId = granuleId;
	}
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((granuleId == null) ? 0 : granuleId.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		final GranuleReference other = (GranuleReference) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (granuleId == null) {
			if (other.granuleId != null)
				return false;
		} else if (!granuleId.equals(other.granuleId))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public String toString() {
		return granuleId + ":[" + status + "]" +
				type + ":" + path;
	}
	@Override
	public int compareTo(Object obj) {
		if (this == obj)
			return 0;
		if (obj == null)
			return -1;
		if (getClass() != obj.getClass())
			return -1;
		final GranuleReference other = (GranuleReference) obj;
		
		if(this.getGranuleId() > other.getGranuleId())
			return 1;
		else if(this.getGranuleId().equals(other.getGranuleId())){
			if(this.getType().compareTo(other.getType()) == 0)
				return (this.getPath().compareTo(other.getPath()));
			else 
				return this.getType().compareTo(other.getType()); 
		}
		else
			return -1;
	}

}

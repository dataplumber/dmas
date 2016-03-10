//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.archive.core;

import java.net.URI;
import java.util.Date;

/**
 * This class contains Archive Information Package
 *
 * @author clwong
 *
 * @version
 * $Id: AIP.java 2196 2008-10-30 17:37:45Z clwong $
 */
public class AIP {
	
	// inputs from serviceprofile
	private Integer granuleId;
	private URI destination;
	private URI staging;
	private String checksum;
	private String algorithm;
	private Long fileSize;
	private String type;
	
	// outputs stored into serviceprofile
	private Date archiveFileStartDate;
	private Date archiveFileEndDate;
	private Date archiveGranuleStartDate;
	private Date archiveGranuleEndDate;
	
	// outputs stored into database GranuleArchive
	private String status;
	private String note;

	public AIP() {}

	public AIP(Integer granuleId, URI destination, String checksum,
			String algorithm, Long fileSize, String type, String status) {
		super();
		this.granuleId = granuleId;
		this.destination = destination;
		this.checksum = checksum;
		this.algorithm = algorithm;
		this.fileSize = fileSize;
		this.type = type;
		this.status = status;
	}

	public AIP(Integer granuleId, URI destination, String type, String status) {
		super();
		this.granuleId = granuleId;
		this.destination = destination;
		this.checksum = "";
		this.algorithm = "";
		this.fileSize = -1L;
		this.type = type;
		this.status = status;
	}

	public Integer getGranuleId() {
		return granuleId;
	}

	public void setGranuleId(Integer granuleId) {
		this.granuleId = granuleId;
	}

	public URI getDestination() {
		return destination;
	}

	public void setDestination(URI destination) {
		this.destination = destination;
	}

	public URI getStaging() {
		return staging;
	}

	public void setStaging(URI staging) {
		this.staging = staging;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	//------------
	public String toString() {
		return new String("granuleId="+granuleId+
				destination+":size="+fileSize+":"+checksum+":algorithm="+algorithm);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((destination == null) ? 0 : destination.hashCode());
		result = prime * result
				+ ((granuleId == null) ? 0 : granuleId.hashCode());
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
		final AIP other = (AIP) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (granuleId == null) {
			if (other.granuleId != null)
				return false;
		} else if (!granuleId.equals(other.granuleId))
			return false;
		return true;
	}

	public Date getArchiveFileStartDate() {
		return archiveFileStartDate;
	}

	public void setArchiveFileStartDate(Date archiveFileStartDate) {
		this.archiveFileStartDate = archiveFileStartDate;
	}

	public Date getArchiveFileEndDate() {
		return archiveFileEndDate;
	}

	public void setArchiveFileEndDate(Date archiveFileEndDate) {
		this.archiveFileEndDate = archiveFileEndDate;
	}

	public Date getArchiveGranuleStartDate() {
		return archiveGranuleStartDate;
	}

	public void setArchiveGranuleStartDate(Date archiveGranuleStartDate) {
		this.archiveGranuleStartDate = archiveGranuleStartDate;
	}

	public Date getArchiveGranuleEndDate() {
		return archiveGranuleEndDate;
	}

	public void setArchiveGranuleEndDate(Date archiveGranuleEndDate) {
		this.archiveGranuleEndDate = archiveGranuleEndDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}

/**
 * 
 */
package gov.nasa.podaac.inventory.model;

import java.io.Serializable;

/**
 * @author clwong
 * @version Jul 30, 2007
 * $Id: GranuleArchive.java 4204 2009-11-10 00:02:00Z gangl $
 */
@SuppressWarnings("serial")
public class GranuleArchive implements Serializable {

	private Integer granuleId;
	private String type;
	private String name;
	private Long fileSize;
	private String checksum;
	private Character compressFlag;
	private String status;
	
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
	/**
	 * @return the name 
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param path the path to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the fileSize
	 */
	public Long getFileSize() {
		return fileSize;
	}
	/**
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	/**
	 * @return the checksum
	 */
	public String getChecksum() {
		return checksum;
	}
	/**
	 * @param checksum the checksum to set
	 */
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	/**
	 * @return the compressFlag
	 */
	public Character getCompressFlag() {
		return compressFlag;
	}
	/**
	 * @param compressFlag the compressFlag to set
	 */
	public void setCompressFlag(Character compressFlag) {
		this.compressFlag = compressFlag;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((checksum == null) ? 0 : checksum.hashCode());
		result = prime * result
				+ ((compressFlag == null) ? 0 : compressFlag.hashCode());
		result = prime * result
				+ ((fileSize == null) ? 0 : fileSize.hashCode());
		result = prime * result
				+ ((granuleId == null) ? 0 : granuleId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		final GranuleArchive other = (GranuleArchive) obj;
		if (checksum == null) {
			if (other.checksum != null)
				return false;
		} else if (!checksum.equals(other.checksum))
			return false;
		if (compressFlag == null) {
			if (other.compressFlag != null)
				return false;
		} else if (!compressFlag.equals(other.compressFlag))
			return false;
		if (fileSize == null) {
			if (other.fileSize != null)
				return false;
		} else if (!fileSize.equals(other.fileSize))
			return false;
		if (granuleId == null) {
			if (other.granuleId != null)
				return false;
		} else if (!granuleId.equals(other.granuleId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
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
				type + ":" + name + ":" + fileSize;
	}

}

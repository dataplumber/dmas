//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author clwong
 * @version Sep 10, 2007
 * $Id: GranuleMetaHistory.java 9505 2012-02-09 00:53:07Z gangl $
 */
@SuppressWarnings("serial")
public class GranuleMetaHistory implements Serializable {
	
	public static class GranuleMetaHistoryPK implements Serializable{
		private Granule granule;
		private Integer versionId;
		
		public Granule getGranule() {
			return granule;
		}
		public void setGranule(Granule granule) {
			this.granule = granule;
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
			final GranuleMetaHistoryPK other = (GranuleMetaHistoryPK) obj;
			if (versionId == null) {
				if (other.versionId != null)
					return false;
			} else if (!versionId.equals(other.versionId))
				return false;
			return true;
		}
	}
	
	private GranuleMetaHistoryPK metaHistoryPK;
	private Date creationDate;
	private Date lastRevisionDate;
	private String revisionHistory;
	private Date echoSubmitDate;
	private Long creationDateLong,lastRevisionDateLong,echoSubmitDateLong;
	
	
	//private Granule granule;
	
	public GranuleMetaHistoryPK getMetaHistoryPK() {
		if(metaHistoryPK != null)
			return metaHistoryPK;
		else{
			metaHistoryPK = new GranuleMetaHistoryPK();
			return metaHistoryPK;
		}
		
	}
	public void setMetaHistoryPK(GranuleMetaHistoryPK metaHistoryPK) {
		this.metaHistoryPK = metaHistoryPK;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		this.creationDateLong = new Long(creationDate.getTime());
	}
	public Date getLastRevisionDate() {
		return lastRevisionDate;
	}
	public void setLastRevisionDate(Date lastRevisionDate) {
		this.lastRevisionDate = lastRevisionDate;
		this.lastRevisionDateLong = new Long(lastRevisionDate.getTime());
	}
	public String getRevisionHistory() {
		return revisionHistory;
	}
	public void setRevisionHistory(String revisionHistory) {
		this.revisionHistory = revisionHistory;
	}
	public Date getEchoSubmitDate() {
		return echoSubmitDate;
	}
	public void setEchoSubmitDate(Date echoSubmitDate) {
		this.echoSubmitDate = echoSubmitDate;
		this.echoSubmitDateLong =  new Long(echoSubmitDate.getTime());
	}
	
	public Long getCreationDateLong() {
		return creationDateLong;
	}
	public void setCreationDateLong(Long creationDate) {
		if(creationDate == null)
			this.creationDate= null;
		else
		this.creationDate = new Date(creationDate);
		this.creationDateLong = creationDate;
	}
	public Long getLastRevisionDateLong() {
		return lastRevisionDateLong;
	}
	public void setLastRevisionDateLong(Long lastRevisionDate) {
		if(lastRevisionDate == null)
			this.lastRevisionDate= null;
		else
		this.lastRevisionDate = new Date(lastRevisionDate);
		this.lastRevisionDateLong = lastRevisionDate;
	}
	public Long getEchoSubmitDateLong() {
		return echoSubmitDateLong;
	}
	public void setEchoSubmitDateLong(Long echoSubmitDate) {
		if(echoSubmitDate == null)
			this.echoSubmitDate= null;
		else
			this.echoSubmitDate = new Date(echoSubmitDate);
		
			
		this.echoSubmitDateLong =  echoSubmitDate;
	}
	
	
	/*public Granule getGranule() {
		return granule;
	}
	public void setGranule(Granule granule) {
		this.granule = granule;
	}*/
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((metaHistoryPK == null) ? 0 : metaHistoryPK.hashCode());
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
		final GranuleMetaHistory other = (GranuleMetaHistory) obj;
		if (metaHistoryPK == null) {
			if (other.metaHistoryPK != null)
				return false;
		} else if (!metaHistoryPK.equals(other.metaHistoryPK))
			return false;
		return true;
	}
}

//Copyright 2007, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author clwong
 * @version Sep 7, 2007
 * $Id: DatasetMetaHistory.java 14546 2015-12-10 14:58:41Z nchung $
 */
@SuppressWarnings("serial")
public class DatasetMetaHistory implements Serializable {
	
	public static class DatasetMetaHistoryPK implements Serializable{
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
					+ ((dataset == null) ? 0 : dataset.hashCode());
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
			final DatasetMetaHistoryPK other = (DatasetMetaHistoryPK) obj;
			if (dataset == null) {
				if (other.dataset != null)
					return false;
			} else if (!dataset.equals(other.dataset))
				return false;
			if (versionId == null) {
				if (other.versionId != null)
					return false;
			} else if (!versionId.equals(other.versionId))
				return false;
			return true;
		}
	}
	private DatasetMetaHistoryPK metaHistoryPK = new DatasetMetaHistoryPK();
	private Date creationDate;
	private Date lastRevisionDate;

	private Date echoSubmitDate;
	private Long echoSubmitDateLong;
	
	private Long creationDateLong, lastRevisionDateLong;
	
	private String revisionHistory;

	public Date getEchoSubmitDate() {
		return echoSubmitDate;
	}
	public void setEchoSubmitDate(Date echoSubmitDate) {
		this.echoSubmitDate = echoSubmitDate;
		this.echoSubmitDateLong = new Long(echoSubmitDate.getTime());
	}
	
	public Long getEchoSubmitDateLong() {
		return echoSubmitDateLong;
	}
	public void setEchoSubmitDateLong(Long echoSubmitDate) {
		if(echoSubmitDate == null)
			this.echoSubmitDate =null;
		else
			this.echoSubmitDate = new Date(echoSubmitDate);
		this.echoSubmitDateLong = echoSubmitDate;
	}

	
	public DatasetMetaHistoryPK getMetaHistoryPK() {
		return metaHistoryPK;
	}
	public void setMetaHistoryPK(DatasetMetaHistoryPK metaHistoryPK) {
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
	
	
	public Long getCreationDateLong() {
		return creationDateLong;
	}
	public void setCreationDateLong(Long creationDate) {
		if(creationDate == null)
			this.creationDate = null;
		else
			this.creationDate = new Date(creationDate);
		this.creationDateLong = creationDate;
	}
	public Long getLastRevisionDateLong() {
		return lastRevisionDateLong;
	}
	public void setLastRevisionDateLong(Long lastRevisionDate) {
		if(lastRevisionDate == null)
			this.lastRevisionDate=null;
		else
			this.lastRevisionDate = new Date(lastRevisionDate);
		this.lastRevisionDateLong = lastRevisionDate;
	}
	
	
	
	public String getRevisionHistory() {
		return revisionHistory;
	}
	public void setRevisionHistory(String revisionHistory) {
		this.revisionHistory = revisionHistory;
	}
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
		final DatasetMetaHistory other = (DatasetMetaHistory) obj;
		if (metaHistoryPK == null) {
			if (other.metaHistoryPK != null)
				return false;
		} else if (!metaHistoryPK.equals(other.metaHistoryPK))
			return false;
		return true;
	}

}

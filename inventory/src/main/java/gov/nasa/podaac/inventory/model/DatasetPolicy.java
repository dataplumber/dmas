//Copyright 2007, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;

/**
 * @author clwong
 * @version Sep 7, 2007
 * $Id: DatasetPolicy.java 8152 2011-08-01 21:56:05Z gangl $
 */
@SuppressWarnings("serial")
public class DatasetPolicy implements Serializable{

	private Integer datasetId;
	private String dataClass;
	private Integer dataFrequency;
	private Integer dataVolume;
	private Integer dataDuration;
	private Integer dataLatency;
	private String accessType;
	private String viewOnline;
	private String basePathAppendType;
	private String dataFormat;
	private String compressType;
	private String checksumType;
	private String spatialType;
	private String accessConstraint;
	private String useConstraint;
	private Dataset dataset;
	//private String versioned;
	//private String versionPolicy;

	public Integer getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}

	public String getViewOnline() {
		return viewOnline;
	}

	public void setViewOnline(String viewOnline) {
		this.viewOnline = viewOnline;
	}	

	public String getDataClass() {
		return dataClass;
	}


	public void setDataClass(String dataClass) {
		this.dataClass = dataClass;
	}


	public Integer getDataFrequency() {
		return dataFrequency;
	}


	public void setDataFrequency(Integer dataFrequency) {
		this.dataFrequency = dataFrequency;
	}

	/*public String getVersioned() {
		return versioned;
	}


	public void setVersioned(String versioned) {
		this.versioned = versioned;
	}
	
	public String getVersionPolicy() {
		return versionPolicy;
	}


	public void setVersionPolicy(String versionPolicy) {
		this.versionPolicy= versionPolicy;
	}*/

	public Integer getDataVolume() {
		return dataVolume;
	}


	public void setDataVolume(Integer dataVolume) {
		this.dataVolume = dataVolume;
	}


	public Integer getDataLatency() {
		return dataLatency;
	}


	public void setDataLatency(Integer dataLatency) {
		this.dataLatency = dataLatency;
	}
	
	public Integer getDataDuration() {
		return dataDuration;
	}


	public void setDataDuration(Integer dataDuration) {
		this.dataDuration = dataDuration;
	}


	public String getAccessType() {
		return accessType;
	}


	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}


	public String getBasePathAppendType() {
		return basePathAppendType;
	}


	public void setBasePathAppendType(String basePathAppendType) {
		this.basePathAppendType = basePathAppendType;
	}


	public String getDataFormat() {
		return dataFormat;
	}


	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}


	public String getCompressType() {
		return compressType;
	}


	public void setCompressType(String compressType) {
		this.compressType = compressType;
	}


	public String getChecksumType() {
		return checksumType;
	}


	public void setChecksumType(String checksumType) {
		this.checksumType = checksumType;
	}


	public String getSpatialType() {
		return spatialType;
	}


	public void setSpatialType(String spatialType) {
		this.spatialType = spatialType;
	}


	public String getAccessConstraint() {
		return accessConstraint;
	}


	public void setAccessConstraint(String accessConstraint) {
		this.accessConstraint = accessConstraint;
	}


	public String getUseConstraint() {
		return useConstraint;
	}


	public void setUseConstraint(String useConstraint) {
		this.useConstraint = useConstraint;
	}

	public Dataset getDataset() {
		return dataset;
	}


	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
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
		final DatasetPolicy other = (DatasetPolicy) obj;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		return true;
	}
	
	private String getDiff(Object o1, Object o2, String param){
		try{ 
			if(!o1.equals(o2)){
				return "DatasetPolicy "+param+" changed from "+o1+" to " + o2 + "\n";
			} 
		}catch(NullPointerException npe){
			if(o2!=null)
				return "DatasetPolicy "+param+" set to " + o2 + "\n";
		}
		return "";
	}
	
	public String diff(DatasetPolicy d){
		StringBuilder sb = new StringBuilder();
		
		sb.append(getDiff(this.getViewOnline(),d.getViewOnline(),"ViewOnline"));
		sb.append(getDiff(this.getDataClass(),d.getDataClass(),"DataClass"));
		sb.append(getDiff(this.getDataFrequency(),d.getDataFrequency(),"DataFrequency"));
		sb.append(getDiff(this.getDataVolume(),d.getDataVolume(),"DataVolume"));
		sb.append(getDiff(this.getDataLatency(),d.getDataLatency(),"DataLatency"));
		sb.append(getDiff(this.getDataDuration(),d.getDataDuration(),"DataDuration"));
		sb.append(getDiff(this.getAccessType(),d.getAccessType(),"AccessType"));
		sb.append(getDiff(this.getBasePathAppendType(),d.getBasePathAppendType(),"BasePathAppendType"));
		sb.append(getDiff(this.getDataFormat(),d.getDataFormat(),"DataFormat"));
		sb.append(getDiff(this.getCompressType(),d.getCompressType(),"CompressType"));
		sb.append(getDiff(this.getSpatialType(),d.getSpatialType(),"SpatialType"));
		sb.append(getDiff(this.getChecksumType(),d.getChecksumType(),"ChecksumType"));
		sb.append(getDiff(this.getAccessConstraint(),d.getAccessConstraint(),"AccessConstraint"));
		sb.append(getDiff(this.getUseConstraint(),d.getUseConstraint(),"UseConstraint"));
//		sb.append(getDiff(this.getVersioned(),d.getVersioned(),"Versioned"));
//		sb.append(getDiff(this.getVersionPolicy(),d.getVersionPolicy(),"VersionPolicy"));
		return sb.toString();
	}
	
}

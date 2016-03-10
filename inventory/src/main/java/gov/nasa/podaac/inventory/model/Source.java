//Copyright 2007, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author clwong
 * @version Sep 7, 2007
 * $Id: Source.java 4687 2010-04-02 22:46:03Z gangl $
 */
@SuppressWarnings("serial")
public class Source implements Serializable {

	private Integer sourceId;
	private String sourceShortName;
	private String sourceLongName;
	private String sourceType;
	private Float orbitPeriod;
	private Float inclinationAngle;
	private String sourceDescription;
	private Set<DatasetSource> datasetSourceSet = new HashSet<DatasetSource>();
	//private Set<Sensor> sensorSet = new HashSet<Sensor>();
	public Integer getSourceId() {
		return sourceId;
	}
	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}
	public String getSourceShortName() {
		return sourceShortName;
	}
	public void setSourceShortName(String sourceShortName) {
		this.sourceShortName = sourceShortName;
	}
	public String getSourceLongName() {
		return sourceLongName;
	}
	public void setSourceLongName(String sourceLongName) {
		this.sourceLongName = sourceLongName;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public Float getOrbitPeriod() {
		return orbitPeriod;
	}
	public void setOrbitPeriod(Float orbitPeriod) {
		this.orbitPeriod = orbitPeriod;
	}
	public Float getInclinationAngle() {
		return inclinationAngle;
	}
	public void setInclinationAngle(Float inclinationAngle) {
		this.inclinationAngle = inclinationAngle;
	}
	public String getSourceDescription() {
		return sourceDescription;
	}
	public void setSourceDescription(String sourceDescription) {
		this.sourceDescription = sourceDescription;
	}
	public Set<DatasetSource> getDatasetSourceSet() {
		return datasetSourceSet;
	}
	public void setDatasetSourceSet(Set<DatasetSource> datasetSourceSet) {
		this.datasetSourceSet = datasetSourceSet;
	}
//	public Set<Sensor> getSensorSet() {
//		
//		
//		return sensorSet;
//	}
//	public void setSensorSet(Set<Sensor> sensorSet) {
//		this.sensorSet = sensorSet;
//	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sourceId == null) ? 0 : sourceId.hashCode());
		result = prime * result
				+ ((sourceShortName == null) ? 0 : sourceShortName.hashCode());
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
		final Source other = (Source) obj;
		if (sourceId == null) {
			if (other.sourceId != null)
				return false;
		} else if (!sourceId.equals(other.sourceId))
			return false;
		if (sourceShortName == null) {
			if (other.sourceShortName != null)
				return false;
		} else if (!sourceShortName.equals(other.sourceShortName))
			return false;
		return true;
	}

}

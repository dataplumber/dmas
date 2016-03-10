//Copyright 2007, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author clwong
 * @version Sep 7, 2007
 * $Id: Sensor.java 6614 2011-01-19 00:06:26Z gangl $
 */
@SuppressWarnings("serial")
public class Sensor implements Serializable {

	private Integer sensorId;
	private String sensorShortName;
	private String sensorLongName;
	private Float swathWidth;
	private String sensorDescription;
	private Float sampleFrequency;
	private Set<DatasetSource> datasetSourceSet = new HashSet<DatasetSource>();
	
	
	public Integer getSensorId() {
		return sensorId;
	}
	public void setSensorId(Integer sensorId) {
		this.sensorId = sensorId;
	}
	public String getSensorShortName() {
		return sensorShortName;
	}
	public void setSensorShortName(String sensorShortName) {
		this.sensorShortName = sensorShortName;
	}
	public String getSensorLongName() {
		return sensorLongName;
	}
	public void setSensorLongName(String sensorLongName) {
		this.sensorLongName = sensorLongName;
	}
	public Float getSampleFrequency() {
		return sampleFrequency;
	}
	public void setSampleFrequency(Float sampleFrequency) {
		this.sampleFrequency = sampleFrequency;
	}
	public Float getSwathWidth() {
		return swathWidth;
	}
	public void setSwathWidth(Float swathWidth) {
		this.swathWidth = swathWidth;
	}
	public String getSensorDescription() {
		return sensorDescription;
	}
	public void setSensorDescription(String sensorDescription) {
		this.sensorDescription = sensorDescription;
	}
	public Set<DatasetSource> getDatasetSourceSet() {
		return datasetSourceSet;
	}
	public void setDatasetSourceSet(Set<DatasetSource> datasetSourceSet) {
		this.datasetSourceSet = datasetSourceSet;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sensorId == null) ? 0 : sensorId.hashCode());
		result = prime * result
				+ ((sensorShortName == null) ? 0 : sensorShortName.hashCode());
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
		final Sensor other = (Sensor) obj;
		if (sensorId == null) {
			if (other.sensorId != null)
				return false;
		} else if (!sensorId.equals(other.sensorId))
			return false;
		if (sensorShortName == null) {
			if (other.sensorShortName != null)
				return false;
		} else if (!sensorShortName.equals(other.sensorShortName))
			return false;
		return true;
	}
	

}

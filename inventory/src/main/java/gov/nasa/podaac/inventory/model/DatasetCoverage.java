//Copyright 2007, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author clwong
 * @version Sep 7, 2007
 * $Id: DatasetCoverage.java 14481 2015-11-24 23:17:40Z gangl $
 */
@SuppressWarnings("serial")
public class DatasetCoverage implements Serializable {

	private Integer datasetId;
	private Date startTime;
	private Date stopTime;
	
	private Double northLat;
	private Double southLat;
	private Double eastLon;
	private Double westLon;
	private Long startTimeLong, stopTimeLong;
	private Double minAltitude;
	private Double maxAltitude;
	private Double minDepth;
	private Double maxDepth;
        private Character granuleRange360;
	private Dataset dataset;


	//Added in 4.4.0
	public Character getGranuleRange360() {
                return granuleRange360;
        }
        public void setGranuleRange360(Character granuleRange360) {
                this.granuleRange360 = granuleRange360;
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
	 * @return the eastLon
	 */
	public Double getEastLon() {
		return eastLon;
	}
	/**
	 * @param eastLon the eastLon to set
	 */
	public void setEastLon(Double eastLon) {
		this.eastLon = eastLon;
	}
	/**
	 * @return the maxAltitude
	 */
	public Double getMaxAltitude() {
		return maxAltitude;
	}
	/**
	 * @param maxAltitude the maxAltitude to set
	 */
	public void setMaxAltitude(Double maxAltitude) {
		this.maxAltitude = maxAltitude;
	}
	/**
	 * @return the maxDepth
	 */
	public Double getMaxDepth() {
		return maxDepth;
	}
	/**
	 * @param maxDepth the maxDepth to set
	 */
	public void setMaxDepth(Double maxDepth) {
		this.maxDepth = maxDepth;
	}
	/**
	 * @return the minAltitude
	 */
	public Double getMinAltitude() {
		return minAltitude;
	}
	/**
	 * @param minAltitude the minAltitude to set
	 */
	public void setMinAltitude(Double minAltitude) {
		this.minAltitude = minAltitude;
	}
	/**
	 * @return the minDepth
	 */
	public Double getMinDepth() {
		return minDepth;
	}
	/**
	 * @param minDepth the minDepth to set
	 */
	public void setMinDepth(Double minDepth) {
		this.minDepth = minDepth;
	}
	/**
	 * @return the northLat
	 */
	public Double getNorthLat() {
		return northLat;
	}
	/**
	 * @param northLat the northLat to set
	 */
	public void setNorthLat(Double northLat) {
		this.northLat = northLat;
	}
	/**
	 * @return the southLat
	 */
	public Double getSouthLat() {
		return southLat;
	}
	/**
	 * @param southLat the southLat to set
	 */
	public void setSouthLat(Double southLat) {
		this.southLat = southLat;
	}
	
	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
		this.startTimeLong = new Long(startTime.getTime());
	}
	/**
	 * @return the stopTime
	 */
	public Date getStopTime() {
		return stopTime;
	}
	/**
	 * @param stopTime the stopTime to set
	 */
	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
		this.stopTimeLong = new Long(stopTime.getTime());
	}
	
	public Long getStartTimeLong() {
		return startTimeLong;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTimeLong(Long startTime) {
		if(startTime == null)
			this.startTime = null;
		else
			this.startTime = new Date(startTime);
		this.startTimeLong = startTime;
	}
	/**
	 * @return the stopTime
	 */
	public Long getStopTimeLong() {
		return stopTimeLong;
	}
	/**
	 * @param stopTime the stopTime to set
	 */
	public void setStopTimeLong(Long stopTime) {
		if(stopTime == null)
			this.stopTime = null;
		else
			this.stopTime = new Date(stopTime);
		this.stopTimeLong = stopTime;
	}
	
	
	/**
	 * @return the westLon
	 */
	public Double getWestLon() {
		return westLon;
	}
	/**
	 * @param westLon the westLon to set
	 */
	public void setWestLon(Double westLon) {
		this.westLon = westLon;
	}
	/**
	 * @return the dataset
	 */
	public Dataset getDataset() {
		return dataset;
	}
	/**
	 * @param dataset the dataset to set
	 */
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
		final DatasetCoverage other = (DatasetCoverage) obj;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		return true;
	}
	
	public String diff(DatasetCoverage d){
		StringBuilder sb = new StringBuilder();
		
		try{ 
			if(!this.getEastLon().equals(d.getEastLon())){
				sb.append("DatasetCoverage East Longitude  changed from "+ this.getEastLon()+" to " + d.getEastLon() + "\n");
			} 
		}catch(NullPointerException npe){
			if(d.getEastLon()!=null)
				sb.append("DatasetCoverage East Longitude set to " + d.getEastLon() + "\n");
		}
		try{ 
			if(!this.getWestLon().equals(d.getWestLon())){
				sb.append("DatasetCoverage West Longitude  changed from "+ this.getWestLon()+" to " + d.getWestLon() + "\n");
			} 
		}catch(NullPointerException npe){
			if(d.getWestLon()!=null)
				sb.append("DatasetCoverage West Longitude set to " + d.getWestLon() + "\n");
		}
		try{ 
			if(!this.getNorthLat().equals(d.getNorthLat())){
				sb.append("DatasetCoverage North Latitude  changed from "+ this.getNorthLat()+" to " + d.getNorthLat() + "\n");
			} 
		}catch(NullPointerException npe){
			if(d.getNorthLat()!=null)
				sb.append("DatasetCoverage North Latitude set to " + d.getNorthLat() + "\n");
		}
		try{ 
			if(!this.getSouthLat().equals(d.getSouthLat())){
				sb.append("DatasetCoverage South Latitude  changed from "+ this.getSouthLat()+" to " + d.getSouthLat() + "\n");
			} 
		}catch(NullPointerException npe){
			if(d.getSouthLat()!=null)
				sb.append("DatasetCoverage South Latitude set to " + d.getSouthLat() + "\n");
		}
		try{ 
			if(!this.getMinAltitude().equals(d.getMinAltitude())){
				sb.append("DatasetCoverage MinAltitude  changed from "+ this.getMinAltitude()+" to " + d.getMinAltitude() + "\n");
			} 
		}catch(NullPointerException npe){
			if(d.getMinAltitude()!=null)
				sb.append("DatasetCoverage MinAltitude set to " + d.getMinAltitude() + "\n");
		}
		try{ 
			if(!this.getMaxAltitude().equals(d.getMaxAltitude())){
				sb.append("DatasetCoverage MaxAltitude  changed from "+ this.getMaxAltitude()+" to " + d.getMaxAltitude() + "\n");
			} 
		}catch(NullPointerException npe){
			if(d.getMaxAltitude()!=null)
				sb.append("DatasetCoverage MaxAltitude set to " + d.getMaxAltitude() + "\n");
		}
		try{ 
			if(!this.getMinDepth().equals(d.getMinDepth())){
				sb.append("DatasetCoverage MinDepth  changed from "+ this.getMinDepth()+" to " + d.getMinDepth() + "\n");
			} 
		}catch(NullPointerException npe){
			if(d.getMinDepth()!=null)
				sb.append("DatasetCoverage MinDepth set to " + d.getMinDepth() + "\n");
		}
		try{ 
			if(!this.getMaxDepth().equals(d.getMaxDepth())){
				sb.append("DatasetCoverage MaxDepth  changed from "+ this.getMaxDepth()+" to " + d.getMaxDepth() + "\n");
			} 
		}catch(NullPointerException npe){
			if(d.getMaxDepth()!=null)
				sb.append("DatasetCoverage MaxDepth set to " + d.getMaxDepth() + "\n");
		}		
		try{ 
			if(!this.getStartTimeLong().equals(d.getStartTimeLong())){
				sb.append("DatasetCoverage StartTime  changed from "+ this.getStartTimeLong()+" to " + d.getStartTimeLong() + "\n");
			} 
		}catch(NullPointerException npe){
			if(d.getStartTimeLong()!=null)
				sb.append("DatasetCoverage StartTime set to " + d.getStartTimeLong() + "\n");
		}
		try{ 
			if(!this.getStopTimeLong().equals(d.getStopTimeLong())){
				sb.append("DatasetCoverage StopTime  changed from "+ this.getStopTimeLong()+" to " + d.getStopTimeLong() + "\n");
			} 
		}catch(NullPointerException npe){
			if(d.getStopTimeLong()!=null)
				sb.append("DatasetCoverage StopTime set to " + d.getStopTimeLong() + "\n");
		}
		return sb.toString();
	}
	

}

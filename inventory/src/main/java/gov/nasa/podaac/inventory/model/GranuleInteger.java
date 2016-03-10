//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;


/**
 * @author clwong
 * @version Jul 30, 2007
 * $Id: GranuleInteger.java 6645 2011-01-21 17:59:10Z gangl $
 */
@SuppressWarnings("serial")
public class GranuleInteger implements Serializable {

	private Granule granule;
	private DatasetElement datasetElement = new DatasetElement();
	private Integer value;
	private String units;
	
	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}
	/**
	 * @param granuleElementDD
	 * @param value
	 */
	public GranuleInteger(DatasetElement de, Integer value) {
		this.datasetElement = de;
		this.value = value;
	}
	
	public GranuleInteger(DatasetElement datasetElement, Integer value, String units) {
		this.datasetElement = datasetElement;
		this.value = value;
		this.units=units;
	}
	
	public GranuleInteger() {
	}

	public Granule getGranule() {
		return granule;
	}

	public void setGranule(Granule granule) {
		this.granule = granule;
	}

	public DatasetElement getDatasetElement() {
		return datasetElement;
	}

	public void setDatasetElement(DatasetElement de) {
		this.datasetElement = de;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((datasetElement.getDeId() == null) ? 0 : datasetElement.getDeId().hashCode());
		result = prime * result + ((granule == null) ? 0 : granule.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			{
				
				return false;
			}
		final GranuleInteger other = (GranuleInteger) obj;
		if(this.getDatasetElement() == null){
			if(other.getDatasetElement()!=null)
				{
				
					return false;
				}
		}else if(this.getDatasetElement().equals(other.getDatasetElement()))
			{	
				return true;
			}
		if (this.getGranule() == null) {
			if (other.getGranule() != null)
				{	
					return false;
				}
		} else if (!this.getGranule().equals(other.getGranule()))
			{
				return false;
			}
		if (this.getValue() == null) {
			if (other.getValue() != null)
				{
					return false;
				}
		} else if (!this.getValue().equals(other.getValue()))
			{
				return false;
			}
			return true;
	}

	public String toString() {
		return datasetElement.getElementDD().getElementId() + ":" + value;
	}
}

//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;


/**
 * @author clwong
 * @version Jul 30, 2007
 * $Id: GranuleReal.java 6645 2011-01-21 17:59:10Z gangl $
 */
@SuppressWarnings("serial")
public class GranuleReal implements Serializable {
	
	private Granule granule;
	private DatasetElement datasetElement= new DatasetElement();
	private Double value;
	private String units;
	
	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}
	public GranuleReal() {
	}

	public GranuleReal(DatasetElement element, Double keyValue) {
		this.datasetElement = element;
		this.value = keyValue;
	}
	
	public GranuleReal(DatasetElement element, Double keyValue, String units) {
		this.datasetElement = element;
		this.value = keyValue;
		this.units = units;
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

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
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
		final GranuleReal other = (GranuleReal) obj;
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

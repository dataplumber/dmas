//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;

import com.vividsolutions.jts.geom.Polygon;


/**
 * @author clwong
 * $Id: GranuleSpatial.java 6645 2011-01-21 17:59:10Z gangl $
 */
@SuppressWarnings("serial")
public class GranuleSpatial implements Serializable {
	
	private Granule granule;
	private DatasetElement datasetElement= new DatasetElement();
	private Polygon value;
	
	public GranuleSpatial() {
	}

	public GranuleSpatial(DatasetElement element, Polygon keyValue) {
		this.datasetElement = element;
		this.value = keyValue;
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
	public Polygon getValue() {
		return value;
	}

	public void setValue(Polygon value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getGranule() == null) ? 0 : this.getGranule().hashCode());
		result = prime
				* result
				+ ((this.getDatasetElement() == null) ? 0 : this.getDatasetElement().hashCode());
		//result = prime * result + ((this.getValue() == null) ? 0 : this.getValue().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			{
		
				return true;
			}
		if (obj == null)
			{	
				return false;
			}
		if (this.getClass() != obj.getClass())
			{
			
				return false;
			}
		final GranuleSpatial other = (GranuleSpatial) obj;
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

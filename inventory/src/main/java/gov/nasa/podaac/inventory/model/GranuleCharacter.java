//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;

/**
 * @author clwong
 * @version Jul 26, 2007
 * $Id: GranuleCharacter.java 6645 2011-01-21 17:59:10Z gangl $
 */
@SuppressWarnings("serial")
public class GranuleCharacter implements Serializable {

	private Granule granule;
	private DatasetElement datasetElement= new DatasetElement();
	private String value;
	
	/**
	 * 
	 */
	public GranuleCharacter() {
	}

	public GranuleCharacter(DatasetElement element, String keyValue) {
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getDatasetElement().getDeId() == null) ? 0 : this.getDatasetElement().getDeId().hashCode());
		result = prime * result + ((this.getGranule() == null) ? 0 : this.getGranule().hashCode());
		result = prime * result + ((this.getValue() == null) ? 0 : this.getValue().hashCode());
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
		final GranuleCharacter other = (GranuleCharacter) obj;
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

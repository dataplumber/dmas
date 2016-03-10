//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;


import java.io.Serializable;
import java.util.Date;

/**
 * @author clwong
 * @version Jul 30, 2007
 * $Id: GranuleDateTime.java 6645 2011-01-21 17:59:10Z gangl $
 */
@SuppressWarnings("serial")
public class GranuleDateTime implements Serializable {

	private Granule granule;
	private DatasetElement datasetElement = new DatasetElement();
	private Date value;
	private Long valueLong;
		
	public GranuleDateTime() {
	}

	public GranuleDateTime(DatasetElement element, Date keyValue) {
		this.datasetElement = element;
		this.value = keyValue;
		try{
		this.valueLong = new Long(keyValue.getTime());
		}catch(NullPointerException npe)
		{
			this.valueLong = null;
		}
	}
	
	public GranuleDateTime(DatasetElement element, Long keyValue) {
		this.datasetElement = element;
		
		this.value = new Date(keyValue);
		
		this.valueLong =  keyValue;
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

	public Date getValue() {
		return value;
	}

	public void setValueLong(Long value) {
		this.valueLong = value;
		if(value==null)
			this.value = null;
		else
			this.value = new Date(value);
	}
	
	public Long getValueLong() {
		return valueLong;
	}

	public void setValue(Date value) {
		this.value = value;
		this.valueLong = new Long(value.getTime());
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
		final GranuleDateTime other = (GranuleDateTime) obj;
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

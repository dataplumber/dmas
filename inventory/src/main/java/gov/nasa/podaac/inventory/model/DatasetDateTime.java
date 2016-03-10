//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;


import java.io.Serializable;
import java.util.Date;

/**
 * @author clwong
 * @version Jul 30, 2007
 * $Id: DatasetDateTime.java 1165 2008-05-20 18:57:40Z clwong $
 */
@SuppressWarnings("serial")
public class DatasetDateTime implements Serializable {

	private Dataset granule = new Dataset();
	
	private Date value;
	private Long valueLong;
	private DatasetElement datasetElement = new DatasetElement();
	public DatasetElement getDatasetElement() {
		return datasetElement;
	}

	public void setDatasetElement(DatasetElement de) {
		this.datasetElement = de;
	}
	
	public DatasetDateTime() {
	}

	public DatasetDateTime(DatasetElement element, Date keyValue) {
		this.datasetElement = element;
		this.value = keyValue;
		this.valueLong = new Long(value.getTime());
	}
	
	public DatasetDateTime(DatasetElement element, Long keyValue) {
		this.datasetElement = element;
		if(keyValue == null)
			this.value= null;
		else
			this.value = new Date(keyValue);
		this.valueLong = keyValue;
	}
	

	public Dataset getDataset() {
		return granule;
	}

	public void setDataset(Dataset granule) {
		this.granule = granule;
	}


	public Date getValue() {
		return value;
	}

	public void setValueLong(Long value) {
		this.valueLong = value;
		if(value == null)
			this.value=null;
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
		if (getClass() != obj.getClass())
			return false;
		final DatasetDateTime other = (DatasetDateTime) obj;
		if (granule == null) {
			if (other.granule != null)
				return false;
		} else if (!granule.equals(other.granule))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public String toString() {
		return datasetElement.getElementDD().getElementId() + ":" + value;
	}
}

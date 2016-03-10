//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;


/**
 * @author clwong
 * @version Jul 30, 2007
 * $Id: DatasetReal.java 1165 2008-05-20 18:57:40Z clwong $
 */
@SuppressWarnings("serial")
public class DatasetReal implements Serializable {
	
	private Dataset granule = new Dataset();
	private Double value;
	private String units;
	private DatasetElement datasetElement = new DatasetElement();
	
	public DatasetElement getDatasetElement() {
		return datasetElement;
	}

	public void setDatasetElement(DatasetElement de) {
		this.datasetElement = de;
	}
	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public DatasetReal() {
	}

	public DatasetReal(DatasetElement element, Double keyValue) {
		this.datasetElement = element;
		this.value = keyValue;
	}
	
	public DatasetReal(DatasetElement element, Double keyValue, String units) {
		this.datasetElement = element;
		this.value = keyValue;
		this.units = units;
	}

	public Dataset getDataset() {
		return granule;
	}

	public void setDataset(Dataset granule) {
		this.granule = granule;
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
		final DatasetReal other = (DatasetReal) obj;
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

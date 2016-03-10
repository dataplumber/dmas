//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;

/**
 * @author clwong
 * @version Jul 26, 2007
 * $Id: DatasetCharacter.java 1165 2008-05-20 18:57:40Z clwong $
 */
@SuppressWarnings("serial")
public class DatasetCharacter implements Serializable {

	private Dataset dataset = new Dataset();
	
	private String value;
	
	private DatasetElement datasetElement = new DatasetElement();
	public DatasetElement getDatasetElement() {
		return datasetElement;
	}

	public void setDatasetElement(DatasetElement de) {
		this.datasetElement = de;
	}
	
	/**
	 * 
	 */
	public DatasetCharacter() {
	}

	public DatasetCharacter(DatasetElement element, String keyValue) {
		this.datasetElement = element;
		this.value = keyValue;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset d) {
		this.dataset = d;
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
		result = prime * result + ((dataset == null) ? 0 : dataset.hashCode());
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
		final DatasetCharacter other = (DatasetCharacter) obj;
		if (dataset == null) {
			if (other.dataset != null)
				return false;
		} else if (!dataset.equals(other.dataset))
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

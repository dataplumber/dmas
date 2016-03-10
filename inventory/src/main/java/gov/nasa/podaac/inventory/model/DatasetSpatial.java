//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.inventory.model;

import java.io.Serializable;

import com.vividsolutions.jts.geom.Polygon;

/**
 * @author clwong
 * $Id: DatasetSpatial.java 5056 2010-06-11 22:21:41Z gangl $
 */
@SuppressWarnings("serial")
public class DatasetSpatial implements Serializable {

//	private Integer datasetId;
	private Polygon spatialGeometry;
	private Dataset dataset;
	private DatasetElement datasetElement = new DatasetElement();
	public DatasetElement getDatasetElement() {
		return datasetElement;
	}

	public void setDatasetElement(DatasetElement de) {
		this.datasetElement = de;
	}
	
//	public Integer getDatasetId() {
//		return datasetId;
//	}
//	public void setDatasetId(Integer datasetId) {
//		this.datasetId = datasetId;
//	}
	public Polygon getSpatialGeometry() {
		return spatialGeometry;
	}
	public void setSpatialGeometry(Polygon spatialGeometry) {
		this.spatialGeometry = spatialGeometry;
	}
	public Dataset getDataset() {
		return dataset;
	}
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataset.getDatasetId() == null) ? 0 : dataset.getDatasetId().hashCode());
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
		final DatasetSpatial other = (DatasetSpatial) obj;
		if (dataset.getDatasetId() == null) {
			if (other.dataset.getDatasetId() != null)
				return false;
		} else if (!dataset.getDatasetId().equals(other.dataset.getDatasetId()))
			return false;
		else if(!this.getDatasetElement().equals(other.getDatasetElement()))
			return false;
		return true;
	}
}

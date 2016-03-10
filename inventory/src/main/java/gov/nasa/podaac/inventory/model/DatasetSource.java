//Copyright 2007, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;
/**
 * @author clwong
 * @version $Id: DatasetSource.java 4741 2010-04-16 19:05:21Z gangl $
 */
@SuppressWarnings("serial")
public class DatasetSource implements Serializable {

	public static class DatasetSourcePK implements Serializable{
		private Dataset dataset;
		private Source source = null;
		private Sensor sensor = null;
		public Dataset getDataset() {
			return dataset;
		}
		public void setDataset(Dataset dataset) {
			this.dataset = dataset;
		}
		public Source getSource() {
			return source;
		}
		public void setSource(Source source) {
			this.source = source;
		}
		public Sensor getSensor() {
			return sensor;
		}
		public void setSensor(Sensor sensor) {
			this.sensor = sensor;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((dataset == null) ? 0 : dataset.hashCode());
			result = prime * result
					+ ((sensor == null) ? 0 : sensor.hashCode());
			result = prime * result
					+ ((source == null) ? 0 : source.hashCode());
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
			final DatasetSourcePK other = (DatasetSourcePK) obj;
			if (dataset == null) {
				if (other.dataset != null)
					return false;
			} else if (!dataset.equals(other.dataset))
				return false;
			if (sensor == null) {
				if (other.sensor != null)
					return false;
			} else if (!sensor.equals(other.sensor))
				return false;
			if (source == null) {
				if (other.source != null)
					return false;
			} else if (!source.equals(other.source))
				return false;
			return true;
		}
	}
	
	private DatasetSourcePK datasetSourcePK = new DatasetSourcePK();

	public DatasetSourcePK getDatasetSourcePK() {
		return datasetSourcePK;
	}

	public void setDatasetSourcePK(DatasetSourcePK datasetSourcePK) {
		this.datasetSourcePK = datasetSourcePK;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datasetSourcePK == null) ? 0 : datasetSourcePK.hashCode());
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
		final DatasetSource other = (DatasetSource) obj;
		if (datasetSourcePK == null) {
			if (other.datasetSourcePK != null)
				return false;
		} else if (!datasetSourcePK.equals(other.datasetSourcePK))
			return false;
		return true;
	}

}

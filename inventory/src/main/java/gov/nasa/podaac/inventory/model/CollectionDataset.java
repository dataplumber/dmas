//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;

/**
 * @author clwong
 * $Id: CollectionDataset.java 4752 2010-04-20 16:14:47Z gangl $
 *
 */
@SuppressWarnings("serial")
public class CollectionDataset implements Serializable {

	public static class CollectionDatasetPK implements Serializable {
		private Collection collection = new Collection();
		private Dataset dataset = new Dataset();
		public Collection getCollection() {
			return collection;
		}
		public void setCollection(Collection collection) {
			this.collection = collection;
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
					+ ((collection == null) ? 0 : collection.hashCode());
			result = prime * result
					+ ((dataset == null) ? 0 : dataset.hashCode());
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
			final CollectionDatasetPK other = (CollectionDatasetPK) obj;
			if (collection == null) {
				if (other.collection != null)
					return false;
			} else if (!collection.equals(other.collection))
				return false;
			if (dataset == null) {
				if (other.dataset != null)
					return false;
			} else if (!dataset.equals(other.dataset))
				return false;
			return true;
		}
	}
		
	private CollectionDatasetPK collectionDatasetPK = new CollectionDatasetPK();
	private Character granuleFlag, granuleRange360;
	private Integer startGranuleId, stopGranuleId;
	
	public CollectionDatasetPK getCollectionDatasetPK() {
		return collectionDatasetPK;
	}
	public void setCollectionDatasetPK(CollectionDatasetPK collectionDatasetPK) {
		this.collectionDatasetPK = collectionDatasetPK;
	}
	public Character getGranuleFlag() {
		return granuleFlag;
	}
	public void setGranuleFlag(Character granuleFlag) {
		this.granuleFlag = granuleFlag;
	}
	public Character getGranuleRange360() {
                return granuleRange360;
        }
        public void setGranuleRange360(Character granuleRange360) {
                this.granuleRange360 = granuleRange360;
        }
	
	public Integer getStartGranuleId() {
		return startGranuleId;
	}
	public void setStartGranuleId(Integer startGranuleId) {
		this.startGranuleId = startGranuleId;
	}
	public Integer getStopGranuleId() {
		return stopGranuleId;
	}
	public void setStopGranuleId(Integer stopGranuleId) {
		this.stopGranuleId = stopGranuleId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((collectionDatasetPK == null) ? 0 : collectionDatasetPK
						.hashCode());
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
		final CollectionDataset other = (CollectionDataset) obj;
		if (collectionDatasetPK == null) {
			if (other.collectionDatasetPK != null)
				return false;
		} else if (!collectionDatasetPK.equals(other.collectionDatasetPK))
			return false;
		return true;
	}
}

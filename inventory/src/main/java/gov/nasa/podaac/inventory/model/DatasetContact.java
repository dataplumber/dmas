// Copyright 2008, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;

/**
 * @author clwong
 * @version $Id: DatasetContact.java 4739 2010-04-16 16:15:39Z gangl $
 */
@SuppressWarnings("serial")
public class DatasetContact implements Serializable {

	public static class DatasetContactPK implements Serializable{
		private Dataset dataset = new Dataset();
		private Contact contact = new Contact();
		public Dataset getDataset() {
			return dataset;
		}
		public void setDataset(Dataset dataset) {
			this.dataset = dataset;
		}
		public Contact getContact() {
			return contact;
		}
		public void setContact(Contact contact) {
			this.contact = contact;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((contact == null) ? 0 : contact.hashCode());
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
			final DatasetContactPK other = (DatasetContactPK) obj;
			if (contact == null) {
				if (other.contact != null)
					return false;
			} else if (!contact.equals(other.contact))
				return false;
			if (dataset == null) {
				if (other.dataset != null)
					return false;
			} else if (!dataset.equals(other.dataset))
				return false;
			return true;
		}
	}
	
	private DatasetContactPK datasetContactPK = new DatasetContactPK();

	public DatasetContactPK getDatasetContactPK() {
		return datasetContactPK;
	}

	public void setDatasetContactPK(DatasetContactPK datasetContactPK) {
		this.datasetContactPK = datasetContactPK;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((datasetContactPK == null) ? 0 : datasetContactPK.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DatasetContact))
			return false;
		final DatasetContact other = (DatasetContact) obj;
		if (datasetContactPK == null) {
			if (other.datasetContactPK != null)
				return false;
		} else if (!datasetContactPK.equals(other.datasetContactPK))
			return false;
		return true;
	}
}

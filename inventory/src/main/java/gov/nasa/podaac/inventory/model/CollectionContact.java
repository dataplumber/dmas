//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.inventory.model;

import java.io.Serializable;

/**
 * @author clwong
 *
 * @version
 * $Id: CollectionContact.java 4751 2010-04-20 01:45:43Z gangl $
 */
@SuppressWarnings("serial")
public class CollectionContact implements Serializable {

	public static class CollectionContactPK implements Serializable {
		private Collection collection;
		private Contact contact = new Contact();
		public Collection getCollection() {
			return collection;
		}
		public void setCollection(Collection collection) {
			this.collection = collection;
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
					+ ((collection == null) ? 0 : collection.hashCode());
			result = prime * result
					+ ((contact == null) ? 0 : contact.hashCode());
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
			final CollectionContactPK other = (CollectionContactPK) obj;
			if (collection == null) {
				if (other.collection != null)
					return false;
			} else if (!collection.equals(other.collection))
				return false;
			if (contact == null) {
				if (other.contact != null)
					return false;
			} else if (!contact.equals(other.contact))
				return false;
			return true;
		}
	}
	private CollectionContactPK collectionContactPK = new CollectionContactPK();
	public CollectionContactPK getCollectionContactPK() {
		return collectionContactPK;
	}
	public void setCollectionContactPK(CollectionContactPK collectionContactPK) {
		this.collectionContactPK = collectionContactPK;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((collectionContactPK == null) ? 0 : collectionContactPK
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
		final CollectionContact other = (CollectionContact) obj;
		if (collectionContactPK == null) {
			if (other.collectionContactPK != null)
				return false;
		} else if (!collectionContactPK.equals(other.collectionContactPK))
			return false;
		return true;
	}

}

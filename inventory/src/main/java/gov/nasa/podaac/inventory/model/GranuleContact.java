// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;

/**
 * @author clwong
 * @version $Id: GranuleContact.java 2014 2008-09-29 15:40:32Z clwong $
 */
@SuppressWarnings("serial")
public class GranuleContact implements Serializable {

	public static class GranuleContactPK implements Serializable{
		private Granule granule;
		private Contact contact;
		public Granule getGranule() {
			return granule;
		}
		public void setGranule(Granule granule) {
			this.granule = granule;
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
					+ ((granule == null) ? 0 : granule.hashCode());
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
			final GranuleContactPK other = (GranuleContactPK) obj;
			if (contact == null) {
				if (other.contact != null)
					return false;
			} else if (!contact.equals(other.contact))
				return false;
			if (granule == null) {
				if (other.granule != null)
					return false;
			} else if (!granule.equals(other.granule))
				return false;
			return true;
		}
	}
	
	private GranuleContactPK granuleContactPK;

	public GranuleContactPK getGranuleContactPK() {
		return granuleContactPK;
	}

	public void setGranuleContactPK(GranuleContactPK granuleContactPK) {
		this.granuleContactPK = granuleContactPK;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((granuleContactPK == null) ? 0 : granuleContactPK.hashCode());
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
		final GranuleContact other = (GranuleContact) obj;
		if (granuleContactPK == null) {
			if (other.granuleContactPK != null)
				return false;
		} else if (!granuleContactPK.equals(other.granuleContactPK))
			return false;
		return true;
	}
}

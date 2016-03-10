//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * @author clwong
 * @version Jul 30, 2007
 * $Id: Contact.java 4744 2010-04-19 18:08:45Z gangl $
 */
@SuppressWarnings("serial")
public class Contact implements Serializable {

	private Integer contactId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String role;
	private String email;
	private String phone;
	private String fax;
	private String address;
	private String notifyType;
	private Provider provider = new Provider();
	
	private Set<DatasetContact> datasetContactSet = new HashSet<DatasetContact>();
	private Set<GranuleContact> granuleContactSet = new HashSet<GranuleContact>();
	private Set<CollectionContact> collectionContactSet = new HashSet<CollectionContact>();
	
	public Integer getContactId() {
		return contactId;
	}
	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getNotifyType() {
		return notifyType;
	}
	public void setNotifyType(String notifyType) {
		this.notifyType = notifyType;
	}
	public Provider getProvider() {
		return provider;
	}
	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	public Set<DatasetContact> getDatasetContactSet() {
		return datasetContactSet;
	}
	public void setDatasetContactSet(Set<DatasetContact> datasetContactSet) {
		this.datasetContactSet = datasetContactSet;
	}
	public Set<GranuleContact> getGranuleContactSet() {
		return granuleContactSet;
	}
	public void setGranuleContactSet(Set<GranuleContact> granuleContactSet) {
		this.granuleContactSet = granuleContactSet;
	}
	public Set<CollectionContact> getCollectionContactSet() {
		return collectionContactSet;
	}
	public void setCollectionContactSet(Set<CollectionContact> collectionContactSet) {
		this.collectionContactSet = collectionContactSet;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contactId == null) ? 0 : contactId.hashCode());
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
		final Contact other = (Contact) obj;
		if (contactId == null) {
			if (other.contactId != null)
				return false;
		} else if (!contactId.equals(other.contactId))
			return false;
		return true;
	}

}

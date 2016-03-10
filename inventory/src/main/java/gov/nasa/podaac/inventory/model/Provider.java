///Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.inventory.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author clwong
 * @version Jul 27, 2007
 * $Id: Provider.java 8045 2011-06-24 22:14:13Z gangl $
 */
@SuppressWarnings("serial")
public class Provider implements Serializable {

	private Integer providerId;
	private String shortName;
	private String longName;
	private String type;
	private Set<Dataset> datasetSet = new HashSet<Dataset>();
	private Set<ProviderResource> providerResourceSet = new HashSet<ProviderResource>();
	private Set<Contact> contactSet = new HashSet<Contact>();
	
	public Integer getProviderId() {
		return providerId;
	}
	public void setProviderId(Integer providerId) {
		this.providerId = new Integer(providerId);
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getLongName() {
		return longName;
	}
	public void setLongName(String longName) {
		this.longName = longName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Set<Dataset> getDatasetSet() {
		return datasetSet;
	}
	public void setDatasetSet(Set<Dataset> datasetSet) {
		this.datasetSet = datasetSet;
	}
	public Set<ProviderResource> getProviderResourceSet() {
		return providerResourceSet;
	}
	public void setProviderResourceSet(Set<ProviderResource> providerResourceSet) {
		this.providerResourceSet = providerResourceSet;
	}
	public Set<Contact> getContactSet() {
		return contactSet;
	}
	public void setContactSet(Set<Contact> contactSet) {
		this.contactSet = contactSet;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((providerId == null) ? 0 : providerId.hashCode());
		result = prime * result
				+ ((shortName == null) ? 0 : shortName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		final Provider other = (Provider) obj;
		if (providerId == null) {
			if (other.providerId != null)
				return false;
		} else if (!providerId.equals(other.providerId))
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	private String getDiff(Object o1, Object o2, String param){
		try{ 
			if(!o1.equals(o2)){
				return "Provider "+param+" changed from "+o1+" to " + o2 + "\n";
			} 
		}catch(NullPointerException npe){
			if(o2!=null)
				return "Provider "+param+" set to " + o2 + "\n";
		}
		return "";
	}
	
	public String diff(Provider p){
		return null;
	}
	

}

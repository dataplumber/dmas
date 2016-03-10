//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

/**
 * @author clwong
 * @version
 * $Id: Granule.java 2719 2009-03-17 22:53:49Z gangl $
 */
public class GranuleSIP {

	/**
	 * 
	 */
	private String sip;
	private Granule granule;
	
	public GranuleSIP() {
		super();
	}

	public GranuleSIP( String sip, Granule g) {
		super();
		this.sip = sip;
		this.granule = g;
	}
	
	

	public String getSip()
	{
		return this.sip;
	}
	
	public void setSip(String sip)
	{
		this.sip = sip;
	}
	
	public void setGranule(Granule g)
	{
		this.granule = g;
	}

	public Granule getGranule()
	{
		return this.granule;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		final GranuleSIP other; 
		try{
			other = (GranuleSIP) obj;
		}catch(ClassCastException cce){
			return false;
		}
		if(other.getSip() == this.getSip())
			return true;
		return false;
	}

}

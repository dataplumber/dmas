package gov.nasa.podaac.distribute.subscriber.model;


import java.util.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import gov.nasa.podaac.distribute.subscriber.model.*;

public class Dataset {
	
	int id;
	String shortName, longName, description, processingLevel;
	Date lastListingTime;
	
	private HashSet<Granule> granuleFiles= new HashSet<Granule>();
	
	public Dataset(String name)
	{
		this.shortName=name;
	}
	
	public Date getLastListingTime() {
		return lastListingTime;
	}

	public void setLastListingTime(Date lastListingTime) {
		this.lastListingTime = lastListingTime;
	}
	
	
	public int getId()
	{
		return this.id;
	}
	public String getShortName()
	{
		return this.shortName;
	}
	public String getLongName()
	{
		return this.longName;
	}
	public String getDescription()
	{
		return this.description;
	}
	public String getProcessingLevel()
	{
		return this.processingLevel;
	}
	
	public HashSet<Granule> getGranuleFiles()
	{
		
		return granuleFiles; 
	}
	
	public void addGranule(Granule g)
	{
		this.granuleFiles.add(g);
	}
	
	public void removeGranule(Granule g)
	{
		this.granuleFiles.remove(g);
	}
	
	
	public void setId(int id)
	{
		 this.id = id;
	}
	public void setShortName(String sn)
	{
		 this.shortName=sn;
	}
	public void setLongName(String ln)
	{
		 this.longName=ln;
	}
	public void setDescription(String d)
	{
		 this.description=d;
	}
	public void setProcessingLevel(String pl)
	{
		 this.processingLevel=pl;
	}
	
}

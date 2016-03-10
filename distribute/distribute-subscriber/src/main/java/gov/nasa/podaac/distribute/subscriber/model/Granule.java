package gov.nasa.podaac.distribute.subscriber.model;

import java.util.Date;
import java.util.HashSet;

public class Granule {

	int id;
	String name, accessType, status;
	private Date startTime;
	private Date stopTime;
	private Date createTime;
	private HashSet<GranuleFile> granuleFiles= null;
	private HashSet<GranuleReference> granuleReferences= null;
	
	
	public void addFile(GranuleFile gf)
	{
		granuleFiles.add(gf);
	}
	
	public boolean removeFiles(GranuleFile gf)
	{
		if(!granuleFiles.isEmpty())
			return granuleFiles.remove(gf);
		else
			return false;
	}
	
	public HashSet<GranuleFile> getFileList()
	{
		return granuleFiles;
	}
	
	public void addReference(GranuleReference gr)
	{
		granuleReferences.add(gr);
	}
	
	public boolean removeReference(GranuleReference gr)
	{
		if(!granuleReferences.isEmpty())
			return granuleReferences.remove(gr);
		else
			return false;
	}
	
	public HashSet<GranuleReference> getReferenceList()
	{
		return granuleReferences;
	}
	
	
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getAccessType() {
		return accessType;
	}


	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Date getStartTime() {
		return startTime;
	}


	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}


	public Date getStopTime() {
		return stopTime;
	}


	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}


	public Date getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public Granule(String accessType, Date createTime, int id, String name,
			Date startTime, String status, Date stopTime) {
		super();
		this.accessType = accessType;
		this.createTime = createTime;
		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.status = status;
		this.stopTime = stopTime;
		
		granuleReferences = new HashSet<GranuleReference>();
		granuleFiles = new HashSet<GranuleFile>();
		
	}
	
	public boolean equals(Granule g)
	{
		if(this.id != g.getId())
			return false;
		if(!this.name.equals(g.getName()))
			return false;
		return true;
	}
	
	public Granule()
	{
		granuleReferences = new HashSet<GranuleReference>();
		granuleFiles = new HashSet<GranuleFile>();
		
	}
	
}

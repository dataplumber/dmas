//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;


import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;
import gov.nasa.podaac.inventory.core.GranuleMetadataImpl;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author clwong
 * @version
 * $Id: Granule.java 13176 2014-04-03 18:56:47Z gangl $
 */

public class Granule {

	/**
	 * 
	 */
	private Integer granuleId;
	private String name;
	//private String officialName;
	private Date startTime;
	private Date stopTime;
	private Date requestedTime;
	private Date acquiredTime;
	private Date createTime;
	private Date ingestTime;
	private Date verifyTime;
	private Date archiveTime;
	
	
	
	private Long startTimeLong;
	private Long stopTimeLong;
	private Long requestedTimeLong;
	private Long acquiredTimeLong;
	private Long createTimeLong;
	private Long ingestTimeLong;
	private Long verifyTimeLong;
	private Long archiveTimeLong;
	
	private Integer version;
	private String accessType;
	private String dataFormat;
	private String compressType;
	private String checksumType;
	private String rootPath;
	private String relPath;
	private GranuleStatus status;

	private Set<GranuleSIP> granuleSIPSet  = new HashSet<GranuleSIP>();
    private Set<GranuleCharacter> granuleCharacterSet  = new HashSet<GranuleCharacter>();
    private Set<GranuleInteger> granuleIntegerSet = new HashSet<GranuleInteger>();
    private Set<GranuleReal> granuleRealSet = new HashSet<GranuleReal>();
    private Set<GranuleDateTime> granuleDateTimeSet = new HashSet<GranuleDateTime>();
    private Set<GranuleSpatial> granuleSpatialSet = new HashSet<GranuleSpatial>();
    private Set<GranuleArchive> granuleArchiveSet = new HashSet<GranuleArchive>();
    private Set<GranuleReference> granuleReferenceSet = new HashSet<GranuleReference>();
    private Set<GranuleMetaHistory> metaHistorySet = new HashSet<GranuleMetaHistory>();
    private Set<GranuleContact> granuleContactSet = new HashSet<GranuleContact>();
	private Dataset dataset;
	
	private static Log log = LogFactory.getLog(Granule.class);

	public Granule() {
		super();
	}
	
	public Granule(String name, Date startTime, Date requestedTime, Date acquiredTime, Date stopTime, Date createTime,
			Date ingestTime, Integer version, String accessType,
			String dataFormat, String compressType, String checksumType,
			GranuleStatus status, Date archiveTime, String rootPath, String relPath) {
		super();
		
		this.name = name;
		try{
		this.startTimeLong = new Long(startTime.getTime());
		}catch(NullPointerException npe)
		{
			this.startTimeLong = null;
		}
		try{
		this.stopTimeLong = new Long(stopTime.getTime());
		}catch(NullPointerException npe)
		{
			this.stopTimeLong = null;
		}
		
		try{
		this.createTimeLong = new Long(createTime.getTime());
		}catch(NullPointerException npe)
		{
			this.createTimeLong = null;
		}
		try{
		this.ingestTimeLong = new Long(ingestTime.getTime());
		}catch(NullPointerException npe)
		{
			this.ingestTimeLong = null;
		}
		this.version = version;
		try{
		this.requestedTimeLong = new Long(requestedTime.getTime());
		}catch(NullPointerException npe)
		{
			this.requestedTimeLong = null;
		}
		try{
		this.acquiredTimeLong = new Long(acquiredTime.getTime());
		}catch(NullPointerException npe)
		{
			this.acquiredTimeLong = null;
		}
		this.accessType = accessType;
		this.dataFormat = dataFormat;
		this.compressType = compressType;
		this.checksumType = checksumType;
		this.status = status;
		try{
		this.archiveTimeLong = new Long(archiveTime.getTime());
		}catch(NullPointerException npe)
		{
			this.archiveTimeLong = null;
		}
		this.rootPath = rootPath;
		this.relPath = relPath;
	}
	
	public Granule(String name,
			Long startTime, Long requestedTime, Long acquiredTime, Long stopTime, Long createTime,	Long ingestTime,
			Integer version,
			String accessType, String dataFormat, String compressType, String checksumType,
			GranuleStatus status,
			Long archiveTime,
			String rootPath, String relPath) {
		super();
		
		this.name = name;
		this.startTimeLong = startTime;
		this.stopTimeLong = stopTime;
		this.createTimeLong = createTime;
		this.ingestTimeLong = ingestTime;
		this.version = version;
		this.requestedTimeLong = requestedTime;
		this.acquiredTimeLong = acquiredTime;
		this.accessType = accessType;
		this.dataFormat = dataFormat;
		this.compressType = compressType;
		this.checksumType = checksumType;
		this.status = status;
		this.archiveTimeLong = archiveTime;
		this.rootPath = rootPath;
		this.relPath = relPath;
	}


	public Integer getGranuleId() {
		return granuleId;
	}

	public void setGranuleId(Integer granuleId) {
		this.granuleId = granuleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	//officialName
//	public String getOfficialName() {
//		return officialName;
//	}
//
//	public void setOfficialName(String name) {
//		this.officialName = name;
//	}
	public String getRootPath() {
                return rootPath;
        }

    public void setRootPath(String rootPath) {
                this.rootPath = rootPath;
    }

    public String getRelPath() {
            return relPath;
    }

    public void setRelPath(String relPath) {
            this.relPath = relPath;
    }

        

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
		this.startTimeLong = new Long(startTime.getTime());
	}

	public Date getStopTime() {
		return stopTime;
	}

	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
		this.stopTimeLong = new Long(stopTime.getTime());
	}
	
	
	public Date getAcquiredTime() {
		return acquiredTime;
	}

	public void setAcquiredTime(Date acquiredTime) {
		this.acquiredTime = acquiredTime;
		this.acquiredTimeLong = new Long(acquiredTime.getTime());
	}
	public Date getRequestedTime() {
		return requestedTime;
	}

	public void setRequestedTime(Date requestedTime) {
		this.requestedTime = requestedTime;
		this.requestedTimeLong = new Long(requestedTime.getTime());
	}
	
	public Date getArchiveTime() {
		return archiveTime;
	}

	public void setArchiveTime(Date archiveTime) {
		this.archiveTime = archiveTime;
		this.archiveTimeLong = new Long(archiveTime.getTime());
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
		this.createTimeLong = new Long(createTime.getTime());
	}

	public Date getIngestTime() {
		return ingestTime;
	}

	public void setIngestTime(Date ingestTime) {
		this.ingestTime = ingestTime;
		this.ingestTimeLong = new Long(ingestTime.getTime());
	}

	public Date getVerifyTime() {
		return verifyTime;
	}

	public void setVerifyTime(Date verifyTime) {
		this.verifyTime = verifyTime;
		this.verifyTimeLong =  new Long(verifyTime.getTime());
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}

	public String getCompressType() {
		return compressType;
	}

	public void setCompressType(String compressType) {
		this.compressType = compressType;
	}
	
	public Set<GranuleSIP> getGranuleSIPSet() {
		return granuleSIPSet;
	}

	public void setGranuleSIPSet(Set<GranuleSIP> granuleSIP) {
		this.granuleSIPSet = granuleSIP;
	}

	public String getChecksumType() {
		return checksumType;
	}

	public void setChecksumType(String checksumType) {
		this.checksumType = checksumType;
	}

	public GranuleStatus getStatus() {
		return status;
	}

	public void setStatus(GranuleStatus status)
	{
		
		if(status != null){
			if(status.equals(GranuleStatus.ONLINE))
			{
				this.setArchiveTime(new Date());
			}
		}
		this.status = status;
	}

	public Set<GranuleCharacter> getGranuleCharacterSet() {
		return granuleCharacterSet;
	}

	public void setGranuleCharacterSet(Set<GranuleCharacter> granuleCharacterSet) {
		this.granuleCharacterSet = granuleCharacterSet;
	}

	public Set<GranuleInteger> getGranuleIntegerSet() {
		return granuleIntegerSet;
	}

	public void setGranuleIntegerSet(Set<GranuleInteger> granuleIntegerSet) {
		this.granuleIntegerSet = granuleIntegerSet;
	}

	public Set<GranuleReal> getGranuleRealSet() {
		return granuleRealSet;
	}

	public void setGranuleRealSet(Set<GranuleReal> granuleRealSet) {
		this.granuleRealSet = granuleRealSet;
	}

	public Set<GranuleDateTime> getGranuleDateTimeSet() {
		return granuleDateTimeSet;
	}

	public void setGranuleDateTimeSet(Set<GranuleDateTime> granuleDateTimeSet) {
		this.granuleDateTimeSet = granuleDateTimeSet;
	}
	
	public Set<GranuleSpatial> getGranuleSpatialSet() {
		return granuleSpatialSet;
	}

	public void setGranuleSpatialSet(Set<GranuleSpatial> granuleSpatialSet) {
		this.granuleSpatialSet = granuleSpatialSet;
	}

	public Set<GranuleArchive> getGranuleArchiveSet() {
		return granuleArchiveSet;
	}

	public void setGranuleArchiveSet(Set<GranuleArchive> granuleArchiveSet) {
		this.granuleArchiveSet = granuleArchiveSet;
	}

	public Set<GranuleReference> getGranuleReferenceSet() {
		return granuleReferenceSet;
	}

	public void setGranuleReferenceSet(Set<GranuleReference> granuleReferenceSet) {
		this.granuleReferenceSet = granuleReferenceSet;
	}

	public Set<GranuleMetaHistory> getMetaHistorySet() {
		return metaHistorySet;
	}

	public void setMetaHistorySet(Set<GranuleMetaHistory> metaHistorySet) {
		this.metaHistorySet = metaHistorySet;
	}

	public Set<GranuleContact> getGranuleContactSet() {
		return granuleContactSet;
	}

	public void setGranuleContactSet(Set<GranuleContact> granuleContactSet) {
		this.granuleContactSet = granuleContactSet;
	}
	//@XmlJavaTypeAdapter(DatasetIDAdapter.class)
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
		result = prime * result + ((dataset == null) ? 0 : dataset.hashCode());
		result = prime * result
				+ ((granuleId == null) ? 0 : granuleId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		final Granule other = (Granule) obj;
		if (dataset == null) {
			if (other.dataset != null)
				return false;
		} else if (!dataset.equals(other.dataset))
			return false;
		if (granuleId == null) {
			if (other.granuleId != null)
				return false;
		} else if (!granuleId.equals(other.granuleId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public void add(GranuleSIP gSip) {
        this.getGranuleSIPSet().add(gSip);
    }
	
	public void add(GranuleCharacter granuleCharacter) {
        this.getGranuleCharacterSet().add(granuleCharacter);
    }

    public void remove(String granuleCharacter) {
        this.getGranuleCharacterSet().remove(granuleCharacter);
    }

	public void add(GranuleInteger granuleInteger) {
        this.getGranuleIntegerSet().add(granuleInteger);
    }

    public void remove(GranuleInteger granuleInteger) {
        this.getGranuleIntegerSet().remove(granuleInteger);
    }
    
	public void add(GranuleReal granuleReal) {
        this.getGranuleRealSet().add(granuleReal);
    }

    public void remove(GranuleReal granuleReal) {
        this.getGranuleRealSet().remove(granuleReal);
    }

	public void add(GranuleDateTime granuleDateTime) {
        this.getGranuleDateTimeSet().add(granuleDateTime);
    }

    public void remove(GranuleDateTime granuleDateTime) {
        this.getGranuleDateTimeSet().remove(granuleDateTime);
    }

	public void add(GranuleSpatial granuleSpatial) {
        this.getGranuleSpatialSet().add(granuleSpatial);
    }

    public void remove(GranuleSpatial granuleSpatial) {
        this.getGranuleSpatialSet().remove(granuleSpatial);
    }

    public void add(GranuleArchive granuleArchive) {
        this.getGranuleArchiveSet().add(granuleArchive);
    }

    public void remove(GranuleArchive granuleArchive) {
        this.getGranuleArchiveSet().remove(granuleArchive);
    }

	public void add(GranuleReference granuleReference) {
		this.getGranuleReferenceSet().add(granuleReference);	
	}
	
    public void remove(GranuleReference granuleReference) {
        this.getGranuleReferenceSet().remove(granuleReference);
    }

	public void add(GranuleMetaHistory granuleMetaHistory) {
        this.getMetaHistorySet().add(granuleMetaHistory);
    }

    public void remove(GranuleMetaHistory granuleMetaHistory) {
        this.getMetaHistorySet().remove(granuleMetaHistory);
    }

	public void add(GranuleContact granuleContact) {
        this.getGranuleContactSet().add(granuleContact);
    }

    public void remove(GranuleContact granuleContact) {
        this.getGranuleContactSet().remove(granuleContact);
    }
    
    public Long getStartTimeLong() {
		return startTimeLong;
	}

	public void setStartTimeLong(Long startTime) {
		this.startTimeLong = startTime;
		if(startTime == null)
			this.startTime = null;
		else
		this.startTime = new Date(startTime);
	}

	public Long getStopTimeLong() {
		return stopTimeLong;
	}

	public void setStopTimeLong(Long stopTime) {
		this.stopTimeLong = stopTime;
		if(stopTime == null)
			this.stopTime = null;
		else
		this.stopTime = new Date(stopTime);
	}
	
	
	public Long getAcquiredTimeLong() {
		return acquiredTimeLong;
	}

	public void setAcquiredTimeLong(Long acquiredTime) {
		this.acquiredTimeLong = acquiredTime;
		if(acquiredTime == null)
			this.acquiredTime = null;
		else
		this.acquiredTime = new Date(acquiredTime);
	}
	public Long getRequestedTimeLong() {
		return requestedTimeLong;
	}

	public void setRequestedTimeLong(Long requestedTime) {
		this.requestedTimeLong = requestedTime;
		if(requestedTime == null)
			this.requestedTime = null;
		else
		this.requestedTime = new Date(requestedTime);
	}
	
	public Long getArchiveTimeLong() {
		return archiveTimeLong;
	}

	public void setArchiveTimeLong(Long archiveTime) {
		this.archiveTimeLong = archiveTime;
		if(archiveTime == null)
			this.archiveTime = null;
		else
			this.archiveTime = new Date(archiveTime);
	}

	public Long getCreateTimeLong() {
		return createTimeLong;
	}

	public void setCreateTimeLong(Long createTime) {
		this.createTimeLong = createTime;
		if(createTime == null)
			this.createTime = null;
		else
		this.createTime = new Date(createTime);
	}

	public Long getIngestTimeLong() {
		return ingestTimeLong;
	}

	public void setIngestTimeLong(Long ingestTime) {
		this.ingestTimeLong = ingestTime;
		if(ingestTime == null)
			this.ingestTime = null;
		else
			this.ingestTime = new Date(ingestTime);
	}

	public Long getVerifyTimeLong() {
		return verifyTimeLong;
	}

	public void setVerifyTimeLong(Long verifyTime) {
		this.verifyTimeLong = verifyTime;
		if(verifyTime == null)
			this.verifyTime = null;
		else
			this.verifyTime = new Date(verifyTime);
	}
	
	public Granule export(){
		Granule g = this;
		if(!org.hibernate.Hibernate.isInitialized(granuleSIPSet)){
			granuleSIPSet = null;
		}
//	    granuleCharacterSet
//	    granuleIntegerSet 
//	    granuleRealSet 
//	    granuleDateTimeSet
//	    granuleSpatialSet 
//	    granuleArchiveSet 
//	    granuleReferenceSet 
//	    metaHistorySet
//	    granuleContactSet 
		g.setDataset(null);
		//org.hibernate.Hibernate.shallowClone();
		return g;
	}
	
	
    //updates the granule inplace.
	//won't change the dataset_id or granule_id.
	public List<String> updateGranule(Granule incoming){
		log.debug("Comparing this, incoming granules.");
		List<String> diffs = new ArrayList<String>();
		
		//granuleId; //don't update this. Bad idea.
		
		if(compareFields(this.name, incoming.getName()) > 0){
			diffs.add("Updating field: name. New value = ["+incoming.getName()+"]");
			this.name = incoming.getName();
		}
		//private String officialName;
		//Dates
		
		if(compareFields(this.startTime, incoming.getStartTime()) > 0){
			diffs.add("Updating field: startTime. New value = ["+incoming.getStartTime()+"]");
			this.startTime = incoming.getStartTime();
		}
		
		if(compareFields(this.stopTime, incoming.getStopTime()) > 0){
			diffs.add("Updating field: stopTime. New value = ["+incoming.getStopTime()+"]");
			this.stopTime= incoming.getStopTime();
		}
		
		if(compareFields(this.requestedTime, incoming.getRequestedTime()) > 0){
			diffs.add("Updating field: RequestTime. New value = ["+incoming.getRequestedTime()+"]");
			this.requestedTime = incoming.getRequestedTime();
		}
		//acquiredTime;
		if(compareFields(this.acquiredTime, incoming.getAcquiredTime()) > 0){
			diffs.add("Updating field: AcquiredTime. New value = ["+incoming.getAcquiredTime()+"]");
			this.acquiredTime = incoming.getAcquiredTime();
		}
		//createTime;
		if(compareFields(this.createTime, incoming.getCreateTime()) > 0){
			diffs.add("Updating field: CreateTime. New value = ["+incoming.getCreateTime()+"]");
			this.createTime = incoming.getCreateTime();
		}
		//ingestTime;
		if(compareFields(this.ingestTime, incoming.getIngestTime()) > 0){
			diffs.add("Updating field: ingestTime. New value = ["+incoming.getIngestTime()+"]");
			this.ingestTime = incoming.getIngestTime();
		}
		
		if(compareFields(this.verifyTime, incoming.getVerifyTime()) > 0){
			diffs.add("Updating field: VerifyTime. New value = ["+incoming.getVerifyTime()+"]");
			this.verifyTime = incoming.getVerifyTime();
		}
		//archiveTime;
		if(compareFields(this.archiveTime, incoming.getArchiveTime()) > 0){
			diffs.add("Updating field: ArchiveTime. New value = ["+incoming.getArchiveTime()+"]");
			this.archiveTime = incoming.getArchiveTime();
		}
		
		if(compareFields(this.startTimeLong, incoming.getStartTimeLong()) > 0){
			diffs.add("Updating field: startTimeLong. New value = ["+incoming.getStartTimeLong()+"]");
			this.startTimeLong = incoming.getStartTimeLong();
		}
		
		if(compareFields(this.stopTimeLong, incoming.getStopTimeLong()) > 0){
			diffs.add("Updating field: stopTimeLong. New value = ["+incoming.getStopTimeLong()+"]");
			this.stopTimeLong= incoming.getStopTimeLong();
		}
		
		if(compareFields(this.requestedTimeLong, incoming.getRequestedTimeLong()) > 0){
			diffs.add("Updating field: RequestTimeLong. New value = ["+incoming.getRequestedTimeLong()+"]");
			this.requestedTimeLong = incoming.getRequestedTimeLong();
		}
		//acquiredTimeLong;
		if(compareFields(this.acquiredTimeLong, incoming.getAcquiredTimeLong()) > 0){
			diffs.add("Updating field: AcquiredTimeLong. New value = ["+incoming.getAcquiredTimeLong()+"]");
			this.acquiredTimeLong = incoming.getAcquiredTimeLong();
		}
		//createTimeLong;
		if(compareFields(this.createTimeLong, incoming.getCreateTimeLong()) > 0){
			diffs.add("Updating field: CreateTimeLong. New value = ["+incoming.getCreateTimeLong()+"]");
			this.createTimeLong = incoming.getCreateTimeLong();
		}
		//ingestTimeLong;
		if(compareFields(this.ingestTimeLong, incoming.getIngestTimeLong()) > 0){
			diffs.add("Updating field: ingestTimeLong. New value = ["+incoming.getIngestTimeLong()+"]");
			this.ingestTimeLong = incoming.getIngestTimeLong();
		}
		
		if(compareFields(this.verifyTimeLong, incoming.getVerifyTimeLong()) > 0){
			diffs.add("Updating field: VerifyTimeLong. New value = ["+incoming.getVerifyTimeLong()+"]");
			this.verifyTimeLong = incoming.getVerifyTimeLong();
		}
		//archiveTimeLong;
		if(compareFields(this.archiveTimeLong, incoming.getArchiveTimeLong()) > 0){
			diffs.add("Updating field: ArchiveTimeLong. New value = ["+incoming.getArchiveTimeLong()+"]");
			this.archiveTimeLong = incoming.getArchiveTimeLong();
		}
		
		//Integer
		if(compareFields(this.version, incoming.getVersion()) > 0){
			diffs.add("Updating field: Version. New value = ["+incoming.getVersion()+"]");
			this.version = incoming.getVersion();
		}
		
		//Strings
		if(compareFields(this.accessType, incoming.getAccessType()) > 0){
			diffs.add("Updating field: AccessType. New value = ["+incoming.getAccessType()+"]");
			this.accessType = incoming.getAccessType();
		}
		if(compareFields(this.dataFormat, incoming.getDataFormat()) > 0){
			diffs.add("Updating field: DataFormat. New value = ["+incoming.getDataFormat()+"]");
			this.dataFormat = incoming.getDataFormat();
		}

		if(compareFields(this.compressType, incoming.getCompressType()) > 0){
			diffs.add("Updating field: CompressType. New value = ["+incoming.getCompressType()+"]");
			this.compressType = incoming.getCompressType();
		}
		
		if(compareFields(this.checksumType, incoming.getChecksumType()) > 0){
			diffs.add("Updating field: ChecksumType. New value = ["+incoming.getChecksumType()+"]");
			this.checksumType = incoming.getChecksumType();
		}
		
		if(compareFields(this.rootPath, incoming.getRootPath()) > 0){
			diffs.add("Updating field: RootPath. New value = ["+incoming.getRootPath()+"]");
			this.rootPath = incoming.getRootPath();
		}
		
		if(compareFields(this.relPath, incoming.getRelPath()) > 0){
			diffs.add("Updating field: RelPath. New value = ["+incoming.getRelPath()+"]");
			this.relPath = incoming.getRelPath();
		}
		
		//GranuleStatus
		if(compareFields(this.status, incoming.getStatus()) > 0){
			diffs.add("Updating field: Status. New value = ["+incoming.getStatus()+"]");
			this.status = incoming.getStatus();
		}
		
		//reals
		for(GranuleReal gr : this.getGranuleRealSet()){
			if(!incoming.getGranuleRealSet().isEmpty()){
				for(GranuleReal grInc : incoming.getGranuleRealSet()){
					if(grInc.getDatasetElement().getDeId().equals(gr.getDatasetElement().getDeId())){
						log.debug("Incoming contains element with deid: " + gr.getDatasetElement().getDeId());
						if(compareFields(gr.getValue(), grInc.getValue()) > 0){
							diffs.add("Updating field: "+grInc.getDatasetElement().getElementDD().getShortName()+". New value = ["+grInc.getValue()+"]");
							gr.setValue(grInc.getValue());
							if(gr.getUnits() != null || !(new String("").equals(gr.getUnits())))
								gr.setUnits(grInc.getUnits());
							continue;
						}
					}
				}
			}
		}
		//ints
		for(GranuleInteger gr : this.getGranuleIntegerSet()){
			if(!incoming.getGranuleIntegerSet().isEmpty()){
				for(GranuleInteger grInc : incoming.getGranuleIntegerSet()){
					if(grInc.getDatasetElement().getDeId().equals(gr.getDatasetElement().getDeId())){
						log.debug("Incoming contains element with deid: " + gr.getDatasetElement().getDeId());
						if(compareFields(gr.getValue(), grInc.getValue()) > 0){
							diffs.add("Updating field: "+grInc.getDatasetElement().getElementDD().getShortName()+". New value = ["+grInc.getValue()+"]");
							gr.setValue(grInc.getValue());
							if(gr.getUnits() != null || !(new String("").equals(gr.getUnits())))
								gr.setUnits(grInc.getUnits());
							continue;
						}
					}
				}
			}
		}
		
		//dts
		for(GranuleDateTime gr : this.getGranuleDateTimeSet()){
			if(!incoming.getGranuleDateTimeSet().isEmpty()){
				for(GranuleDateTime grInc : incoming.getGranuleDateTimeSet()){
					if(grInc.getDatasetElement().getDeId().equals(gr.getDatasetElement().getDeId())){
						log.debug("Incoming contains element with deid: " + gr.getDatasetElement().getDeId());
						if(compareFields(gr.getValueLong(), grInc.getValueLong()) > 0){
							diffs.add("Updating field: "+grInc.getDatasetElement().getElementDD().getShortName()+". New value = ["+grInc.getValueLong()+"]");
							gr.setValueLong(grInc.getValueLong());
						}
					}
				}
			}
		}
		
		//chars
		for(GranuleCharacter gr : this.getGranuleCharacterSet()){
			if(!incoming.getGranuleCharacterSet().isEmpty()){
				for(GranuleCharacter grInc : incoming.getGranuleCharacterSet()){
					if(grInc.getDatasetElement().getDeId().equals(gr.getDatasetElement().getDeId())){
						log.debug("Incoming contains element with deid: " + gr.getDatasetElement().getDeId());
						if(compareFields(gr.getValue(), grInc.getValue()) > 0){
							diffs.add("Updating field: "+grInc.getDatasetElement().getElementDD().getShortName()+". New value = ["+grInc.getValue()+"]");
							gr.setValue(grInc.getValue());
//							if(gr.getUnits() != null || !(new String("").equals(gr.getUnits())))
//								gr.setUnits(grInc.getUnits());
							continue;
						}
					}
				}
			}
		}
		
		return diffs;
		
	}
	
	/*
	 * compare objects. 0 if no change, 1 or -1 if there is a difference.
	 * -1 if the incoming is null,
	 * 1 if the incoming has a different val
	 * Both == null yields a 0. No change is highest precedent.
	 */
	public int compareFields(Object orig, Object inc){
		if(orig == null){
			if(inc == null)
				return 0;
			else
				return 1;
		}else{
			//not null
			if(inc == null)
				return -1;
			else{
				if(orig.equals(inc))
					return 0;
				else
					return 1;
			}
		}
	}
	
	
    
}

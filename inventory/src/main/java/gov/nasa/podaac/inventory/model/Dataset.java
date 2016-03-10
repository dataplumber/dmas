// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;



/**
 * @author clwong
 * @version Jul 26, 2007
 * $Id: Dataset.java 11358 2013-04-01 21:54:05Z gangl $
 */
//@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("serial")
public class Dataset implements Serializable {

	private Integer datasetId;
	private String persistentId;
	private String shortName;
	private String longName;
	private String originalProvider;
	private String providerDatasetName;
	private String processingLevel;
	private String metadata;
	private String note;


	private Integer acrossTrackResolution;
	private Integer alongTrackResolution;
	private Long ascendingNodeTime;
	
	/*
	private Float latitudeResolution;
	private Float longitudeResolution;
	private Float altitudeResolution;
	private Float depthResolution;
	*/
	
	private Float sampleFrequency;
	private Float swathWidth;
	private String temporalRepeat;
	private String temporalRepeatMin;
	private String temporalRepeatMax;
	
	private Double latitudeResolution;
	private Double longitudeResolution;
	private String horizontalResolutionRange;
	private String remoteDataset;
	private String altitudeResolution;
	private String depthResolution;
	private String temporalResolution;
	private String temporalResolutionRange;
	private String ellipsoidType;
	private String projectionType;
	private String projectionDetail;
	private String reference;
	private String description;			// TBD: CLOB

	private Provider provider = new Provider();
	private DatasetPolicy datasetPolicy = new DatasetPolicy();
	private DatasetCoverage datasetCoverage = new DatasetCoverage();

	private Set<DatasetCitation>citationSet = new HashSet<DatasetCitation>();
	private Set<DatasetResource>resourceSet = new HashSet<DatasetResource>();
	private Set<DatasetSoftware> softwareSet = new HashSet<DatasetSoftware>();
	private Set<DatasetLocationPolicy>locationPolicySet =
											new HashSet<DatasetLocationPolicy>();
	private Set<DatasetParameter>parameterSet = new HashSet<DatasetParameter>();
	private Set<DatasetRegion>regionSet = new HashSet<DatasetRegion>();
	private Set<Granule> granuleSet = new HashSet<Granule>();
	private Set<GranuleElement> granuleElementSet = new HashSet<GranuleElement>();
	private Set<DatasetElement> datasetElementSet = new HashSet<DatasetElement>();

	private Set<DatasetVersion> versionSet = new HashSet<DatasetVersion>();
	private Set<DatasetMetaHistory> metaHistorySet = new HashSet<DatasetMetaHistory>();
	private Set<DatasetSource> sourceSet = new HashSet<DatasetSource>();
	private Set<DatasetContact> contactSet = new HashSet<DatasetContact>();
	private Set<CollectionDataset> collectionDatasetSet = new HashSet<CollectionDataset>();
	private Set<DatasetProject> projectSet = new HashSet<DatasetProject>();
	
	private Set<DatasetCharacter> datasetCharacterSet  = new HashSet<DatasetCharacter>();
	private Set<DatasetInteger> datasetIntegerSet = new HashSet<DatasetInteger>();
	private Set<DatasetReal> datasetRealSet = new HashSet<DatasetReal>();
    private Set<DatasetDateTime> datasetDateTimeSet = new HashSet<DatasetDateTime>();

	private Set<DatasetSpatial> datasetSpatial = new HashSet<DatasetSpatial>();

	public Dataset() {}

	//---------------------------------------------------	
	public Integer getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}

	public Integer getAcrossTrackResolution() {
		return acrossTrackResolution;
	}

	public void setAcrossTrackResolution(Integer acrossTrackResolution) {
		this.acrossTrackResolution = acrossTrackResolution;
	}

	public Integer getAlongTrackResolution() {
		return alongTrackResolution;
	}

	public void setAlongTrackResolution(Integer alongTrackResolution) {
		this.alongTrackResolution = alongTrackResolution;
	}

	public Long getAscendingNodeTime() {
		return ascendingNodeTime;
	}

	public void setAscendingNodeTime(Long ascendingNodeTime) {
		this.ascendingNodeTime = ascendingNodeTime;
	}
	
	public String getTemporalRepeat() {
		return temporalRepeat;
	}

	public void setTemporalRepeat(String temporalRepeat) {
		this.temporalRepeat = temporalRepeat;
	}

	public String getTemporalRepeatMin() {
		return temporalRepeatMin;
	}

	public void setTemporalRepeatMin(String temporalRepeatMin) {
		this.temporalRepeatMin = temporalRepeatMin;
	}
	
	public String getTemporalRepeatMax() {
		return temporalRepeatMax;
	}

	public void setTemporalRepeatMax(String temporalRepeatMax) {
		this.temporalRepeatMax = temporalRepeatMax;
	}
	
	//persistentId
	public String getPersistentId() {
		return persistentId;
	}

	public void setPersistentId(String persistentId) {
		this.persistentId = persistentId;
	}
	
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	
	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getOriginalProvider() {
		return originalProvider;
	}

	public void setOriginalProvider(String originalProvider) {
		this.originalProvider = originalProvider;
	}

	public String getProviderDatasetName() {
		return providerDatasetName;
	}

	
	public void setProviderDatasetName(String providerDatasetName) {
		this.providerDatasetName = providerDatasetName;
	}

	public String getProcessingLevel() {
		return processingLevel;
	}

	public void setProcessingLevel(String processingLevel) {
		this.processingLevel = processingLevel;
	}

	public Double getLatitudeResolution() {
		return latitudeResolution;
	}

	public void setLatitudeResolution(Double latitudeResolution) {
		this.latitudeResolution = latitudeResolution;
	}

	public Double getLongitudeResolution() {
		return longitudeResolution;
	}

	public void setLongitudeResolution(Double longitudeResolution) {
		this.longitudeResolution = longitudeResolution;
	}

	public String getHorizontalResolutionRange() {
		return horizontalResolutionRange;
	}

	public void setHorizontalResolutionRange(String horizontalResolutionRange) {
		this.horizontalResolutionRange = horizontalResolutionRange;
	}

	public String getAltitudeResolution() {
		return altitudeResolution;
	}

	public void setAltitudeResolution(String altitudeResolution) {
		this.altitudeResolution = altitudeResolution;
	}

	public boolean isRemote(){
		if(this.getRemoteDataset().equals("R")){
			return true;
		}
		return false;
	}
	
	public String getRemoteDataset() {
		return remoteDataset;
	}

	public void setRemoteDataset(String remoteDataset) {
		this.remoteDataset = remoteDataset;
	}
	
	public String getDepthResolution() {
		return depthResolution;
	}

	public void setDepthResolution(String depthResolution) {
		this.depthResolution = depthResolution;
	}

	public String getTemporalResolution() {
		return temporalResolution;
	}

	public void setTemporalResolution(String temporalResolution) {
		this.temporalResolution = temporalResolution;
	}

	public String getTemporalResolutionRange() {
		return temporalResolutionRange;
	}

	public void setTemporalResolutionRange(String temporalResolutionRange) {
		this.temporalResolutionRange = temporalResolutionRange;
	}

	public String getEllipsoidType() {
		return ellipsoidType;
	}

	public void setEllipsoidType(String ellipsoidType) {
		this.ellipsoidType = ellipsoidType;
	}

	public String getProjectionType() {
		return projectionType;
	}

	public void setProjectionType(String projectionType) {
		this.projectionType = projectionType;
	}

	public String getProjectionDetail() {
		return projectionDetail;
	}

	public void setProjectionDetail(String projectionDetail) {
		this.projectionDetail = projectionDetail;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public DatasetPolicy getDatasetPolicy() {
		return datasetPolicy;
	}

	public void setDatasetPolicy(DatasetPolicy datasetPolicy) {
		this.datasetPolicy = datasetPolicy;
	}

	public DatasetCoverage getDatasetCoverage() {
		return datasetCoverage;
	}

	public void setDatasetCoverage(DatasetCoverage datasetCoverage) {
		this.datasetCoverage = datasetCoverage;
	}

	public Set<DatasetCitation> getCitationSet() {
		
			return citationSet;
		
	}

	public void setCitationSet(Set<DatasetCitation> citationSet) {
		this.citationSet = citationSet;
	}

	public Set<DatasetResource> getResourceSet() {
		return resourceSet;
	}

	public void setResourceSet(Set<DatasetResource> resourceSet) {
		this.resourceSet = resourceSet;
	}

	public Set<DatasetSoftware> getSoftwareSet() {
		return softwareSet;
	}

	public void setSoftwareSet(Set<DatasetSoftware> softwareSet) {
		this.softwareSet = softwareSet;
	}

	public Set<DatasetLocationPolicy> getLocationPolicySet() {
		return locationPolicySet;
	}

	public void setLocationPolicySet(Set<DatasetLocationPolicy> locationPolicySet) {
		this.locationPolicySet = locationPolicySet;
	}

	public Set<DatasetRegion> getRegionSet() {
		return regionSet;
	}

	public void setRegionSet(Set<DatasetRegion> regionSet) {
		this.regionSet = regionSet;
	}
	//regionSet
	public Set<DatasetParameter> getParameterSet() {
		return parameterSet;
	}

	public void setParameterSet(Set<DatasetParameter> parameterSet) {
		this.parameterSet = parameterSet;
	}

	public Set<Granule> getGranuleSet() {
		return granuleSet;
	}

	public void setGranuleSet(Set<Granule> granuleSet) {
		this.granuleSet = granuleSet;
	}

	public Set<GranuleElement> getGranuleElementSet() {
		return granuleElementSet;
	}

	public void setGranuleElementSet(Set<GranuleElement> granuleElementSet) {
		this.granuleElementSet = granuleElementSet;
	}

	
	public Set<DatasetElement> getDatasetElementSet() {
		return datasetElementSet;
	}

	public void setDatasetElementSet(Set<DatasetElement> datasetElementSet) {
		this.datasetElementSet = datasetElementSet;
		
		/*
		 * Here is where we'll set the granule Elements
		 */
		//granuleElementSet = null;
		//granuleElementSet = new HashSet<GranuleElement>();
		
		/*for(DatasetElement x: datasetElementSet){
			if(x.getScope().equals("BOTH") || x.getScope().equals("GRANULE")){
				GranuleElement ge = new GranuleElement();
				ge.setObligationFlag(x.getObligationFlag());
				ge.setDataset(x.getDataset());
				ge.setElementDD(x.getElementDD());
				
				granuleElementSet.add(ge);
			}
		}*/
		
	}
	public Float getSampleFrequency() {
		return sampleFrequency;
	}
	public void setSampleFrequency(Float sampleFrequency) {
		this.sampleFrequency = sampleFrequency;
	}
	public Float getSwathWidth() {
		return swathWidth;
	}
	public void setSwathWidth(Float swathWidth) {
		this.swathWidth = swathWidth;
	}
	public Set<DatasetVersion> getVersionSet() {
		return versionSet;
	}

	public void setVersionSet(Set<DatasetVersion> versionSet) {
		this.versionSet = versionSet;
	}

	public Set<DatasetMetaHistory> getMetaHistorySet() {
		return metaHistorySet;
	}

	public void setMetaHistorySet(Set<DatasetMetaHistory> metaHistorySet) {
		this.metaHistorySet = metaHistorySet;
	}

	public Set<DatasetSource> getSourceSet() {
		return sourceSet;
	}

	public void setSourceSet(Set<DatasetSource> sourceSet) {
		this.sourceSet = sourceSet;
	}

	public Set<DatasetContact> getContactSet() {
		return contactSet;
	}

	public void setContactSet(Set<DatasetContact> contactSet) {
		this.contactSet = contactSet;
	}

	public Set<CollectionDataset> getCollectionDatasetSet() {
		
			return collectionDatasetSet;
		
		
	}

	public void setCollectionDatasetSet(Set<CollectionDataset> collectionDatasetSet) {
		this.collectionDatasetSet = collectionDatasetSet;
	}

	public Set<DatasetProject> getProjectSet() {
		return projectSet;
	}

	public void setProjectSet(Set<DatasetProject> projectSet) {
		this.projectSet = projectSet;
	}

	public Set<DatasetSpatial> getDatasetSpatial() {
		return datasetSpatial;
	}

	public void setDatasetSpatial(Set<DatasetSpatial> datasetSpatial) {
		this.datasetSpatial = datasetSpatial;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime * result
				+ ((longName == null) ? 0 : longName.hashCode());
		result = prime * result
				+ ((shortName == null) ? 0 : shortName.hashCode());
		return result;
	}
	
	public Set<DatasetCharacter> getDatasetCharacterSet() {
		return datasetCharacterSet;
	}

	public void setDatasetCharacterSet(Set<DatasetCharacter> datasetCharacterSet) {
		this.datasetCharacterSet = datasetCharacterSet;
	}

	public Set<DatasetInteger> getDatasetIntegerSet() {
		return datasetIntegerSet;
	}

	public void setDatasetIntegerSet(Set<DatasetInteger> datasetIntegerSet) {
		this.datasetIntegerSet = datasetIntegerSet;
	}

	public Set<DatasetReal> getDatasetRealSet() {
		return datasetRealSet;
	}

	public void setDatasetRealSet(Set<DatasetReal> datasetRealSet) {
		this.datasetRealSet = datasetRealSet;
	}

	public Set<DatasetDateTime> getDatasetDateTimeSet() {
		return datasetDateTimeSet;
	}

	public void setDatasetDateTimeSet(Set<DatasetDateTime> datasetDateTimeSet) {
		this.datasetDateTimeSet = datasetDateTimeSet;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Dataset other = (Dataset) obj;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		if (longName == null) {
			if (other.longName != null)
				return false;
		} else if (!longName.equals(other.longName))
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		return true;
	}

	//---------------------------------------------------
	public void add(DatasetCitation citation) {
		this.getCitationSet().add(citation);
	}

	public void add(DatasetResource resource) {
		this.getResourceSet().add(resource);
	}

	public void add(DatasetSoftware software) {
		this.getSoftwareSet().add(software);
	}

	public void add(DatasetLocationPolicy locationPolicy) {
		this.getLocationPolicySet().add(locationPolicy);
	}

	public void add(DatasetParameter parameter) {
		this.getParameterSet().add(parameter);
	}

	public void add(Granule granule) {
		this.getGranuleSet().add(granule);
	}

	public void add(GranuleElement granuleElement) {
		this.getGranuleElementSet().add(granuleElement);
	}

	public void add(DatasetVersion version) {
		this.getVersionSet().add(version);
	}

	public void add(DatasetMetaHistory metaHistory) {
		this.getMetaHistorySet().add(metaHistory);
	}

	public void add(DatasetSource source) {
		this.getSourceSet().add(source);
	}

	public void add(DatasetContact contact) {
		this.getContactSet().add(contact);
	}

	public void add(CollectionDataset collectionDataset) {
		this.getCollectionDatasetSet().add(collectionDataset);
	}
	
	public String diff(Dataset d){
		StringBuilder sb = new StringBuilder();
		//compare dataset info here
			try{ 
				if(!this.getDatasetId().equals(d.getDatasetId())){
					sb.append("Dataset id changed from "+this.getDatasetId()+" to " + d.getDatasetId() + "\n");
				} 
			}catch(NullPointerException npe){
				if(d.getDatasetId()!=null)
					sb.append("Dataset id set to " + d.getDatasetId() + "\n");
			}
			try{ 
				if(!this.getAcrossTrackResolution().equals(d.getAcrossTrackResolution())){
					sb.append("AcrossTrackResolution changed from "+this.getAcrossTrackResolution()+" to " + d.getAcrossTrackResolution()+"\n");
				} 
			}catch(NullPointerException npe){
				if(d.getAcrossTrackResolution()!=null)
					sb.append("AcrossTrackResolution set to " + d.getAcrossTrackResolution()+"\n");
			}
			try{ 
				if(!this.getAlongTrackResolution().equals(d.getAlongTrackResolution())){
					sb.append("AlongTrackResolution changed from "+this.getAlongTrackResolution()+" to " + d.getAlongTrackResolution()+"\n");
				} 
			}catch(NullPointerException npe){
				if(d.getAlongTrackResolution()!=null)
					sb.append("AlongTrackResolution set to " + d.getAlongTrackResolution()+"\n");
			}
			try{ 
				if(!this.getAscendingNodeTime().equals(d.getAscendingNodeTime())){
					sb.append("AscendingNodeTime changed from "+this.getAscendingNodeTime()+" to " + d.getAscendingNodeTime() +"\n");
				} 
			}catch(NullPointerException npe){
				if(d.getAscendingNodeTime()!=null)
					sb.append("AscendingNodeTime set to " + d.getAscendingNodeTime()+"\n");
			}
			try{
				if(!this.getTemporalRepeat().equals(d.getTemporalRepeat())){
					sb.append("TemporalRepeat changed from "+this.getTemporalRepeat()+" to " + d.getTemporalRepeat()+"\n");
				} 
			}catch(NullPointerException npe){
				if(!d.getTemporalRepeat().equals(""))
					sb.append("TemporalRepeat set to " + d.getTemporalRepeat()+"\n");
			}
			try{ 
				if(!this.getTemporalRepeatMin().equals(d.getTemporalRepeatMin())){
					sb.append("TemporalRepeatMin changed from "+this.getTemporalRepeatMin()+" to " + d.getTemporalRepeatMin()+"\n");
				} 
			}catch(NullPointerException npe){
				if(!d.getTemporalRepeatMin().equals(""))
					sb.append("TemporalRepeatMin set to " + d.getTemporalRepeatMin() + "\n");
			}
			try{ 
				if(!this.getTemporalRepeatMax().equals(d.getTemporalRepeatMax())){
					sb.append("TemporalRepeatMax changed from "+this.getTemporalRepeatMax()+" to " + d.getTemporalRepeatMax()+"\n");
				} 
			}catch(NullPointerException npe){
				if(!d.getTemporalRepeatMax().equals(""))
					sb.append("TemporalRepeatMax set to " + d.getTemporalRepeatMax() + "\n");
			}
			try{ 
				if(!this.getPersistentId().equals(d.getPersistentId())){
					sb.append("PersistentId changed from "+this.getPersistentId()+" to " + d.getPersistentId()+"\n");
				} 
			}catch(NullPointerException npe){
				if(!d.getPersistentId().equals(""))
					sb.append("PersistentId set to " + d.getPersistentId() + "\n");
			}
			try{ 
				if(!this.getShortName().equals(d.getShortName())){
					sb.append("ShortName changed from "+this.getShortName()+" to " + d.getShortName()+"\n");
				} 
			}catch(NullPointerException npe){
				if(!d.getShortName().equals(""))
					sb.append("Dataset id set to " + d.getShortName() + "\n");
			}
			try{ 
				if(!this.getMetadata().equals(d.getMetadata())){
					sb.append("Metadata changed from "+this.getMetadata()+" to " + d.getMetadata()+"\n");
				} 
			}catch(NullPointerException npe){
				if(!d.getMetadata().equals(""))
					sb.append("Metadata set to " + d.getMetadata() + "\n");
			}
			try{ 
				if(!this.getLongName().equals(d.getLongName())){
					sb.append("LongName changed from "+this.getLongName()+" to " + d.getLongName()+"\n");
				} 
			}catch(NullPointerException npe){
				if(!d.getLongName().equals(""))
					sb.append("LongName set to " + d.getLongName() + "\n");
			}
			try{ 
				if(!this.getOriginalProvider().equals(d.getOriginalProvider())){
					sb.append("OriginalProvider changed from "+this.getOriginalProvider()+" to " + d.getOriginalProvider()+"\n");
				} 
			}catch(NullPointerException npe){
				if(!d.getOriginalProvider().equals(""))
					sb.append("OriginalProvider set to " + d.getOriginalProvider() + "\n");
			}
			try{ 
				if(!this.getProviderDatasetName().equals(d.getProviderDatasetName())){
					sb.append("ProviderDatasetName changed from "+this.getProviderDatasetName()+" to " + d.getProviderDatasetName()+"\n");
				} 
			}catch(NullPointerException npe){
				if(!d.getProviderDatasetName().equals(""))
					sb.append("ProviderDatasetName set to " + d.getProviderDatasetName() + "\n");
			}
			try{ 
				if(!this.getProcessingLevel().equals(d.getProcessingLevel())){
					sb.append("ProcessingLevel changed from "+this.getProcessingLevel()+" to " + d.getProcessingLevel()+"\n");
				} 
			}catch(NullPointerException npe){
				if(!d.getProcessingLevel().equals(""))
					sb.append("ProcessingLevel set to " + d.getProcessingLevel() + "\n");
			}
			try{ 
				if(!this.getLatitudeResolution().equals(d.getLatitudeResolution())){
					sb.append("LatitudeResolution changed from "+this.getLatitudeResolution()+" to " + d.getLatitudeResolution()+"\n");
				} 
			}catch(NullPointerException npe){
				if(d.getLatitudeResolution()!=null)
					sb.append("LatitudeResolution set to " + d.getLatitudeResolution() + "\n");
			}
			try{ 
				if(!this.getLongitudeResolution().equals(d.getLongitudeResolution())){
					sb.append("LongitudeResolution changed from "+this.getLongitudeResolution()+" to " + d.getLongitudeResolution()+"\n");
				} 
			}catch(NullPointerException npe){
				if(d.getLongitudeResolution()!=null)
					sb.append("LongitudeResolution set to " + d.getLongitudeResolution() + "\n");
			}
			try{ 
				if(!this.getHorizontalResolutionRange().equals(d.getHorizontalResolutionRange())){
					sb.append("HorizontalResolutionRange changed from "+this.getHorizontalResolutionRange()+" to " + d.getHorizontalResolutionRange()+"\n");
				} 
			}catch(NullPointerException npe){
				if(d.getHorizontalResolutionRange()!=null){
					if(!d.getHorizontalResolutionRange().equals(""))
						sb.append("HorizontalResolutionRange set to " + d.getHorizontalResolutionRange() + "\n");
				}
			}
			try{ 
				if(!this.getAltitudeResolution().equals(d.getAltitudeResolution())){
					sb.append("AltitudeResolution changed from "+this.getAltitudeResolution()+" to " + d.getAltitudeResolution()+"\n");
				} 
			}catch(NullPointerException npe){
				if(d.getAltitudeResolution()!=null){
					if(!d.getAltitudeResolution().equals(""))
						sb.append("AltitudeResolution set to " + d.getAltitudeResolution() + "\n");
				}
			}
			try{ 
				if(!this.getRemoteDataset().equals(d.getRemoteDataset())){
					sb.append("RemoteDataset changed from "+this.getRemoteDataset()+" to " + d.getRemoteDataset()+"\n");
				} 
			}catch(NullPointerException npe){
				if(!d.getRemoteDataset().equals(""))
					sb.append("RemoteDataset set to " + d.getRemoteDataset() + "\n");
			}
			try{ 
				if(!this.getDepthResolution().equals(d.getDepthResolution())){
					sb.append("DepthResolution changed from "+this.getDepthResolution()+" to " + d.getDepthResolution()+"\n");
				} 
			}catch(NullPointerException npe){
				if(d.getDepthResolution() != null){
					if(!d.getDepthResolution().equals(""))
						sb.append("DepthResolution set to " + d.getDepthResolution() + "\n");
				}
			}
			try{ 
				if(!this.getTemporalResolution().equals(d.getTemporalResolution())){
					sb.append("TemporalResolution changed from "+this.getTemporalResolution()+" to " + d.getTemporalResolution()+"\n");
				} 
			}catch(NullPointerException npe){
				if(d.getTemporalResolution() != null){
					if(!d.getTemporalResolution().equals(""))
						sb.append("TemporalResolution set to " + d.getTemporalResolution() + "\n");
				}
			}
			try{ 
				if(!this.getTemporalResolutionRange().equals(d.getTemporalResolutionRange())){
					sb.append("TemporalResolutionRange changed from "+this.getTemporalResolutionRange()+" to " + d.getTemporalResolutionRange()+"\n");
				} 
			}catch(NullPointerException npe){
				if(d.getTemporalResolutionRange()!=null){
					if(!d.getTemporalResolutionRange().equals(""))
					sb.append("TemporalResolutionRange set to " + d.getTemporalResolutionRange() + "\n");
				}
			}
			try{ 
				if(!this.getEllipsoidType().equals(d.getEllipsoidType())){
					sb.append("EllipsoidType changed from "+this.getEllipsoidType()+" to " + d.getEllipsoidType()+"\n");
				} 
			}catch(NullPointerException npe){
				if(d.getEllipsoidType()!=null){
					if(!d.getEllipsoidType().equals(""))
						sb.append("EllipsoidType set to " + d.getEllipsoidType() + "\n");
				}
			}
			try{ 
				if(!this.getProjectionType().equals(d.getProjectionType())){
					sb.append("ProjectionType changed from "+this.getProjectionType()+" to " + d.getProjectionType()+"\n");
				} 
			}catch(NullPointerException npe){
				if(d.getProjectionType()!=null){
						if(!d.getProjectionType().equals(""))
					sb.append("ProjectionType set to " + d.getProjectionType() + "\n");
				}
			}
			try{ 
				if(!this.getProjectionDetail().equals(d.getProjectionDetail())){
					sb.append("ProjectionDetail changed from "+this.getProjectionDetail()+" to " + d.getProjectionDetail()+"\n");
				} 
			}catch(NullPointerException npe){
				if(d.getProjectionDetail()!=null){
					if(!d.getProjectionDetail().equals(""))
						sb.append("ProjectionDetail set to " + d.getProjectionDetail() + "\n");
				}
			}
			try{ 
				if(!this.getReference().equals(d.getReference())){
					sb.append("Reference changed from "+this.getReference()+" to " + d.getReference()+"\n");
				} 
			}catch(NullPointerException npe){
				if(!d.getReference().equals(""))
					sb.append("Reference set to " + d.getReference() + "\n");
			}
			try{ 
				if(!this.getDescription().equals(d.getDescription())){
					sb.append("Description changed from "+this.getDescription()+" to " + d.getDescription()+"\n");
				} 
			}catch(NullPointerException npe){
				if(!d.getDescription().equals(""))
					sb.append("Description set to " + d.getDescription() + "\n");
			}
			try{ 
				if(!this.getSampleFrequency().equals(d.getSampleFrequency())){
					sb.append("SampleFrequency changed from "+this.getSampleFrequency()+" to " + d.getSampleFrequency()+"\n");
				} 
			}catch(NullPointerException npe){
				if(d.getSampleFrequency()!=null)
					sb.append("SampleFrequency set to " + d.getSampleFrequency() + "\n");
			}
			try{ 
				if(!this.getSwathWidth().equals(d.getSwathWidth())){
					sb.append("SwathWidth changed from "+this.getSwathWidth()+" to " + d.getSwathWidth()+"\n");
				} 
			}catch(NullPointerException npe){
				if(d.getSwathWidth()!=null)
					sb.append("SwathWidth set to " + d.getSwathWidth() + "\n");
			}
		return sb.toString();
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getNote() {
		return note;
	}
	
}

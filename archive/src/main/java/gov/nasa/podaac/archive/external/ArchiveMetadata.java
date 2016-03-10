//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.archive.external;

import gov.nasa.podaac.common.api.util.StringUtility;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class contains rolling store information.
 *
 * @author clwong
 * $Id: ArchiveMetadata.java 5680 2010-08-26 17:24:57Z niessner $
 */
public class ArchiveMetadata {

	private String dataClass;
	private Integer dataDuration;
	private Map<String, String> remoteBaseLocation = new TreeMap<String, String>();
	private Map<String, String> localBaseLocation = new TreeMap<String, String>();
	private List<String> archivePathList = new ArrayList<String>();
	private Dataset dataset;
	private Granule granule;
	
	public String getDataClass() {
		return dataClass;
	}
	public void setDataClass(String dataClass) {
		this.dataClass = dataClass;
	}
	public Integer getDataDuration() {
		return dataDuration;
	}
	public void setDataDuration(Integer dataDuration) {
		this.dataDuration = dataDuration;
	}
	public Map<String, String> getRemoteBaseLocation() {
		return remoteBaseLocation;
	}
	public void setRemoteBaseLocation(Map<String, String> remoteBaseLocation) {
		this.remoteBaseLocation = remoteBaseLocation;
	}
	public Map<String, String> getLocalBaseLocation() {
		return localBaseLocation;
	}
	public void setLocalBaseLocation(Map<String, String> localBaseLocation) {
		this.localBaseLocation = localBaseLocation;
	}
	public List<String> getArchivePathList() {
		return archivePathList;
	}
	public void setArchivePathList(List<String> archivePathList) {
		this.archivePathList = archivePathList;
	}
	public Dataset getDataset() {
		return dataset;
	}
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	public Granule getGranule() {
		return granule;
	}
	public void setGranule(Granule granule) {
		this.granule = granule;
	}
	
	public long getGranuleSpace(List<String> dels) {
		
		long size = 0;
		Set<GranuleArchive> archiveSet = granule.getGranuleArchiveSet();
		for (GranuleArchive archive : archiveSet) {
			
			for(String d : dels){
			if(archive.getName().equals(d))
				size += archive.getFileSize();
			}
		}
		return size;
	}
	
	public String printGranuleReferences() {
		String printString = "\n\n----- Granule References [ID="+granule.getGranuleId()+"] -----"
							+"\ntype:path:status\n----------------";
		Set<GranuleReference> refSet = granule.getGranuleReferenceSet();
		for (GranuleReference ref : refSet) {
			printString += "\n"+ref.getType()+":"+ref.getPath()+":"+ref.getStatus();
		}
		return printString;
	}
	
	public String printGranuleArchives() {
		String printString = "\n\n----- Granule Archives [ID="+granule.getGranuleId()+"] -----"
							+"\ntype:path:status\n----------------";
		Set<GranuleArchive> archiveSet = granule.getGranuleArchiveSet();
		for (GranuleArchive archive : archiveSet) {
			printString += "\n"+archive.getType()+":"+ StringUtility.cleanPaths(granule.getRootPath(), granule.getRelPath(),archive.getName())+":"+archive.getStatus();
		}
		return printString;
	}
}

package gov.nasa.podaac.archive.external;

import gov.nasa.podaac.archive.core.AIP;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleReference;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface InventoryQuery
{
    public Dataset fetchDataset (Integer id, String...properties);//done
    public List<String> findDatasetByProductId (int id); //done
    public List<Integer> findGranuleList (List<Integer> idList); //list of granule Ids, added to Inventory Service and working
    
    public List<AIP> getArchiveAIPByDataset (Integer datasetId); //in inventory service, returns maps
    public List<AIP> getArchiveAIPByGranule(List<Integer> idList);//return maps
    
    public String getArchiveBaseLocation (Integer datasetId);
    public String getArchiveBaseLocation (String datasetName);
    public Map<Integer, String> getArchiveBaseLocation (List<Integer> granuleIdList);
    public ArchiveMetadata getDatasetMetadata(Integer datasetId);
    
    public Map<Integer, ArchiveMetadata> getDatasetMetadata(List<Integer> granuleIdList);
    public List<Integer> getGranuleIdList(Integer datasetId);
    public List<Integer> getGranuleIdList(Integer datasetId, Calendar start, Calendar stop);
    
    public ArchiveMetadata getGranuleMetadata(Integer granuleId);
    public Integer getGranuleSizeByDataset(Integer datasetId);
    
    public Integer getGranuleVersion (Integer id);
    public String getOnlineDataFilePath(Granule granule);
    
    public List<AIP> getReferenceAIPByDataset (Integer datasetId);
    public List<AIP> getReferenceAIPByGranule(List<Integer> idList);
    
    public Set<GranuleReference> getReferenceSet(Integer id);
    public Map<Integer, ArchiveMetadata> getRollingStore();
    
    public ArchiveMetadata getRollingStore(Integer datasetId);
    public Map<Integer, ArchiveMetadata> getRollingStore(List<Integer> granuleIdList);
   
    
    public Granule fetchGranule(Integer id);
    public Dataset fetchDatasetByPersistentId(String perId);//done
    public List<Granule> locateGranules(Integer Id, String pattern, Long startTime, Long stopTime);
	public List<Integer> fetchGranulesByDatasetAndPatter(Integer datasetId, String gnp);
}

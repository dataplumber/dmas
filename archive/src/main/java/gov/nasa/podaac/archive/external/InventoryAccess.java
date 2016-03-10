package gov.nasa.podaac.archive.external;

import gov.nasa.podaac.archive.core.AIP;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.Granule;

import java.util.List;
import java.util.Map;

public interface InventoryAccess
{
    public void deleteGranule(Granule granule, boolean dataOnly);
    public void remoteGranule (Granule granule);
    public void setGranuleReference(Granule granule, Map<String, String> baseLocationPolicy, String subpath);
    public void updateAIPArchiveStatus (List<AIP> aipList);
    public void updateAIPGranuleArchiveStatus (List<AIP> aipList);
    public void updateAIPGranuleReferenceStatus (List<AIP> aipList);
    public void updateGranuleArchiveChecksum(Integer granuleId,String name, String Sum);
    public void updateGranuleArchiveSize (Integer granuleId,String name, Long Size);
    public void updateGranuleArchiveStatus (Integer granuleId, String status);
    public void updateGranuleLocation (Granule granule, String archiveBaseLocation, String baseLocation);
    public boolean updateLocalReference(Granule granule, Map<String, String> localBaseLocationPolicy);
    public void updateVerifyGranuleStatus (List<Integer> granuleIdList, String status);
	public void updateGranuleInfo(Granule g);
	public void updateGranuleReferencePath(Integer granuleId, String path,
			String newRef);
	public void reassociateGranuleElement(Integer key, Integer deId, Integer deId2, String type);
	public void reElement(Granule g, Dataset toD, Dataset fromD);
}

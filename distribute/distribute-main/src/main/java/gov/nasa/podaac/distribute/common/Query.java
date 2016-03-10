//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.distribute.common;

import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.model.CollectionProduct;
import gov.nasa.podaac.inventory.model.Contact;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory;
import gov.nasa.podaac.inventory.model.GranuleSpatial;
import gov.nasa.podaac.inventory.model.Provider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains query methods to the database via Inventory query api.
 *
 * @author clwong
 *
 * @version
 * $Id: Query.java 14484 2015-11-25 00:18:25Z gangl $
 */
public interface Query {
	
	public Collection fetchCollectionById(Integer id);
	public  List<Collection> getCollectionList();

	public List<Collection> getVisibleCollectionList ();
	
	public boolean isVisible(Collection collection);
	
	public  List<Granule> listGranuleByDateRange(long begin, long stop, String timefield);
	public List<Granule> listGranuleByDateRange(long begin, long stop,
			String timefield, Integer datasetId);
	
	public  Collection fetchCollection (Integer collectionId);
	
	public  Dataset fetchDataset (Integer datasetId);
	public Dataset fetchDataset(String datasetId);
	
	public  Dataset fetchDataset (Integer datasetId, String... joinProperties);
	
	public  Contact fetchContact (Integer contactId, String... joinProperties);
	
	public  List<Integer> getGranuleIdList(Integer datasetId);
	
	public  Granule fetchGranule(Integer granuleId);
	public Dataset fetchDatasetById(Integer datasetId);
	public CollectionProduct findCollectionProductById(Integer collectionId);
	public List<BigDecimal> getResultList(Integer datasetId);
	public Granule fetchGranuleByIdDeep(Integer granuleId);
	public void updateGranuleEchoSubmitTime(GranuleMetaHistory gmh);
	public void updateCollectionProduct(CollectionProduct cp);
	public Dataset findDatasetByShortNameDeep(String shortName);
	public Dataset findDatasetByPersistentId(String shortName);
	public Provider fetchProviderById(int providerId);
	public Dataset fetchDatasetById(int datasetId, String ...props);
	public Granule findGranuleByName(String gName);
	public ElementDD fetchElementDDById(Integer elementId);
	public ElementDD findElementDDByShortName(String eName);
	public GranuleSpatial fetchSpatial(Granule g);
	public Collection fetchCollectionByDataset(Dataset d);
	public void updateGranuleEchoSubmitTime(Granule granule, Date date);
	
}

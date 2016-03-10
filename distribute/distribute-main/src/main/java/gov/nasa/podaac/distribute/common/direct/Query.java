//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.distribute.common.direct;

import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.model.CollectionDataset;
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

import thredds.catalog2.Access;

/**
 * This class contains query methods to the database via Inventory query api.
 *
 * @author clwong
 *
 * @version
 * $Id: Query.java 2385 2008-12-10 22:32:09Z clwong $
 */
public class Query implements gov.nasa.podaac.distribute.common.Query{
	
	private static Log log = LogFactory.getLog(Query.class);
	private gov.nasa.podaac.inventory.api.Query query = QueryFactory.getInstance().createQuery();
	public List<Collection> getCollectionList () {
		gov.nasa.podaac.inventory.api.Query query = QueryFactory.getInstance().createQuery();
		return query.listCollection();
	}

	public Collection fetchCollectionByDataset(Dataset d){
		Integer id = null;
		for(CollectionDataset cd : d.getCollectionDatasetSet()){
			if(id == null)
				id = cd.getCollectionDatasetPK().getCollection().getCollectionId();
			else if(cd.getCollectionDatasetPK().getCollection().getCollectionId() > id)
				id = cd.getCollectionDatasetPK().getCollection().getCollectionId();
		}
		log.debug("Finding collection with id: " + id);
		Collection c = query.fetchCollectionById(id, "collectionDatasetSet","collectionDataset");
		return c;
	}
	
	
	public List<Collection> getVisibleCollectionList () {
		List<Collection> collectionList = query.listCollection();
		List<Collection> visibleCollectionList = new ArrayList<Collection>();
		for (Collection collection : collectionList) {
			Collection coll = query.fetchCollectionById(collection.getCollectionId());
			if (!coll.getType().equalsIgnoreCase("PRODUCT")) continue;
			if (this.isVisible(coll)) visibleCollectionList.add(coll);
		}		
		return visibleCollectionList;
	}
	
	public boolean isVisible(Collection collection) {
		if (collection == null) return false;
		if (collection.getCollectionProduct() == null) return false;
		else {
			try {
			   if (collection.getCollectionProduct().getVisibleFlag().equals('Y')) return true;
			   else return false;
			} catch (org.hibernate.LazyInitializationException ex) {
			   log.warn("Collection Id: "+collection.getCollectionId()+":"+collection.getShortName()+" has no product.");
			   return false;
			}
		}
	}
	
	public List<Granule> listGranuleByDateRange(long begin, long stop, String timefield){
		return query.listGranuleByDateRange(begin, stop, timefield);
	}
	
	public Collection fetchCollection (Integer collectionId) {
		return query.fetchCollectionById(collectionId);
	}
	
	public Dataset fetchDataset (Integer datasetId) {
		return query.fetchDatasetById(datasetId);	
	}
	
	public Dataset fetchDataset (String datasetId) {
		return query.fetchDatasetById(Integer.valueOf(datasetId));	
	}
	
	public Dataset fetchDataset (Integer datasetId, String... joinProperties) {
		return query.fetchDatasetById(datasetId, joinProperties);	
	}
	
	public Contact fetchContact (Integer contactId, String... joinProperties) {
		return query.fetchContactById(contactId, joinProperties);	
	}
	
	public List<Integer> getGranuleIdList(Integer datasetId) {
		return query.listGranuleId("dataset_id="+datasetId);	
	}
	
	public Granule fetchGranule(Integer granuleId) {
		return query.fetchGranuleById(granuleId);
	}

	@Override
	public Collection fetchCollectionById(Integer id) {
		return query.fetchCollectionById(id);
	}

	@Override
	public Dataset fetchDatasetById(Integer datasetId) {
		return query.fetchDatasetById(datasetId);
	}

	@Override
	public CollectionProduct findCollectionProductById(Integer collectionId) {
		return query.findCollectionProductById(collectionId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BigDecimal> getResultList(Integer datasetId) {
		return (List<java.math.BigDecimal>) query.getResultList("select granule_id from (select granule_id,version_id from (select granule_id, version_id, max(version_id) over (partition by granule_id) max_version from   granule_meta_history where (LAST_REVISION_DATE_LONG > ECHO_SUBMIT_DATE_LONG OR ECHO_SUBMIT_DATE_LONG is null)) where version_id = max_version AND granule_id in (select granule_id from granule where DATASET_ID="+datasetId +"))");
	}

	@Override
	public Granule fetchGranuleByIdDeep(Integer granuleId) {
		return query.fetchGranuleByIdDeep(granuleId,"ElementDD","GranuleSpatialSet","GranuleSpatial","granuleElementSet","granuleDateTimeSet","granuleRealSet","granuleCharacterSet","granuleCharacter","GranuleMetaHistorySet");
	}

	@Override
	public void updateGranuleEchoSubmitTime(GranuleMetaHistory gmh) {
		query.updateGranuleEchoSubmitTime(gmh);
		
	}

	@Override
	public void updateCollectionProduct(CollectionProduct cp) {
		query.updateCollectionProduct(cp);
	}

	@Override
	public Dataset findDatasetByShortNameDeep(String shortName) {
		return query.findDatasetByShortNameDeep(shortName);
	}

	@Override
	public Dataset findDatasetByPersistentId(String shortName) {
		return query.findDatasetByPersistentId(shortName);
	}

	public Provider fetchProviderById(int providerId) {
		return query.fetchProviderById(providerId);
	}

	public Dataset fetchDatasetById(int datasetId, String ...props) {
		// return 
		return query.fetchDatasetById(datasetId, props);
	}

	@Override
	public List<Granule> listGranuleByDateRange(long begin, long stop,
			String timefield, Integer datasetId) {
		return query.listGranuleByDateRange(begin, stop, timefield, datasetId);
	}

	@Override
	public Granule findGranuleByName(String gName) {
		return query.findGranuleByName(gName);
	}

	@Override
	public ElementDD fetchElementDDById(Integer elementId) {
		return query.fetchElementDDById(elementId, "datasetElementSet");
	}

	@Override
	public ElementDD findElementDDByShortName(String eName) {
		return query.findElementDDByShortName(eName);
	}

	@Override
	public GranuleSpatial fetchSpatial(Granule g) {
		
		for(GranuleSpatial gs: g.getGranuleSpatialSet()){
			return gs;
		}
		
		return null;
	}

	@Override
	public void updateGranuleEchoSubmitTime(Granule granule, Date date) {
		// TODO Auto-generated method stub
		
	}
}

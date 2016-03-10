package gov.nasa.podaac.distribute.common.wsm;

import gov.nasa.podaac.distribute.common.QueryFactory;
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
import gov.nasa.podaac.inventory.api.ServiceFactory;
import gov.nasa.podaac.inventory.api.Service;
import gov.nasa.podaac.inventory.exceptions.InventoryException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Query implements gov.nasa.podaac.distribute.common.Query {

	private Service iws;
	private String URI;
	private String USER;
	private String PASS;
	private Integer PORT;
	private static Log log = LogFactory.getLog(Query.class);
	
	public Query(){
		URI = System.getProperty("inventory.ws.url");
		USER = System.getProperty("inventory.ws.user");
		PASS = System.getProperty("inventory.ws.password");
		try{
			PORT = Integer.valueOf(System.getProperty("inventory.ws.port"));
		}catch(Exception e){PORT = 8443;}
		log.debug("Creating IWS with URI, USER, PASS: " + URI +", " + USER + ", "+ PASS);
		iws = ServiceFactory.getInstance().createService(URI, PORT);
		iws.setAuthInfo(USER, PASS);
	}
	
	@Override
	public Collection fetchCollectionById(Integer id) {
		Collection c = null;
		try {
			c = iws.getCollectionById(id);
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		return c;
	}

	public Collection fetchCollectionByDataset(Dataset d){
		Collection c = null;
		try{
			c = iws.getCollectionByDataset(d.getDatasetId());
		}catch(InventoryException e){
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		
		return c;

	}
	
	@Override
	public List<Collection> getCollectionList() {
		try{
			List<Collection> cList = iws.listCollections();
			return cList;
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		return null;
	}

	@Override
	public List<Collection> getVisibleCollectionList() {
		List<Collection> lst;
		List<Collection> ret = new ArrayList<Collection>();
		lst = getCollectionList();
		for(Collection c : lst){
			c = fetchCollectionById(c.getCollectionId());
			if(Character.toUpperCase(c.getCollectionProduct().getVisibleFlag()) == 'Y')
				ret.add(c);
		}
		return ret;
	}

	@Override
	public boolean isVisible(Collection collection) {
		if (collection == null) return false;
		if (collection.getCollectionProduct() == null) return false;
		else {
			if (collection.getCollectionProduct().getVisibleFlag().equals('Y')) return true;
			   else return false;
		}
	}

	@Override
	public List<Granule> listGranuleByDateRange(long begin, long stop,
			String timefield) {
		
		List<Integer> resp = null;
		
		try{
			
			resp = iws.getGranuleIdListByDateRange(begin, stop, timefield);
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		List<Granule> gList = new ArrayList<Granule>();
		
		for(Integer i: resp){
			Granule g = this.fetchGranule(i);
			gList.add(g);
		}
		
		return gList;
	}
	
	@Override
	public List<Granule> listGranuleByDateRange(long begin, long stop,
			String timefield, Integer datasetId) {
		List<Integer> resp = null;
		try{
			resp = iws.getGranuleIdListByDateRange(begin, stop, timefield, datasetId);
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		List<Granule> gList = new ArrayList<Granule>();
		
		for(Integer i: resp){
			Granule g = this.fetchGranule(i);
			gList.add(g);
		}
		
		return gList;
	}

	@Override
	public Collection fetchCollection(Integer collectionId) {
		return fetchCollectionById(collectionId);
		
	}

	@Override
	public Dataset fetchDataset(Integer datasetId) {
		Dataset d = null;
		try {
			d = iws.getDataset(datasetId);
			//d.setDatasetPolicy(iws.getDatasetPolicy(datasetId));
			d.setProvider(iws.getProvider(d.getProvider().getProviderId()));
			//d.setDatasetCoverage(iws.getDatasetCoverage(datasetId));
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		return d;
	}
	
	@Override
	public Dataset fetchDataset(String datasetId) {
		Dataset d = null;
		try {
			d = iws.getDataset(datasetId);
			//d.setDatasetPolicy(iws.getDatasetPolicy(datasetId));
			d.setProvider(iws.getProvider(d.getProvider().getProviderId()));
			//d.setDatasetCoverage(iws.getDatasetCoverage(datasetId));
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		return d;
	}

	//this one i'll need to see what's needed, and grab it myself.
	@Override
	public Dataset fetchDataset(Integer datasetId, String... joinProperties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contact fetchContact(Integer contactId, String... joinProperties) {
		try {
			return iws.getContactByID(contactId);
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		return null;
	}

	@Override
	public List<Integer> getGranuleIdList(Integer datasetId) {
		
		try {
			return iws.getGranuleIdListAll(datasetId, null, null, null);
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		return null;
	}

	@Override
	public Granule fetchGranule(Integer granuleId) {
		try {
			return iws.getGranuleById(granuleId);
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		return null;
	}

	@Override
	public Dataset fetchDatasetById(Integer datasetId) {
		try {
			return iws.getDataset(datasetId);
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		return null;
	}

	@Override
	public CollectionProduct findCollectionProductById(Integer collectionId) {
		try{
			return iws.getCollectionById(collectionId).getCollectionProduct();
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		return null;
	}

	@Override
	public List<BigDecimal> getResultList(Integer datasetId) {
		try {
			return iws.getEchoGranulesByDataset(datasetId);
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		
		return null;
	}

	@Override
	public Granule fetchGranuleByIdDeep(Integer granuleId) {
		try {
				
			return iws.getGranuleById(granuleId);
			
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		
		return null;
	}

	@Override
	public void updateGranuleEchoSubmitTime(GranuleMetaHistory gmh) {
		
		
		// TODO Auto-generated method stub
		//implement in service
		
	}

	@Override
	public void updateCollectionProduct(CollectionProduct cp) {
		//this is to update the echo submit time
		//implement in service
		//TODO stuff
	}

	@Override
	public Dataset findDatasetByShortNameDeep(String shortName) {
		Dataset d = null;
		try {
			
			d = iws.getDataset(shortName);
			d.setDatasetPolicy(iws.getDatasetPolicy(d.getDatasetId()));
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		return d;
	}

	@Override
	public Dataset findDatasetByPersistentId(String pId) {
		try {
			return iws.getDataset(pId);
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		return null;
	}

	@Override
	public Provider fetchProviderById(int providerId) {
		Provider p = null;
		try {
			
			Set<Contact> cset = new HashSet<Contact>();
			p = iws.getProvider(providerId);
			for(Contact c: p.getContactSet()){

				Contact con = iws.getContactByID(c.getContactId());
				log.debug("First: " + con.getFirstName());
				cset.add(con);
				
			}
			log.debug("number of queried contacts: "+cset.size());
			p.setContactSet(null);
			p.setContactSet(cset);
			
			log.debug("number of provider contacts: "+p.getContactSet().size());
			
//			for(Contact con: cset)
//				p.getContactSet().add(con);
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		return p;
	}

	@Override
	public Dataset fetchDatasetById(int datasetId, String... props) {
		return fetchDatasetById(datasetId);		
	}

	@Override
	public Granule findGranuleByName(String gName) {
		try{
			return iws.getGranuleByName(gName);
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		return null;
	}

	@Override
	public ElementDD fetchElementDDById(Integer elementId) {
		log.debug("Fetching Element: " + elementId);
		
		try{
			return iws.getElementDDById(elementId);
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		return null;
	}

	@Override
	public ElementDD findElementDDByShortName(String eName) {
		try{
			return iws.getElementDDByShortName(eName);
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		return null;
	}

	@Override
	public GranuleSpatial fetchSpatial(Granule g) {
		GranuleSpatial gs = null;
		
		try {
			gs = iws.getSpatial(g.getGranuleId());
		} catch (InventoryException e) {
			log.error("Error executing query: " + e.getMessage(), e);
			log.error("Aborting processing.");
			System.exit(400);
		}
		
		return gs;
	}

	@Override
	public void updateGranuleEchoSubmitTime(Granule granule, Date date) {
		// TODO Auto-generated method stub
		//iws
		try {
			iws.updateGranuleEcho(granule, date.getTime());
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			log.error("Error executing echoUpdate for granule ["+granule.getGranuleId()+"]", e);
		}

	}
}

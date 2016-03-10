package gov.nasa.podaac.inventory.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nasa.podaac.common.api.metadatamanifest.Constant;
import gov.nasa.podaac.common.api.metadatamanifest.MetadataField;
import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifest;
import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifestException;
import gov.nasa.podaac.inventory.api.DataManager;
import gov.nasa.podaac.inventory.api.DataManagerFactory;
import gov.nasa.podaac.inventory.api.MetadataObject;
import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.model.CollectionDataset;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetCharacter;
import gov.nasa.podaac.inventory.model.DatasetCitation;
import gov.nasa.podaac.inventory.model.DatasetContact;
import gov.nasa.podaac.inventory.model.DatasetCoverage;
import gov.nasa.podaac.inventory.model.DatasetDateTime;
import gov.nasa.podaac.inventory.model.DatasetElement;
import gov.nasa.podaac.inventory.model.DatasetInteger;
import gov.nasa.podaac.inventory.model.DatasetLocationPolicy;
import gov.nasa.podaac.inventory.model.DatasetMetaHistory;
import gov.nasa.podaac.inventory.model.DatasetParameter;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.DatasetProject;
import gov.nasa.podaac.inventory.model.DatasetReal;
import gov.nasa.podaac.inventory.model.DatasetRegion;
import gov.nasa.podaac.inventory.model.DatasetResource;
import gov.nasa.podaac.inventory.model.DatasetSoftware;
import gov.nasa.podaac.inventory.model.DatasetSource;
import gov.nasa.podaac.inventory.model.DatasetVersion;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.Project;
import gov.nasa.podaac.inventory.model.Sensor;
import gov.nasa.podaac.inventory.model.Source;
import gov.nasa.jpl.horizon.sigevent.api.EventType;
import gov.nasa.jpl.horizon.sigevent.api.Response;
import gov.nasa.jpl.horizon.sigevent.api.SigEvent;


public class DatasetMetadataImpl implements MetadataObject {
	Query q = null;
	DataManager manager;
	Dataset dataset =  null;
	String user = null;
	
	boolean isAdd = false;
	private static Log log = LogFactory.getLog(DatasetMetadataImpl.class);
	
	Set<Integer> contactIds = new HashSet<Integer>();
	//lists for the multiple items
	List<indexObject> datasetElements = new LinkedList<indexObject>();
	List<indexObject> datasetLocPolicies = new LinkedList<indexObject>();
	
	List<indexObject> sources = new LinkedList<indexObject>();
	
	
	List<indexObject> resources = new LinkedList<indexObject>();
	List<indexObject> parameters = new LinkedList<indexObject>();
	List<indexObject> regions = new LinkedList<indexObject>();
	List<indexObject> versions = new LinkedList<indexObject>();
	List<indexObject> cds = new LinkedList<indexObject>();
	
	List<ElementDD> elements = new ArrayList<ElementDD>();
	
	List<indexObject> reals = new LinkedList<indexObject>();
	List<indexObject> ints =new LinkedList<indexObject>();
	List<indexObject> chars = new LinkedList<indexObject>();
	List<indexObject> datetime = new LinkedList<indexObject>();
	
	public DatasetMetadataImpl(){
		q = QueryFactory.getInstance().createQuery();
		 manager = DataManagerFactory.getInstance()
		.createDataManager();
	}
	
	public DatasetMetadataImpl(String user){
		q = QueryFactory.getInstance().createQuery();
		 manager = DataManagerFactory.getInstance()
		.createDataManager();
		 this.user = user;
	}
	
	public MetadataManifest getTemplate() throws InventoryException{
		
		InputStream is = DatasetMetadataImpl.class.getResourceAsStream("/gov/nasa/podaac/inventory/xml/datasetTemplate.xml");
		if(is == null){
			throw new InventoryException("Resource datasetTemplate not found");
		}
		
		BufferedReader reader =  new BufferedReader(new InputStreamReader(is));
		
		
		StringBuilder sb = new StringBuilder();
		String line;
		
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new InventoryException(e1);
		}
		
		String xml = sb.toString();
		MetadataManifest x = null;
		try {
			x = new MetadataManifest(xml);
		} catch (MetadataManifestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return x;
	}

	public MetadataManifest addNew(MetadataManifest mf)
			throws InventoryException {
		
		isAdd = true;
		elements = q.fetchElementDDList(new ElementDD());
		
		
		//map the existing elements to the dataset
		dataset = mapElementsToDateset(mf, null);
		
		DatasetPolicy dp = dataset.getDatasetPolicy();
		DatasetCoverage dc = dataset.getDatasetCoverage();
		
		//DatasetVersion dv = ;
		
		dataset = manager.addDataset(dataset);
		dc.setDataset(dataset);
		dp.setDataset(dataset);
		manager.addDatasetCoverage(dc);
		manager.addDatasetPolicy(dp);
		
		if(!versions.isEmpty()){
			dataset.getVersionSet().clear();
			Iterator<indexObject> it = versions.iterator();
			while(it.hasNext()){
				DatasetVersion dv = (DatasetVersion)it.next().o;
				if(dv.getVersion()!=null){
					dv.getDatasetVersionPK().setDataset(dataset);
					dataset.getVersionSet().add(dv);
				}
			}
		}
		
		if(dataset.getVersionSet().isEmpty()){
		
			DatasetVersion dv = new DatasetVersion();
			dv.getDatasetVersionPK().setDataset(dataset);
			dv.setDescription("Initial creation of dataset.");
			dv.setVersion("1");
			dv.getDatasetVersionPK().setVersionId(1);
			dv.setVersionDateLong(new Date().getTime());
			dv.setDescription("Creation of Dataset via Metadata Tool.");
			dataset.getVersionSet().add(dv);
			
			Integer versionId = dv.getDatasetVersionPK().getVersionId();
			
			DatasetMetaHistory dmh = getMetaHistory(dataset);
			dmh.setCreationDateLong(new Date().getTime());
			dmh.setLastRevisionDateLong(new Date().getTime());
			dmh.setRevisionHistory("Dataset Creation via Metadata Tool");
			dmh.getMetaHistoryPK().setDataset(dataset);
			dmh.getMetaHistoryPK().setVersionId(versionId);
			dataset.getMetaHistorySet().add(dmh);
		}
		
		
		//add contacts
		if(!contactIds.isEmpty()){
			dataset.getContactSet().clear();
			for(int i : contactIds){
				DatasetContact c = new DatasetContact();
				c.getDatasetContactPK().setDataset(dataset);
				c.getDatasetContactPK().getContact().setContactId(i);
				dataset.getContactSet().add(c);
			}
		}	
		
		//We may need to add this after saving the dataset/and set the datasetId
		//add DLP
		if(!datasetLocPolicies.isEmpty()){
			dataset.getLocationPolicySet().clear();
			Iterator<indexObject> it = datasetLocPolicies.iterator();
			while(it.hasNext()){
				DatasetLocationPolicy dlp = (DatasetLocationPolicy)it.next().o;
				if(dlp.getBasePath()!=null){
					dlp.setDatasetId(dataset.getDatasetId());
					dataset.getLocationPolicySet().add(dlp);
				}
			}
		}
		//add sources
		if(!sources.isEmpty()){
			dataset.getSourceSet().clear();
			Iterator<indexObject> it = sources.iterator();
			while(it.hasNext()){
				DatasetSource ds = (DatasetSource)it.next().o;
				if(ds.getDatasetSourcePK().getSensor()!=null){
					ds.getDatasetSourcePK().setDataset(dataset);
					dataset.getSourceSet().add(ds);
				}
			}
		}
		dataset = manager.addDataset(dataset);
		//System.out.println(dataset.getDatasetId());
		
		log.debug("***Printing DEID Info***");
		for(DatasetElement de : dataset.getDatasetElementSet()){
			log.debug("DE ID: "+de.getDeId() + ", ELEMENT_ID: " + de.getElementDD().getElementId());
		}
		
		
		if(!cds.isEmpty()){
			dataset.getCollectionDatasetSet().clear();
			Iterator<indexObject> it = cds.iterator();
			while(it.hasNext()){
				CollectionDataset cd = (CollectionDataset)it.next().o;
				if(cd.getCollectionDatasetPK().getCollection().getCollectionId() != null){
					cd.getCollectionDatasetPK().setDataset(dataset);
					manager.addCollectionDataset(cd);
				}
			}
		}
		
	
		//find ways to speed this part up...
		if(!reals.isEmpty()){
			log.debug("Processing reals...");
			dataset.getDatasetRealSet().clear();
			Iterator<indexObject> it = reals.iterator();
			while(it.hasNext()){
				
				DatasetReal dr = (DatasetReal)it.next().o;
				log.debug("Processing real:" + dr.getDatasetElement().getDeId());
				if(dr!=null){
					for(DatasetElement d: dataset.getDatasetElementSet())
					{
						
						if(d.getElementDD().getElementId().equals(dr.getDatasetElement().getElementDD().getElementId()))
						{
							log.debug("Found match, adding...");
							if(d.getDeId() == null){
								log.debug("No DEID! This won't work!");
								
							}
							dr.setDatasetElement(d);
							dr.getDataset().setDatasetId(dataset.getDatasetId());
							
							log.debug("adding: " + dataset.getDatasetId()+","+ d.getDeId()+","+dr.getValue()+","+ dr.getUnits());
							q.addReal(dataset.getDatasetId(), d.getDeId(), dr.getValue(), dr.getUnits());
						}
					}
				}
			}
		}
		
		if(!ints.isEmpty()){
			dataset.getDatasetIntegerSet().clear();
			Iterator<indexObject> it = ints.iterator();
			while(it.hasNext()){
				DatasetInteger dr =(DatasetInteger)it.next().o;
				if(dr!=null){
					for(DatasetElement d: dataset.getDatasetElementSet())
					{
						if(d.getElementDD().getElementId().equals(dr.getDatasetElement().getElementDD().getElementId()))
						{
							dr.setDatasetElement(d);
							dr.getDataset().setDatasetId(dataset.getDatasetId());
							q.addInteger(dataset.getDatasetId(), d.getDeId(), dr.getValue(), dr.getUnits());
						}
					}
				}
			}
		}
		
		if(!chars.isEmpty()){
			dataset.getDatasetCharacterSet().clear();
			Iterator<indexObject> it = chars.iterator();
			while(it.hasNext()){
				DatasetCharacter dr = (DatasetCharacter)it.next().o;
				if(dr!=null){
					for(DatasetElement d: dataset.getDatasetElementSet())
					{
						//System.out.println(d.getElementDD().getElementId() + ":" +dr.getDatasetElement().getElementDD().getElementId());
						if(d.getElementDD().getElementId().equals(dr.getDatasetElement().getElementDD().getElementId()))
						{
							//System.out.println("Adding Character");
							dr.setDatasetElement(d);
							dr.getDataset().setDatasetId(dataset.getDatasetId());
							q.addChars(dataset.getDatasetId(), d.getDeId(), dr.getValue());
						}
					}
				}
			}
		}
		
		if(!datetime.isEmpty()){
			dataset.getDatasetDateTimeSet().clear();
			Iterator<indexObject> it = datetime.iterator();
			while(it.hasNext()){
				DatasetDateTime dr = (DatasetDateTime)it.next().o;
				if(dr!=null){
					for(DatasetElement d: dataset.getDatasetElementSet())
					{
						//System.out.println(d.getElementDD().getElementId() + ":" +dr.getDatasetElement().getElementDD().getElementId());
						if(d.getElementDD().getElementId().equals(dr.getDatasetElement().getElementDD().getElementId()))
						{
							dr.setDatasetElement(d);
							dr.getDataset().setDatasetId(dataset.getDatasetId());
							q.addDateTime(dataset.getDatasetId(), d.getDeId(), dr.getValueLong());
						}
					}
				}
			}
		}
		String inventoryConfig = System.getProperty("inventory.config.file");
		
		log.debug("loading inventory config file: " + inventoryConfig);
		Properties inventoryProps = new Properties();
		if (inventoryConfig != null) {
            try {
            	inventoryProps.load(
                                    new FileInputStream(inventoryConfig));
            } catch (IOException e) {
                    e.printStackTrace();
            }
		}
		String URL =  inventoryProps.getProperty("sig.event.url");
		log.debug("sigevent URL: " + URL);
		
		
		SigEvent se = new SigEvent(URL);
		
		String persId = "";
		if(dataset.getPersistentId() != null)
			persId =  dataset.getPersistentId() + " - ";
		
		//shortname should be either updated or added...
		Response r = se.create(EventType.Info, "DMAS-DATASET-ADDED", "Inventory/DMT", "unknown", "unknown", persId +"DMT Update - " + dataset.getShortName() + "; User: " + user,  99, "Dataset: "+dataset.getShortName()+" added at " + new Date().getTime() + "("+new Date().toString()+")");
		if(r.hasError()){
			log.debug("sig event error: " + r.getError());
		}
		
		MetadataManifest mani = new MetadataManifest();
		mani.setObject(Constant.ObjectType.DATASET);
		mani.setType(Constant.ActionType.LIST);
		MetadataField field = new MetadataField();
		field.setName("report");
		field.setValue("Dataset: " + dataset.getShortName() + "was successfully added");
		field.setType("char");
		mani.getFields().add(field);
		
		field = new MetadataField();
		field.setName("object_id");
		field.setValue(dataset.getDatasetId());
		field.setType("int");
		mani.getFields().add(field);
		return mani;
	}

	public MetadataManifest delete(MetadataManifest mf)
			throws InventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public String Compare(Dataset orig, Dataset d){
		
		if(orig == null){
			return "Original Dataset Not Found.";
		}
		if(d == null){
			return "Could not create new dataset";
		}
		StringBuilder sb = new StringBuilder();
		
		//dataset
		String diff = null;
		diff = orig.diff(d);
		if (diff != null)
			sb.append(diff);
		diff = orig.getDatasetCoverage().diff(d.getDatasetCoverage());
		if (diff != null)
			sb.append(diff);
		diff = orig.getDatasetPolicy().diff(d.getDatasetPolicy());
		if (diff != null)
			sb.append(diff);

		//citation
		{
			log.debug("Original Citation Size: " +orig.getCitationSet().size());
			log.debug("Modified Citation Size: " +d.getCitationSet().size());
			
			Set<DatasetCitation> removed = new HashSet<DatasetCitation>(orig.getCitationSet());
			Set<DatasetCitation> added = new HashSet<DatasetCitation>(d.getCitationSet());
			removed.removeAll(d.getCitationSet());
			added.removeAll(orig.getCitationSet());
			log.debug("processed citation sets...");
			log.debug("Original Citation Size: " +orig.getCitationSet().size());
			log.debug("Modified Citation Size: " +d.getCitationSet().size());
			for(DatasetCitation dc : removed){
				sb.append("Removed datasetCitation with title ["+dc.getTitle()+"] from dataset\n");
			}
			for(DatasetCitation dc : added){
				sb.append("Added datasetCitation with title ["+dc.getTitle()+"] to dataset\n");
			}
		}
		//resource
		{
			Set<DatasetResource> removed = new HashSet<DatasetResource>(orig.getResourceSet());
			Set<DatasetResource> added = new HashSet<DatasetResource>(d.getResourceSet());
			removed.removeAll(d.getResourceSet());
			added.removeAll(orig.getResourceSet());	
			for(DatasetResource dc : removed){
				sb.append("Removed DatasetResource with name ["+dc.getResourceName()+"] from dataset\n");
			}
			for(DatasetResource dc : added){
				sb.append("Added DatasetResource with name ["+dc.getResourceName()+"] to dataset\n");
			}
		}	
		//project
		{
			Set<Integer> removed = new HashSet<Integer>();		
			Set<Integer> added = new HashSet<Integer>();
	
			for(DatasetProject dp :orig.getProjectSet()){
				removed.add(dp.getDatasetProjectPK().getProject().getProjectId());
			}
			for(DatasetProject dp :d.getProjectSet()){
				added.add(dp.getDatasetProjectPK().getProject().getProjectId());
			}
			Set<Integer> origList = new HashSet<Integer>(removed);
			Set<Integer> newList = new HashSet<Integer>(added);
			
			removed.removeAll(newList);
			added.removeAll(origList);
			
			for(Integer dc : removed){
				sb.append("Removed DatasetProject with id ["+dc+"] from dataset\n");
			}
			for(Integer dc : added){
				sb.append("Added DatasetProject with id ["+dc+"] \n");
			}
		}
	
		//parameter
		{
			Set<DatasetParameter> removed = new HashSet<DatasetParameter>(orig.getParameterSet());
			Set<DatasetParameter> added = new HashSet<DatasetParameter>(d.getParameterSet());
			log.debug("size of datasetParams: " + added.size());
			removed.removeAll(d.getParameterSet());
			added.removeAll(orig.getParameterSet());
			
			for(DatasetParameter dc : removed){
				sb.append("Removed DatasetParameter with var/var detail ["+dc.getVariable()+"/"+dc.getVariableDetail()+"] from dataset\n");
			}
			for(DatasetParameter dc : added){
				sb.append("Added DatasetParameter with var/var detail ["+dc.getVariable()+"/"+dc.getVariableDetail()+"] to dataset\n");
			}
			
		}
		//region
		{
			Set<DatasetRegion> removed = new HashSet<DatasetRegion>(orig.getRegionSet());
			Set<DatasetRegion> added = new HashSet<DatasetRegion>(d.getRegionSet());
			log.debug("size of datasetRegions: " + added.size());
			removed.removeAll(d.getRegionSet());
			added.removeAll(orig.getRegionSet());
			
			for(DatasetRegion dc : removed){
				sb.append("Removed DatasetRegion with region/region detail ["+dc.getRegion()+"/"+dc.getRegionDetail()+"] from dataset\n");
			}
			for(DatasetRegion dc : added){
				sb.append("Added DatasetRegion with region/region detail ["+dc.getRegion()+"/"+dc.getRegionDetail()+"] to dataset\n");
			}
		}

		//provider DIFF
		{
			if(!orig.getProvider().getProviderId().equals(d.getProvider().getProviderId()))
					sb.append("Dataset Provider changed from "+orig.getProvider().getProviderId()+" to "+d.getProvider().getProviderId()+"\n");
		}
		
		//datasetElements
		{
			Set<Integer> removed = new HashSet<Integer>();		
			Set<Integer> added = new HashSet<Integer>();
	
			for(DatasetElement de :orig.getDatasetElementSet()){
				removed.add(de.getElementDD().getElementId()); //uh oh...
			}
			for(DatasetElement de :d.getDatasetElementSet()){ 
				added.add(de.getElementDD().getElementId()); //uh oh...
			}
			log.debug("size of Original datasetelements: " + orig.getDatasetElementSet().size());
			log.debug("size of New datasetelements: " + d.getDatasetElementSet().size());
			Set<Integer> origList = new HashSet<Integer>(removed);
			Set<Integer> newList = new HashSet<Integer>(added);
			
			removed.removeAll(newList);
			added.removeAll(origList);
			
			for(Integer dc : removed){
				String eName = null;
				for(ElementDD ed : elements){
					if(ed.getElementId().equals(dc)){
						eName = " ("+ ed.getLongName() +")";					}
				}
				if(eName == null)
					eName = "";
				sb.append("Removed DatasetElement with ElementId ["+dc+"]"+eName+" from dataset\n");
			}
			for(Integer dc : added){
				String eName = null;
				for(ElementDD ed : elements){
					if(ed.getElementId().equals(dc)){
						eName = " ("+ ed.getLongName() +")";
					}
				}
				if(eName == null)
					eName = "";
				
				sb.append("Added DatasetElement with ElementId ["+dc+"]"+eName+" \n");
			}
		}
		//source
		{
			Set<Integer> removed = new HashSet<Integer>();		
			Set<Integer> added = new HashSet<Integer>();
			log.debug("Original Source Size" + orig.getSourceSet().size());
			for(DatasetSource dc : orig.getSourceSet()){
				Integer t = dc.getDatasetSourcePK().getSource().getSourceId() * 100000 + dc.getDatasetSourcePK().getSensor().getSensorId();
				log.debug("original sensor code: " + t);
				removed.add(t);
				//sb.append("Removed DatasetResource with name ["+dc.getDatasetSourcePK().get+"] from dataset\n");
			}
			log.debug("New Source Size" + sources.size());

			for(indexObject dc : sources){
				DatasetSource ds = ((DatasetSource)dc.o);
				Integer t = ds.getDatasetSourcePK().getSource().getSourceId() * 100000 + ds.getDatasetSourcePK().getSensor().getSensorId();
				log.debug("new sensor code: " + t);
				added.add(t);
			}
			
			Set<Integer> origList = new HashSet<Integer>(removed);
			Set<Integer> newList = new HashSet<Integer>(added);
			
			removed.removeAll(newList);
			added.removeAll(origList);
			
			for(Integer dc : removed){
				sb.append("Removed Source/Sensor with source/sensor ids "+dc/100000+"/"+dc%100000+"\n");
			}
			for(Integer dc : added){
				sb.append("Added Source/Sensor with source/sensor ids "+dc/100000+"/"+dc%100000+"\n");
			}
		}
		
		//metahistory set
		//orig.getMetaHistorySet(); Won't do this, these are all meta/controlled by the database
		
		//version
		{
			Set<DatasetVersion> removed = new HashSet<DatasetVersion>(orig.getVersionSet());
			Set<DatasetVersion> added = new HashSet<DatasetVersion>();
			
			for(indexObject o : versions){
				added.add((DatasetVersion)o.o);
			}
			
			removed.removeAll(added);
			added.removeAll(orig.getVersionSet());
			
			for(DatasetVersion dc : removed){
				sb.append("Removed DatasetVersion with Version ["+dc.getVersion()+"] from dataset\n");
			}
			for(DatasetVersion dc : added){
				sb.append("Added DatasetVersion with Version ["+dc.getVersion()+"] to dataset\n");
			}
		}		
		//contact
		{
			Set<Integer> removed = new HashSet<Integer>();		
			Set<Integer> added = new HashSet<Integer>(contactIds);
	
			for(DatasetContact de :orig.getContactSet()){
				removed.add(de.getDatasetContactPK().getContact().getContactId()); //uh oh...
			}
//			for(DatasetContact de :contactIds){
//				added.add(de.getDatasetContactPK().getContact().getContactId()); //uh oh...
//			}
			Set<Integer> origList = new HashSet<Integer>(removed);
			Set<Integer> newList = new HashSet<Integer>(added);
			
			removed.removeAll(newList);
			added.removeAll(origList);
			
			for(Integer dc : removed){
				sb.append("Removed Contact with id ["+dc+"] from dataset\n");
			}
			for(Integer dc : added){
				sb.append("Added Contact with id ElementId ["+dc+"] \n");
			}
		}
		//collections
		{
			Set<Integer> removed = new HashSet<Integer>();//new HashSet<CollectionDataset>(orig.getCollectionDatasetSet());
			Set<Integer> added = new HashSet<Integer>();

			for(CollectionDataset cd :orig.getCollectionDatasetSet() ){
				removed.add(cd.getCollectionDatasetPK().getCollection().getCollectionId());
			}
			
			for(indexObject o : cds){
				added.add(((CollectionDataset)o.o).getCollectionDatasetPK().getCollection().getCollectionId());
			}
			Set<Integer> origList = new HashSet<Integer>(removed);
			Set<Integer> newList = new HashSet<Integer>(added);
			
			removed.removeAll(newList);
			added.removeAll(origList);
			
			for(Integer dc : removed){
				sb.append("Removed Collection with ID ["+dc+"] from dataset\n");
			}
			for(Integer dc : added){
				sb.append("Added Collection with ID ["+dc+"] to dataset\n");
			}
		}
		//DLP
		{
			Set<DatasetLocationPolicy> removed = new HashSet<DatasetLocationPolicy>(orig.getLocationPolicySet());
			Set<DatasetLocationPolicy> added = new HashSet<DatasetLocationPolicy>();
			log.debug("Size of orig Loc Policies: " + removed.size());
			
			for(indexObject o : datasetLocPolicies){
				added.add((DatasetLocationPolicy)o.o);
			}
			log.debug("Size of new Loc Policies: " + added.size());
			removed.removeAll(added);
			added.removeAll(orig.getLocationPolicySet());
			for(DatasetLocationPolicy dc : removed){
				sb.append("Removed DatasetLocationPolicy with Path ["+dc.getBasePath()+"] from dataset\n");
			}
			for(DatasetLocationPolicy dc : added){
				sb.append("Added DatasetLocationPolicy with Path ["+dc.getBasePath()+"] to dataset\n");
			}
			
		}
		
		
		//other stuff...
		//log.debug("Diff: " + sb.toString());
		return sb.toString();
		
	}
	
	public MetadataManifest getInstance(MetadataManifest mf)
			throws InventoryException {
		int id = 0;
		try{
			for(MetadataField mef : mf.getFields()){
				if(mef.getName().equals("object_id"))
					id = Integer.valueOf(mef.getValue());
			}
		}catch(Exception e){
			throw new InventoryException("Dataset id not found or improper format.");
		}
		
		Dataset d = q.fetchDatasetByIdFull(id,"DatasetIntegerSet", "sourceSet","DatasetDateTimeSet","DatasetCharacterSet","softwareSet","resourceSet","collectionDatasetSet","datasetPolicy","CollectionDataset","locationPolicySet","versionSet","datasetCoverage","contactSet","parameterSet","regionSet", "citationSet","DatasetSoftware", "DatasetSource", "DatasetProvider", "projectSet");
		if(d == null){
			throw new InventoryException("Dataset [id=\""+id+"\"] not found.");
		}
		MetadataManifest mm = mapDatasetToManifest(d);
		return mm;
	}

	

	public MetadataManifest update(MetadataManifest mf)
			throws InventoryException {
		int id = 0;
		try{
		for(MetadataField mef : mf.getFields()){
			if(mef.getName().equals("object_id") || mef.getName().equals("dataset_datasetId"))
				id = Integer.valueOf(mef.getValue());
		}
		}catch(Exception e){
			throw new InventoryException("Dataset id not found or improper format.");
		}
		
		Dataset d = null;
		Dataset original = q.fetchDatasetByIdFull(id,"DatasetIntegerSet", "sourceSet","DatasetDateTimeSet","DatasetCharacterSet","softwareSet","resourceSet","collectionDatasetSet","datasetPolicy","CollectionDataset","locationPolicySet","versionSet","datasetCoverage","contactSet","parameterSet", "citationSet","DatasetSoftware", "DatasetSource", "DatasetProvider","DatasetProject", "projectSet");
		if(original == null){
			log.debug("Dataset is null...");
			throw new InventoryException("Original Dataset could not be found.");
		}
		
		elements = q.fetchElementDDList(new ElementDD());
		
		//System.out.println(d.getShortName());
		dataset = mapElementsToDateset(mf, d);
		
		String comparison = Compare(original, dataset);
		
		
		log.debug("Diff: " + comparison);
		
		log.debug("Dataset after mapping: " + dataset.getDatasetId());
		log.debug("Dataset after mapping: " + dataset.getShortName());
		for(DatasetMetaHistory dmh : dataset.getMetaHistorySet()){
			dmh.setLastRevisionDateLong(new Date().getTime());
		}
		String ids = null;
		for(Integer i : contactIds){
			if(i != null){
				if(ids == null)
					ids = "" + i;
				else
					ids += ", " + i;
			}
		}
		String dids = null;
		
		//datasetElements
		//delete from granules, check granules for use of DEIDS?
		
		for(indexObject io : datasetElements){
			DatasetElement i = (DatasetElement) io.o;

			if(i.getDeId() != null){
				if(dids == null)
					dids = "" + i.getDeId();
				else
					dids += ", " + i.getDeId();
			}
		}
		
		//
		// FIX THIS 
		// FIX THIS
		//
		
		//System.out.println("DELETE FROM dataset_contact where dataset_id="+dataset.getDatasetId() +" AND contact_id not in ("+ ids + ");");
		q.runQuery("DELETE FROM dataset_contact where dataset_id="+dataset.getDatasetId() +" AND contact_id not in ("+ ids + ")");
		q.runQuery("DELETE FROM dataset_coverage where dataset_id="+dataset.getDatasetId() +"");
		q.runQuery("DELETE FROM dataset_policy where dataset_id="+dataset.getDatasetId() +"");
		q.runQuery("DELETE FROM dataset_project where dataset_id="+dataset.getDatasetId() +"");
		q.runQuery("DELETE FROM dataset_source where dataset_id="+dataset.getDatasetId() +"");
		
		//delete from granules, check granules for use of DEIDS?
		boolean deletedAll = true;
		//get the lsit of dataset IDS not in the list of ids.
		List<Integer> delIds = q.getDatasetIds(dataset.getDatasetId(),dids);
		
		//check each delIds for usage, if they are used, add them to 'dids'
		for(Integer did : delIds){
			if(q.deiIdIsUsed(did)){
				log.debug("deId ["+did+"] is in use. Not deleting.");
				deletedAll = false;
				if(dids == null)
					dids = "" + did;
				else
					dids += ", " + did;
			}
		}
		
		
		q.runQuery("DELETE FROM dataset_datetime where dataset_id="+dataset.getDatasetId() +" AND de_Id not in ("+ dids + ")");
		q.runQuery("DELETE FROM dataset_character where dataset_id="+dataset.getDatasetId() +" AND de_Id not in ("+ dids + ")");
		q.runQuery("DELETE FROM dataset_integer where dataset_id="+dataset.getDatasetId() +" AND de_Id not in ("+ dids + ")");
		q.runQuery("DELETE FROM dataset_real where dataset_id="+dataset.getDatasetId() +" AND de_Id not in ("+ dids + ")");
		q.runQuery("DELETE FROM dataset_element where dataset_id="+dataset.getDatasetId() +" AND de_Id not in ("+ dids + ")");
		
		
		DatasetPolicy dp = dataset.getDatasetPolicy();
		DatasetCoverage dc = dataset.getDatasetCoverage();
		dataset = manager.addDataset(dataset);
		dc.setDataset(dataset);
		dp.setDataset(dataset);
		manager.addDatasetCoverage(dc);
		manager.addDatasetPolicy(dp);
		
		//add contacts
		if(!contactIds.isEmpty()){
			dataset.getContactSet().clear();
			for(Integer i : contactIds){
				if(i != null){
					DatasetContact c = new DatasetContact();
					c.getDatasetContactPK().setDataset(dataset);
					c.getDatasetContactPK().getContact().setContactId(i);
					dataset.getContactSet().add(c);
				}
			}
		}	
		
		//add sources
		if(!sources.isEmpty()){
			dataset.getSourceSet().clear();
			Iterator<indexObject> it = sources.iterator();
			while(it.hasNext()){
				DatasetSource ds = (DatasetSource)it.next().o;
				if(ds.getDatasetSourcePK().getSensor()!=null){
					ds.getDatasetSourcePK().setDataset(dataset);
					dataset.getSourceSet().add(ds);
				}
			}
		}
		
		if(!datasetLocPolicies.isEmpty()){
			dataset.getLocationPolicySet().clear();
			Iterator<indexObject> it = datasetLocPolicies.iterator();
			while(it.hasNext()){
				DatasetLocationPolicy dlp = (DatasetLocationPolicy) it.next().o;
				if(dlp.getBasePath()!=null){
					dlp.setDatasetId(dataset.getDatasetId());
					dataset.getLocationPolicySet().add(dlp);
				}
			}
		}
		
		if(!versions.isEmpty()){
			dataset.getVersionSet().clear();
			Iterator<indexObject> it = versions.iterator();
			while(it.hasNext()){
				DatasetVersion dv = (DatasetVersion)it.next().o;
				if(dv.getVersion()!=null){
					dv.getDatasetVersionPK().setDataset(dataset);
					dataset.getVersionSet().add(dv);
				}
			}
		}
		List<CollectionDataset> cdDels = new ArrayList<CollectionDataset>();
		//evict original
		for(CollectionDataset o : original.getCollectionDatasetSet()){
			q.evictObject(o.getCollectionDatasetPK());
			q.evictObject(o.getCollectionDatasetPK().getCollection());
			cdDels.add(o);
		}
		q.evictObject(original);
		original = null;
		log.debug("Evicted everything");
		manager.updateDatasetDirty(dataset);
		log.debug("Updated dataset...");
		dataset = q.fetchDatasetByIdFull(id,"DatasetIntegerSet","DatasetDateTimeSet","DatasetCharacterSet", "sourceSet","softwareSet","resourceSet","collectionDatasetSet","datasetPolicy","CollectionDataset","locationPolicySet","versionSet","datasetCoverage","contactSet","parameterSet","regionSet", "citationSet","DatasetSoftware", "DatasetSource", "DatasetProvider", "projectSet");
		log.debug("Fetched full dataset");


		List<CollectionDataset> cdDels2 = new ArrayList<CollectionDataset>();
                for(CollectionDataset cd : cdDels){	
			for(CollectionDataset cdx :  dataset.getCollectionDatasetSet()){
				if(cdx.getCollectionDatasetPK().getCollection().getCollectionId().equals(cd.getCollectionDatasetPK().getCollection().getCollectionId())){
					log.debug("Deleting collection " + cdx.getCollectionDatasetPK().getCollection().getCollectionId());
					//manager.deleteCollectionDataset(cdx);
					cdDels2.add(cdx);
					q.evictObject(cdx);
					q.runQuery("delete from Collection_Dataset where collection_id="+cdx.getCollectionDatasetPK().getCollection().getCollectionId()+" AND dataset_id="+cdx.getCollectionDatasetPK().getDataset().getDatasetId()+" ");
				}
			}
		}

		if(dataset.getCollectionDatasetSet().removeAll(cdDels)){
			log.debug("Removed deleted collections 1");
		}	

   		if(dataset.getCollectionDatasetSet().removeAll(cdDels2)){
                        log.debug("Removed deleted collections 1");
                }

                List<Integer> remv = new ArrayList<Integer>();
		log.debug("Deleting collectionDatasets");
		if(!cds.isEmpty()){
			log.debug("Size of collectionDatasets: " + cds.size());
			
			dataset.getCollectionDatasetSet().clear();
			Iterator<indexObject> it = cds.iterator();
			while(it.hasNext()){
				CollectionDataset cd = (CollectionDataset)it.next().o;
				if(cd.getCollectionDatasetPK().getCollection().getCollectionId() != null){

					remv.add(cd.getCollectionDatasetPK().getCollection().getCollectionId());

					cd.getCollectionDatasetPK().setDataset(dataset);
					q.evictObject(cd);
					manager.deleteCollectionDataset(cd);
					log.debug("delete");
					manager.addCollectionDataset(cd);
				}
			}
		}

		if(!reals.isEmpty()){
			dataset.getDatasetRealSet().clear();
			Iterator<indexObject> it = reals.iterator();
			while(it.hasNext()){
				DatasetReal dr = (DatasetReal)it.next().o;
				
				if(dr.getDatasetElement().getElementDD().getElementId()!=null || dr.getDatasetElement().getDeId()!=null){
					for(DatasetElement dd: dataset.getDatasetElementSet())
					{
						//System.out.println(dd.getElementDD().getElementId());
						
						if(dd.getElementDD().getElementId().equals(dr.getDatasetElement().getElementDD().getElementId()) || dd.getDeId().equals(dr.getDatasetElement().getDeId()))
						{
						
							dr.setDatasetElement(dd);
							dr.getDataset().setDatasetId(dataset.getDatasetId());
							q.addReal(dataset.getDatasetId(), dd.getDeId(), dr.getValue(), dr.getUnits());
						}
					}
				}
			}
		}
		
		if(!ints.isEmpty()){
			dataset.getDatasetIntegerSet().clear();
			Iterator<indexObject> it = ints.iterator();
			while(it.hasNext()){
				DatasetInteger dr = (DatasetInteger)it.next().o;
				if(dr.getDatasetElement().getElementDD().getElementId()!=null || dr.getDatasetElement().getDeId()!=null){
					for(DatasetElement dd: dataset.getDatasetElementSet())
					{
						if(dd.getElementDD().getElementId().equals(dr.getDatasetElement().getElementDD().getElementId()) || dd.getDeId().equals(dr.getDatasetElement().getDeId()))
						{
							dr.setDatasetElement(dd);
							dr.getDataset().setDatasetId(dataset.getDatasetId());
							q.addInteger(dataset.getDatasetId(), dd.getDeId(), dr.getValue(), dr.getUnits());
						}
					}
				}
			}
		}
		
		if(!chars.isEmpty()){
			dataset.getDatasetCharacterSet().clear();
			Iterator<indexObject> it = chars.iterator();
			while(it.hasNext()){
				DatasetCharacter dr = (DatasetCharacter)it.next().o;
				if(dr.getDatasetElement().getElementDD().getElementId()!=null || dr.getDatasetElement().getDeId()!=null){
					for(DatasetElement dd: dataset.getDatasetElementSet())
					{
						//System.out.println(d.getElementDD().getElementId() + ":" +dr.getDatasetElement().getElementDD().getElementId());
						if(dd.getElementDD().getElementId().equals(dr.getDatasetElement().getElementDD().getElementId()) || dd.getDeId().equals(dr.getDatasetElement().getDeId()))
						{
							//System.out.println("Adding Character");
							dr.setDatasetElement(dd);
							dr.getDataset().setDatasetId(dataset.getDatasetId());
							q.addChars(dataset.getDatasetId(), dd.getDeId(), dr.getValue());
						}
					}
				}
			}
		}
		
		if(!datetime.isEmpty()){
			dataset.getDatasetDateTimeSet().clear();
			Iterator<indexObject> it = datetime.iterator();
			while(it.hasNext()){
				DatasetDateTime dr = (DatasetDateTime)it.next().o;
				if(dr.getDatasetElement().getElementDD().getElementId()!=null || dr.getDatasetElement().getDeId()!=null){
					for(DatasetElement dd: dataset.getDatasetElementSet())
					{
						//System.out.println(d.getElementDD().getElementId() + ":" +dr.getDatasetElement().getElementDD().getElementId());
						if(dd.getElementDD().getElementId().equals(dr.getDatasetElement().getElementDD().getElementId()) || dd.getDeId().equals(dr.getDatasetElement().getDeId()))
						{
							dr.setDatasetElement(dd);
							dr.getDataset().setDatasetId(dataset.getDatasetId());
							q.addDateTime(dataset.getDatasetId(), dd.getDeId(), dr.getValueLong());
						}
					}
				}
			}
		}
		String inventoryConfig = System.getProperty("inventory.config.file");
		log.debug("loading inventory config file: " + inventoryConfig);
		Properties inventoryProps = new Properties();
		if (inventoryConfig != null) {
            try {
            	inventoryProps.load(
                                    new FileInputStream(inventoryConfig));
            } catch (IOException e) {
                    e.printStackTrace();
            }
		}
		String URL =  inventoryProps.getProperty("sig.event.url");
		log.debug("sigevent URL: " + URL);
		
		
		
		if(comparison != null){
			SigEvent se = new SigEvent(URL);
			String persId = "";
			if(dataset.getPersistentId() != null)
				persId =  dataset.getPersistentId() + " - ";
			//shortname should be either updated or added...
			
			
			Response r = se.create(EventType.Info, "DMAS-DATASET-UPDATED", "Inventory/DMT", "unknown", "unknown", persId + "DMT Update - " + dataset.getShortName() + "; User: " + user,  99, comparison);
			if(r.hasError()){
				log.debug("sig event error: " + r.getError());
			}
		}
		
		MetadataManifest mani = new MetadataManifest();
		mani.setObject(Constant.ObjectType.DATASET);
		mani.setType(Constant.ActionType.LIST);
		MetadataField field = new MetadataField();
		field.setName("report");
		
		if(deletedAll)
			field.setValue("Dataset: " + dataset.getShortName() + "was successfully updated.");
		else
			field.setValue("Dataset: " + dataset.getShortName() + "was successfully updated. Some dataset Elements were not deleted.");
		
		field.setType("char");
		mani.getFields().add(field);
		
		field = new MetadataField();
		field.setName("object_id");
		field.setValue(dataset.getDatasetId());
		field.setType("int");
		mani.getFields().add(field);
		mani.setNote(comparison);

                for(CollectionDataset cdx : cdDels){
   			if(!remv.contains(cdx.getCollectionDatasetPK().getCollection().getCollectionId()))
	               		q.runQuery("delete from Collection_Dataset where collection_id="+cdx.getCollectionDatasetPK().getCollection().getCollectionId()+" AND dataset_id="+cdx.getCollectionDatasetPK().getDataset().getDatasetId()+" ");
		}
		return mani;
	}
	
	public MetadataManifest mapDatasetToManifest(Dataset d) {
		//.out.println("in mapping...");
		log.debug("Mapping dataset...");
		MetadataManifest mm = new MetadataManifest();
		
		
		
		mm.setObject(Constant.ObjectType.DATASET);
		mm.setType(Constant.ActionType.TEMPLATE);
		
		//go through the dataset and create fields for each of the items.
		MetadataField mf = new MetadataField(); 
		
		mf.setName("dataset_datasetId");
		try{
		mf.setValue(d.getDatasetId().toString());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(true);
		mf.setType("int");
		mm.getFields().add(mf); mf = new MetadataField();

		//dataset_persistentId
		mf.setName("dataset_persistentId");
		try{
			if(d.getPersistentId() == null)
				mf.setValue("");
			else
				mf.setValue(d.getPersistentId());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("dataset_providerId");
		try{
		mf.setValue(d.getProvider().getProviderId().toString());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(true);
		mf.setType("int");
		mm.getFields().add(mf); mf = new MetadataField();

		
		mf.setName("dataset_remoteDataset");
		try{
		mf.setValue(d.getRemoteDataset());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("dataset_temporalRepeat");
		try{
		mf.setValue(d.getTemporalRepeat());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("dataset_temporalRepeatMin");
		try{
		mf.setValue(d.getTemporalRepeatMin());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("dataset_temporalRepeatMax");
		try{
		mf.setValue(d.getTemporalRepeatMax());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		
		
		
		mf.setName("dataset_sampleFrequency");
		try{
		mf.setValue(d.getSampleFrequency());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(false);
		mf.setType("float");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("dataset_swathWidth");
		try{
		mf.setValue(d.getSwathWidth());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(false);
		mf.setType("float");
		mm.getFields().add(mf); mf = new MetadataField();
		
	
		mf.setName("dataset_metadata");
		try{
		mf.setValue(d.getMetadata());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("dataset_shortName");
		mf.setValue(d.getShortName());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_longName");
		mf.setValue(d.getLongName());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_originalProvider");
		mf.setValue(d.getOriginalProvider());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_providerDatasetName");
		mf.setValue(d.getProviderDatasetName());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_processingLevel");
		mf.setValue(d.getProcessingLevel());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_latitudeResolution");
		mf.setValue(d.getLatitudeResolution());
		mf.setRequired(false);
		mf.setType("float");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_longitudeResolution");
		mf.setValue(d.getLongitudeResolution());
		mf.setRequired(false);
		mf.setType("float");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("dataset_acrossTrackResolution");
		try{
			mf.setValue(d.getAcrossTrackResolution().toString());
		}catch(Exception npe){
			mf.setValue("");
		}
		mf.setRequired(false);
		mf.setType("int");
		mm.getFields().add(mf); mf = new MetadataField();
		
		
		mf.setName("dataset_alongTrackResolution");
		try{
		mf.setValue(d.getAlongTrackResolution().toString());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(false);
		mf.setType("int");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_ascendingNodeTime");
		try{
		mf.setValue(d.getAscendingNodeTime().toString());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(false);
		mf.setType("datetime");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_horizontalResolutionRange");
		mf.setValue(d.getHorizontalResolutionRange());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_altitudeResolution");
		mf.setValue(d.getAltitudeResolution());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_depthResolution");
		mf.setValue(d.getDepthResolution());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_temporalResolution");
		mf.setValue(d.getTemporalResolution());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_temporalResolutionRange");
		mf.setValue(d.getTemporalResolutionRange());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_ellipsoidType");
		mf.setValue(d.getEllipsoidType());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_projectionType");
		mf.setValue(d.getProjectionType());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_projectionDetail");
		mf.setValue(d.getProjectionDetail());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_reference");
		mf.setValue(d.getReference());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();

		mf.setName("dataset_description");
		mf.setValue(d.getDescription());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("dataset_note");
		mf.setValue(d.getNote());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();

		if(!d.getCitationSet().isEmpty()){
			DatasetCitation dc = d.getCitationSet().iterator().next();
			
			mf = new MetadataField();
			
			mf.setName("datasetCitation_title");
			mf.setValue(dc.getTitle());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetCitation_creator");
			mf.setValue(dc.getCreator());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetCitation_version");
			mf.setValue(dc.getVersion());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetCitation_publisher");
			mf.setValue(dc.getPublisher());
			mf.setRequired(false);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetCitation_seriesName");
			mf.setValue(dc.getSeriesName());
			mf.setRequired(false);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetCitation_releaseDate");
			try{
				mf.setValue(dc.getReleaseDateLong().toString());
			}catch(NullPointerException npe){
				mf.setValue("");
			}
			mf.setRequired(false);
			mf.setType("datetime");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetCitation_releasePlace");
			mf.setValue(dc.getReleasePlace());
			mf.setRequired(false);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetCitation_citationDetail");
			mf.setValue(dc.getCitationDetail());
			mf.setRequired(false);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetCitation_onlineResource");
			mf.setValue(dc.getOnlineResource());
			mf.setRequired(false);
			mf.setType("char");
			mm.getFields().add(mf); 
			
		}
		int i = 1;
		for(DatasetContact dc: d.getContactSet()){
			mf = new MetadataField();
			mf.setName("datasetContact_contactId" + i);
			try{
			mf.setValue(dc.getDatasetContactPK().getContact().getContactId().toString());
			}catch(NullPointerException npe){
				mf.setValue("");
			}
			mf.setRequired(true);
			mf.setType("int");
			mm.getFields().add(mf); 
			i++;
		}
		
		//COLLECTION DATASET
		i=1;
		for(CollectionDataset cd : d.getCollectionDatasetSet()){
			
			mf = new MetadataField();
			mf.setName("datasetCollection_CollectionId" + i);
			mf.setValue(cd.getCollectionDatasetPK().getCollection().getCollectionId());
			mf.setRequired(true);
			mf.setType("int");
			mm.getFields().add(mf); 
			
			mf = new MetadataField();
			mf.setName("datasetCollection_granuleFlag" + i);
			mf.setValue(cd.getGranuleFlag());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf);
			
			mf = new MetadataField();
			mf.setName("datasetCollection_granuleRange360" + i);
			mf.setValue(cd.getGranuleRange360());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf);
			mf = new MetadataField();
			mf.setName("datasetCollection_startGranuleId" + i);
			mf.setValue(cd.getStartGranuleId());
			mf.setRequired(false);
			mf.setType("int");
			mm.getFields().add(mf);
			mf = new MetadataField();
			mf.setName("datasetCollection_stopGranuleId" + i);
			mf.setValue(cd.getStopGranuleId());
			mf.setRequired(false);
			mf.setType("int");
			mm.getFields().add(mf);
			i++;
		}
		
		mf = new MetadataField();

		DatasetCoverage dc = d.getDatasetCoverage();
		{
			mf.setName("datasetCoverage_startTime");
			try{
				mf.setValue(dc.getStartTimeLong().toString());
			}catch(NullPointerException npe){
				mf.setValue("");
				}
			mf.setRequired(true);
			mf.setType("datetime");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetCoverage_stopTime");
			try{
			mf.setValue(dc.getStopTimeLong().toString());
			}catch(NullPointerException npe){
				mf.setValue("");
			}
			mf.setRequired(false);
			mf.setType("datetime");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetCoverage_northernmostLatitude");
			try{
			mf.setValue(dc.getNorthLat().toString());
			}catch(NullPointerException npe){
				mf.setValue("");
			}
			mf.setRequired(false);
			mf.setType("double");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetCoverage_southernmostLatitude");
			try{
				mf.setValue(dc.getSouthLat().toString());
			}catch(NullPointerException npe){
				mf.setValue("");
			}
			mf.setRequired(false);
			mf.setType("double");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetCoverage_easternmostLongitude");
			mf.setValue(dc.getEastLon());
			mf.setRequired(false);
			mf.setType("double");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetCoverage_westernmostLongitude");
			mf.setValue(dc.getWestLon());
			mf.setRequired(false);
			mf.setType("double");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetCoverage_minAltitude");
			mf.setValue(dc.getMinAltitude());
			mf.setRequired(false);
			mf.setType("double");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetCoverage_maxAltitude");
			mf.setValue(dc.getMaxAltitude());
			mf.setRequired(false);
			mf.setType("double");
			mm.getFields().add(mf); mf = new MetadataField();		
			
			mf.setName("datasetCoverage_minDepth");
			mf.setValue(dc.getMinDepth());
			mf.setRequired(false);
			mf.setType("double");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetCoverage_maxDepth");
			mf.setValue(dc.getMaxDepth());
			mf.setRequired(false);
			mf.setType("double");
			mm.getFields().add(mf); 

			mf.setName("datasetCoverage_granuleRange360");
                        mf.setValue(dc.getGranuleRange360());
                        mf.setRequired(true);
                        mf.setType("char");
                        mm.getFields().add(mf);


		}
	
		i = 1;
		for(DatasetElement de: d.getDatasetElementSet())
		{	
			mf = new MetadataField();
			mf.setName("datasetElement_deId"+ i);
			mf.setValue(de.getDeId());
			mf.setRequired(true);
			mf.setType("int");
			mm.getFields().add(mf); ;
			
			mf = new MetadataField();
			mf.setName("datasetElement_elementId"+ i);
			mf.setValue(de.getElementDD().getElementId());
			mf.setRequired(true);
			mf.setType("int");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetElement_obligationFlag"+ i);
			mf.setValue(de.getObligationFlag());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetElement_scope"+ i);
			mf.setValue(de.getScope());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf);
			i++;
		}
		
		i=1;
		//dataset_real, inteeger, datetime, char, spatial
		for(DatasetReal dr : d.getDatasetRealSet()){
			
			mf = new MetadataField();
			mf.setName("datasetReal_deId"+ i);
			mf.setValue(dr.getDatasetElement().getDeId());
			mf.setRequired(true);
			mf.setType("int");
			mm.getFields().add(mf);
			
			mf = new MetadataField();
			mf.setName("datasetReal_units"+ i);
			mf.setValue(dr.getUnits());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf);
			
			mf = new MetadataField();
			mf.setName("datasetReal_value"+ i);
			mf.setValue(dr.getValue());
			mf.setRequired(true);
			mf.setType("double");
			mm.getFields().add(mf);
			i++;
		}
		
		i=1;
		//dataset_real, inteeger, datetime, char, spatial
		for(DatasetCharacter dr : d.getDatasetCharacterSet()){
			
			mf = new MetadataField();
			mf.setName("datasetCharacter_deId"+ i);
			mf.setValue(dr.getDatasetElement().getDeId());
			mf.setRequired(true);
			mf.setType("int");
			mm.getFields().add(mf);
			
			
			mf = new MetadataField();
			mf.setName("datasetCharacter_value"+ i);
			mf.setValue(dr.getValue());
			mf.setRequired(true);
			mf.setType("double");
			mm.getFields().add(mf);
			i++;
		}
		
		i=1;
		//dataset_real, inteeger, datetime, char, spatial
		for(DatasetInteger dr : d.getDatasetIntegerSet()){
			
			mf = new MetadataField();
			mf.setName("datasetInteger_deId"+ i);
			mf.setValue(dr.getDatasetElement().getDeId());
			mf.setRequired(true);
			mf.setType("int");
			mm.getFields().add(mf);
			
			mf = new MetadataField();
			mf.setName("datasetInteger_units"+ i);
			mf.setValue(dr.getUnits());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf);
			
			mf = new MetadataField();
			mf.setName("datasetInteger_value"+ i);
			mf.setValue(dr.getValue());
			mf.setRequired(true);
			mf.setType("double");
			mm.getFields().add(mf);
			i++;
		}
		
		i=1;
		//dataset_real, inteeger, datetime, char, spatial
		for(DatasetDateTime dr : d.getDatasetDateTimeSet()){
			
			mf = new MetadataField();
			mf.setName("datasetDateTime_deId"+ i);
			mf.setValue(dr.getDatasetElement().getDeId());
			mf.setRequired(true);
			mf.setType("int");
			mm.getFields().add(mf);
			
			mf = new MetadataField();
			mf.setName("datasetDateTime_value"+ i);
			mf.setValue(dr.getValueLong());
			mf.setRequired(true);
			mf.setType("double");
			mm.getFields().add(mf);
			i++;
		}
		
		i = 1;
		for(DatasetLocationPolicy dlp : d.getLocationPolicySet())
		{
			mf = new MetadataField();
			mf.setName("datasetLocationPolicy_type" + i);
			mf.setValue(dlp.getType());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); 
			
			
			mf = new MetadataField();
			mf.setName("datasetLocationPolicy_basePath"+i);
			mf.setValue(dlp.getBasePath());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); 
			i++;
		}

		i=1;
		for(DatasetMetaHistory dmh: d.getMetaHistorySet()){
			mf = new MetadataField();
			mf.setName("datasetMetaHistory_versionId");
			mf.setValue(dmh.getMetaHistoryPK().getVersionId());
			mf.setRequired(true);
			mf.setType("int");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetMetaHistory_creationDateLong");
			mf.setValue(dmh.getCreationDateLong());
			mf.setRequired(true);
			mf.setType("datetime");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetMetaHistory_lastRevisionDate");
			mf.setValue(dmh.getLastRevisionDateLong());
			mf.setRequired(true);
			mf.setType("datetime");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetMetaHistory_revisionHistory");
			mf.setValue(dmh.getRevisionHistory());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
		}
//
		i=1;
		for(DatasetParameter dp : d.getParameterSet()){
			mf = new MetadataField();
			mf.setName("datasetParameter_category"+i);
			mf.setValue(dp.getCategory());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetParameter_topic"+i);
			mf.setValue(dp.getTopic());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetParameter_term"+i);
			mf.setValue(dp.getTerm());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetParameter_variable"+i);
			mf.setValue(dp.getVariable());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetParameter_variableDetail"+i);
			mf.setValue(dp.getVariableDetail());
			mf.setRequired(false);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
			i++;
		}
		
		i=1;
		d.getRegionSet();
		
		for(DatasetRegion dr : d.getRegionSet()){ 
			mf = new MetadataField();
	
			mf.setName("datasetRegion_region"+i);
			mf.setValue(dr.getRegion());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetRegion_regionDetail"+i);
			mf.setValue(dr.getRegionDetail());
			mf.setRequired(false);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
			i++;
		}
//
		
		if(d.getDatasetPolicy() != null)
		{
			mf = new MetadataField();
			DatasetPolicy dp = d.getDatasetPolicy();
			mf.setName("datasetPolicy_dataClass");
			mf.setValue(dp.getDataClass());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetPolicy_dataFrequency");
			try{
			mf.setValue(dp.getDataFrequency());
			}catch(NullPointerException npe){};
			mf.setRequired(false);
			mf.setType("int");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetPolicy_dataVolume");
			mf.setValue(dp.getDataVolume());
			mf.setRequired(false);
			mf.setType("int");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetPolicy_dataDuration");
			mf.setValue(dp.getDataDuration());
			mf.setRequired(false);
			mf.setType("int");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetPolicy_dataLatency");
			mf.setValue(dp.getDataLatency());
			mf.setRequired(false);
			mf.setType("int");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetPolicy_accessType");
			mf.setValue(dp.getAccessType());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetPolicy_basePathAppendType");
			mf.setValue(dp.getBasePathAppendType());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetPolicy_dataFormat");
			mf.setValue(dp.getDataFormat());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetPolicy_compressType");
			mf.setValue(dp.getCompressType());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetPolicy_checksumType");
			mf.setValue(dp.getChecksumType());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetPolicy_spatialType");
			mf.setValue(dp.getSpatialType());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetPolicy_accessConstraint");
			mf.setValue(dp.getAccessConstraint());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetPolicy_useConstraint");
			mf.setValue(dp.getUseConstraint());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
			
			mf.setName("datasetPolicy_viewOnline");
			mf.setValue(dp.getViewOnline());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
			
//			//versioned
//			mf.setName("datasetPolicy_versioned");
//			if(dp.getVersioned().equals("Y"))
//				mf.setValue("true");
//			else
//				mf.setValue("false");
//			mf.setRequired(true);
//			mf.setType("boolean");
//			mm.getFields().add(mf); mf = new MetadataField();
//			
//			//versionPolicy
//			mf.setName("datasetPolicy_versionPolicy");
//			if(dp.getVersionPolicy()==null)
//				mf.setValue("");
//			else
//				mf.setValue(dp.getVersionPolicy());
//			mf.setRequired(true);
//			mf.setType("char");
//			mm.getFields().add(mf); 
			
		}
	//
		i =1;
		for(DatasetProject dp : d.getProjectSet())
		{
			mf = new MetadataField();
			mf.setName("datasetProject_projectId");
			mf.setValue(dp.getDatasetProjectPK().getProject().getProjectId());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
		}
		
		i=1;
		for(DatasetResource dr: d.getResourceSet())
		{
			mf = new MetadataField();
			mf.setName("datasetResource_name"+i);
			mf.setValue(dr.getResourceName());
			mf.setRequired(false);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetResource_path"+i);
			mf.setValue(dr.getResourcePath());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetResource_type"+i);
			mf.setValue(dr.getResourceType());
			mf.setRequired(false);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetResource_description"+i);
			mf.setValue(dr.getResourceDescription());
			mf.setRequired(false);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
			i++;
		}
		//
		for(DatasetSoftware ds : d.getSoftwareSet())
		{
			mf = new MetadataField();
			mf.setName("datasetSoftware_name");
			mf.setValue(ds.getSoftwareName());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetSoftware_type");
			mf.setValue(ds.getSoftwareType());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetSoftware_path");
			mf.setValue(ds.getSoftwarePath());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetSoftware_releaseDate");
			mf.setValue(ds.getSoftwareDateLong());
			mf.setRequired(false);
			mf.setType("datetime");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetSoftware_version");
			mf.setValue(ds.getSoftwareVersion());
			mf.setRequired(false);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetSoftware_language");
			mf.setValue(ds.getSoftwareLanguage());
			mf.setRequired(false);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetSoftware_platform");
			mf.setValue(ds.getSoftwarePlatform());
			mf.setRequired(false);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetSoftware_description");
			mf.setValue(ds.getSoftwareDescription());
			mf.setRequired(false);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();

		}
		i=1;
		for(DatasetSource ds : d.getSourceSet())
		{	
			mf = new MetadataField();
			mf.setName("datasetSource_source"+i);
			mf.setValue(ds.getDatasetSourcePK().getSource().getSourceId());
			mf.setRequired(true);
			mf.setType("int");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetSource_sensor"+i);
			mf.setValue(ds.getDatasetSourcePK().getSensor().getSensorId());
			mf.setRequired(true);
			mf.setType("int");
			mm.getFields().add(mf); mf = new MetadataField();
			i++;
	    }
		i=1;
		for(DatasetVersion dv : d.getVersionSet())
		{
			mf = new MetadataField();
			mf.setName("datasetVersion_versionId"+i);
			mf.setValue(dv.getDatasetVersionPK().getVersionId());
			mf.setRequired(true);
			mf.setType("int");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetVersion_version"+i);
			mf.setValue(dv.getVersion());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetVersion_versionDate"+i);
			mf.setValue(dv.getVersionDateLong());
			mf.setRequired(false);
			mf.setType("datetime");
			mm.getFields().add(mf); mf = new MetadataField();
	
			mf.setName("datasetVersion_descritpion"+i);
			mf.setValue(dv.getDescription());
			mf.setRequired(false);
			mf.setType("char");
			mm.getFields().add(mf); mf = new MetadataField();
			i++;
		}

		return mm;
	}
	
	public Dataset mapElementsToDateset(MetadataManifest mf, Dataset d) throws InventoryException{
		if(d == null)
			d = new Dataset();
		
		if(elements.size() == 0){
			elements = q.fetchElementDDList(new ElementDD());
		}
		
		String fname, value;
		d.getContactSet().clear();
		for(MetadataField f : mf.getFields())
		{
		//do the mappings
			fname = f.getName();
			value = f.getValue();
			if(value.equals("null"))
				value = null;
			
			/* -------------------------------------------
			 * DATASET
			 * -------------------------------------------
			 */
			try{
			if(f.getName().equals("dataset_datasetId")){
				try{d.setDatasetId(Integer.valueOf((f.getValue())));
				
				}catch(Exception e){
				}
			}
			else if(f.getName().equals("dataset_providerId")){
			
				
				d.getProvider();
				d.getProvider().setProviderId(0);
				 
				
				d.getProvider().setProviderId(Integer.valueOf((f.getValue())));
			
			
			}//dataset_persistentId
			//dataset_remoteDataset
			else if(f.getName().equals("dataset_persistentId")){
				d.setPersistentId(f.getValue());
			}
			else if(f.getName().equals("dataset_shortName")){
				d.setShortName(f.getValue());
			}
			else if(f.getName().equals("dataset_remoteDataset")){
				if(f.getValue() == null)
					{d.setRemoteDataset("L");}
				d.setRemoteDataset(f.getValue());
			}
			else if(f.getName().equals("dataset_longName")){
				d.setLongName(f.getValue());
			}
			else if(f.getName().equals("dataset_metadata")){
				d.setMetadata(f.getValue());
			}
			else if(f.getName().equals("dataset_originalProvider")){
				d.setOriginalProvider(f.getValue());
			}
			else if(f.getName().equals("dataset_providerDatasetName")){
				d.setProviderDatasetName(f.getValue());
			}
			else if(f.getName().equals("dataset_temporalRepeat")){
				d.setTemporalRepeat(f.getValue());
			}
			else if(f.getName().equals("dataset_temporalRepeatMin")){
				d.setTemporalRepeatMin(f.getValue());
			}
			else if(f.getName().equals("dataset_temporalRepeatMax")){
				d.setTemporalRepeatMax(f.getValue());
			}
			else if(f.getName().equals("dataset_processingLevel")){
				d.setProcessingLevel(f.getValue());
			}
			else if(f.getName().equals("dataset_latitudeResolution")){
				try{d.setLatitudeResolution(Double.valueOf(f.getValue()));}
				catch(Exception e){
					
					d.setLatitudeResolution(null);
				}
			}
			else if(f.getName().equals("dataset_longitudeResolution")){
				try{d.setLongitudeResolution(Double.valueOf(value));
				
				}catch(Exception e){
					d.setLongitudeResolution(null);
				}
			}
			else if(f.getName().equals("dataset_sampleFrequency")){
				try{d.setSampleFrequency(Float.valueOf(value));
				
				}catch(Exception e){
					d.setSampleFrequency(null);
				}
			}
			else if(f.getName().equals("dataset_swathWidth")){
				try{d.setSwathWidth(Float.valueOf(value));
				
				}catch(Exception e){
					d.setSwathWidth(null);
				}
			}
			else if(f.getName().equals("dataset_acrossTrackResolution")){
				try{d.setAcrossTrackResolution(Integer.valueOf(value));
				
				}catch(Exception E){

				}
			}
			else if(f.getName().equals("dataset_alongTrackResolution")){
			try{	d.setAlongTrackResolution(Integer.valueOf(value));
			}catch(Exception E){	
			}
			}
			else if(f.getName().equals("dataset_ascendingNodeTime")){
				try{
					d.setAscendingNodeTime(Long.valueOf(value));
				}catch(NumberFormatException nfe){
					
					d.setAscendingNodeTime(null);
				}
			}
			else if(f.getName().equals("dataset_horizontalResolutionRange")){
					d.setHorizontalResolutionRange(value);
			}
			else if(f.getName().equals("dataset_altitudeResolution")){
				d.setAltitudeResolution(value);
			}
			else if(f.getName().equals("dataset_depthResolution")){
				d.setDepthResolution(value);
			}
			else if(f.getName().equals("dataset_temporalResolution")){
				d.setTemporalResolution(value);
			}
			else if(f.getName().equals("dataset_temporalResolutionRange")){
				d.setTemporalResolutionRange(value);
			}
			else if(f.getName().equals("dataset_ellipsoidType")){
				d.setEllipsoidType(value);
			}
			else if(f.getName().equals("dataset_projectionType")){
				d.setProjectionType(value);
			}
			else if(f.getName().equals("dataset_projectionDetail")){
				d.setProjectionDetail(value);
			}
			else if(f.getName().equals("dataset_reference")){
				d.setReference(value);
			}
			else if(f.getName().equals("dataset_description")){
				d.setDescription(value);
			}
			else if(f.getName().equals("dataset_note")){
				d.setNote(value);
			}
			
			/* -------------------------------------------
			 * CITATION
			 * -------------------------------------------
			 */
			else if(f.getName().equals("datasetCitation_title")){
				DatasetCitation dc = getCitation(d);
				dc.setTitle(value);
				addCitation(d,dc);
			}
			else if(f.getName().equals("datasetCitation_creator")){
				DatasetCitation dc = getCitation(d);
				dc.setCreator(value);
				addCitation(d,dc);
			}
			else if(f.getName().equals("datasetCitation_version")){
				DatasetCitation dc = getCitation(d);
				dc.setVersion(value);
				addCitation(d,dc);
			}
			else if(f.getName().equals("datasetCitation_publisher")){
				DatasetCitation dc = getCitation(d);
				dc.setPublisher(value);
				addCitation(d,dc);
			}
			else if(f.getName().equals("datasetCitation_seriesName")){
				DatasetCitation dc = getCitation(d);
				dc.setSeriesName(value);
				addCitation(d,dc);
			}
			else if(f.getName().equals("datasetCitation_releaseDate")){
				DatasetCitation dc = getCitation(d);
				try{dc.setReleaseDateLong(Long.valueOf(value));
				}catch(Exception e){}
				
				addCitation(d,dc);
			}
			else if(f.getName().equals("datasetCitation_releasePlace")){
				DatasetCitation dc = getCitation(d);
				dc.setReleasePlace(value);
				addCitation(d,dc);
			}
			else if(f.getName().equals("datasetCitation_citationDetail")){
				DatasetCitation dc = getCitation(d);
				dc.setCitationDetail(value);
				addCitation(d,dc);
			}
			else if(f.getName().equals("datasetCitation_onlineResource")){
				DatasetCitation dc = getCitation(d);
				dc.setOnlineResource(value);
				addCitation(d,dc);
			}
			/* -------------------------------------------
			 * CONTACT
			 * -------------------------------------------
			 */
			else if(f.getName().startsWith("datasetContact_contactId")){
				contactIds.add(Integer.valueOf(value));
		
			}
			/* -------------------------------------------
			 * DATASET COLLECTION
			 * -------------------------------------------
			 */
			else if(f.getName().startsWith("datasetCollection_CollectionId")){
				
				int idx = getEndingDigit("datasetCollection_CollectionId".length(), fname);
				CollectionDataset cd = null;
				cd = (CollectionDataset) findElement(cds, idx);
				if(cd == null){
					cd = new CollectionDataset();
					indexObject iox = new indexObject(idx,cd);
					cds.add(iox);
				}

				cd.getCollectionDatasetPK().getCollection().setCollectionId(Integer.valueOf(value));
				log.debug("Got collectionDataset with id: " + Integer.valueOf(value));
				//cds.set(idx, cd);
			}
			
			else if(f.getName().startsWith("datasetCollection_granuleFlag")){
				int idx = getEndingDigit("datasetCollection_granuleFlag".length(), fname);
				CollectionDataset cd = null;
				cd = (CollectionDataset) findElement(cds, idx);
				if(cd == null){
					cd = new CollectionDataset();
					indexObject iox = new indexObject(idx,cd);
					cds.add(iox);
				}
				cd.setGranuleFlag((value.charAt(0)));
			}
			
			else if(f.getName().startsWith("datasetCollection_granuleRange360")){
				int idx = getEndingDigit("datasetCollection_granuleRange360".length(), fname);
				CollectionDataset cd = null;
				cd = (CollectionDataset) findElement(cds, idx);
				if(cd == null){
					cd = new CollectionDataset();
					indexObject iox = new indexObject(idx,cd);
					cds.add(iox);
				}
				cd.setGranuleRange360((value.charAt(0)));
				
			}
			else if(f.getName().startsWith("datasetCollection_startGranuleId")){
				int idx = getEndingDigit("datasetCollection_startGranuleId".length(), fname);
				CollectionDataset cd = null;
				cd = (CollectionDataset) findElement(cds, idx);
				if(cd == null){
					cd = new CollectionDataset();
					indexObject iox = new indexObject(idx,cd);
					cds.add(iox);
				}
				try{cd.setStartGranuleId(Integer.valueOf(value));
				}catch(Exception e){
					
				}
				//cds.set(idx, cd);
			}
			else if(f.getName().startsWith("datasetCollection_stopGranuleId")){
				int idx = getEndingDigit("datasetCollection_stopGranuleId".length(), fname);
				CollectionDataset cd = null;
				cd = (CollectionDataset) findElement(cds, idx);
				if(cd == null){
					cd = new CollectionDataset();
					indexObject iox = new indexObject(idx,cd);
					cds.add(iox);
				}
				try{cd.setStopGranuleId(Integer.valueOf(value));
				}catch(Exception e){}
				//cds.set(idx, cd);
			}
			
			
			/* -------------------------------------------
			 * DATASET COVERAGE
			 * -------------------------------------------
			 */
			else if(f.getName().equals("datasetCoverage_startTime")){
				d.getDatasetCoverage().setStartTimeLong(Long.valueOf(value));
			}
			else if(f.getName().equals("datasetCoverage_stopTime")){
				try{d.getDatasetCoverage().setStopTimeLong(Long.valueOf(value));
				}catch(Exception e){
				}
			}
			else if(f.getName().equals("datasetCoverage_northernmostLatitude")){
				try{d.getDatasetCoverage().setNorthLat(Double.valueOf(value));
				}catch(Exception e){}
			}
			else if(f.getName().equals("datasetCoverage_southernmostLatitude")){
				try{d.getDatasetCoverage().setSouthLat(Double.valueOf(value));
				}catch(Exception e){}
			}
			else if(f.getName().equals("datasetCoverage_easternmostLongitude")){
				try{d.getDatasetCoverage().setEastLon(Double.valueOf(value));
				}catch(Exception e){}
			}
			else if(f.getName().equals("datasetCoverage_westernmostLongitude")){
				try{d.getDatasetCoverage().setWestLon(Double.valueOf(value));
				}catch(Exception e){}
			}
			else if(f.getName().equals("datasetCoverage_minAltitude")){
				try{d.getDatasetCoverage().setMinAltitude(Double.valueOf(value));
				}catch(Exception e){}
			}
			else if(f.getName().equals("datasetCoverage_maxAltitude")){
				try{d.getDatasetCoverage().setMaxAltitude(Double.valueOf(value));
				}catch(Exception e){}
			}
			else if(f.getName().equals("datasetCoverage_minDepth")){
				try{d.getDatasetCoverage().setMinDepth(Double.valueOf(value));
				}catch(Exception e){}
			}
			else if(f.getName().equals("datasetCoverage_maxDepth")){
				try{d.getDatasetCoverage().setMaxDepth(Double.valueOf(value));
				}catch(Exception e){}
			}
 			else if(f.getName().equals("datasetCoverage_granuleRange360")){
                                try{d.getDatasetCoverage().setGranuleRange360(value.charAt(0));
                                }catch(Exception e){}
                        }
			
			/* -------------------------------------------
			 * ELEMENT
			 * -------------------------------------------
			 */
			else if(f.getName().startsWith(("datasetElement_deId"))){
				//we need to add it here, but remove it later if an add is occuring. 
//				if(isAdd)
//					log.debug("Skipping datasetElement deID section, new dataset");
//				else{
					int idx = this.getEndingDigit("datasetElement_deId".length(), f.getName());
				
					DatasetElement de = (DatasetElement) findElement(datasetElements,idx);
					if(de == null)
					{	
						de = new DatasetElement();
						indexObject iox = new indexObject(idx,de);
						datasetElements.add(iox);
					}
					//System.out.println("idx: "+idx+",DeID: " + value);
					de.setDeId(Integer.valueOf(value));
					//datasetElements.add(idx, de);
//				}
			}
			else if(f.getName().startsWith("datasetElement_elementId")){
				
				int idx = this.getEndingDigit("datasetElement_elementId".length(), f.getName());

				
				DatasetElement de =  (DatasetElement) findElement(datasetElements,idx);
				if(de == null)
				{	
					de = new DatasetElement();
					indexObject iox = new indexObject(idx,de);
					datasetElements.add(iox);
				}
				Iterator<ElementDD> it = elements.iterator();
				while(it.hasNext())
				{
					ElementDD ed = it.next();
					if(ed.getElementId().equals(Integer.valueOf(value)))
					{
						de.setElementDD(ed);
					}
				}
				//datasetElements.add(idx, de);
			}
			else if(f.getName().startsWith("datasetElement_obligationFlag")){
				int idx = this.getEndingDigit("datasetElement_obligationFlag".length(), f.getName());
				
				DatasetElement de = (DatasetElement) findElement(datasetElements,idx);
				if(de == null)
				{	
					de = new DatasetElement();
					indexObject iox = new indexObject(idx,de);
					datasetElements.add(iox);
				}
				de.setObligationFlag(value.charAt(0));
				//datasetElements.add(idx, de);
			}
			else if(f.getName().startsWith("datasetElement_scope")){
				int idx = this.getEndingDigit("datasetElement_scope".length(), f.getName());
				
				DatasetElement de = (DatasetElement) findElement(datasetElements,idx);
				if(de == null)
				{	
					de = new DatasetElement();
					indexObject iox = new indexObject(idx,de);
					datasetElements.add(iox);
				}
				de.setScope(value);
				
				
			}
			
			/* -------------------------------------------
			 * LOCATION POLICY
			 * -------------------------------------------
			 */
			else if(f.getName().startsWith("datasetLocationPolicy_type")){
				int idx = this.getEndingDigit("datasetLocationPolicy_type".length(), f.getName());
				DatasetLocationPolicy dlp = (DatasetLocationPolicy) findElement(datasetLocPolicies,idx);
				if(dlp == null)
				{	
					dlp = new DatasetLocationPolicy();
					indexObject iox = new indexObject(idx,dlp);
					datasetLocPolicies.add(iox);
				}
				dlp.setType(value);
			}
			else if(f.getName().startsWith("datasetLocationPolicy_basePath")){
				int idx = this.getEndingDigit("datasetLocationPolicy_basePath".length(), f.getName());
				DatasetLocationPolicy dlp = (DatasetLocationPolicy) findElement(datasetLocPolicies,idx);
				if(dlp == null)
				{	
					dlp = new DatasetLocationPolicy();
					indexObject iox = new indexObject(idx,dlp);
					datasetLocPolicies.add(iox);
				}
				dlp.setBasePath(value);
			}
			
			/* -------------------------------------------
			 * DATASET REALS
			 * -------------------------------------------
			 */
			else if(f.getName().startsWith("datasetReal_deId")){
				int idx = this.getEndingDigit("datasetReal_deId".length(), f.getName());
				DatasetReal dr =(DatasetReal) findElement(reals,idx);
				if(dr == null)
				{	
					dr = new DatasetReal();
					indexObject iox = new indexObject(idx,dr);
					reals.add(iox);
				}
				dr.getDatasetElement().setDeId(Integer.valueOf(value));
				//reals.add(idx, dr);
			}
			else if(f.getName().startsWith("datasetReal_elementId")){
			//	System.out.println("Setting elementId for Real: " + value);
				int idx = this.getEndingDigit("datasetReal_elementId".length(), f.getName());
				DatasetReal dr = (DatasetReal) findElement(reals,idx);
				if(dr == null)
				{	
					dr = new DatasetReal();
					indexObject iox = new indexObject(idx,dr);
					reals.add(iox);
				}
				dr.getDatasetElement().getElementDD().setElementId(Integer.valueOf(value));
				//reals.add(idx, dr);
			}
			else if(f.getName().startsWith("datasetReal_units")){
				int idx = this.getEndingDigit("datasetReal_units".length(), f.getName());
				DatasetReal dr = (DatasetReal) findElement(reals,idx);
				if(dr == null)
				{	
					dr = new DatasetReal();
					indexObject iox = new indexObject(idx,dr);
					reals.add(iox);
				}
				dr.setUnits(value);
				//reals.add(idx, dr);
			}
			else if(f.getName().startsWith("datasetReal_value")){
				int idx = this.getEndingDigit("datasetReal_value".length(), f.getName());
				DatasetReal dr = (DatasetReal) findElement(reals,idx);
				if(dr == null)
				{	
					dr = new DatasetReal();
					indexObject iox = new indexObject(idx,dr);
					reals.add(iox);
				}
				dr.setValue(Double.valueOf(value));
				//reals.add(idx, dr);
			}
			
			/* -------------------------------------------
			 * DATASET Integers
			 * -------------------------------------------
			 */
			else if(f.getName().startsWith("datasetInteger_deId")){
				int idx = this.getEndingDigit("datasetInteger_deId".length(), f.getName());
				DatasetInteger dr = (DatasetInteger) findElement(ints,idx);
				if(dr == null)
				{	
					dr = new DatasetInteger();
					indexObject iox = new indexObject(idx,dr);
					ints.add(iox);
				}
				dr.getDatasetElement().setDeId(Integer.valueOf(value));
				//ints.add(idx, dr);
			}
			else if(f.getName().startsWith("datasetInteger_elementId")){
				int idx = this.getEndingDigit("datasetInteger_elementId".length(), f.getName());
				DatasetInteger dr = (DatasetInteger) findElement(ints,idx);
				if(dr == null)
				{	
					dr = new DatasetInteger();
					indexObject iox = new indexObject(idx,dr);
					ints.add(iox);
				}
				dr.getDatasetElement().getElementDD().setElementId(Integer.valueOf(value));
				//reals.add(idx, dr);
			}
			else if(f.getName().startsWith("datasetInteger_units")){
				int idx = this.getEndingDigit("datasetInteger_units".length(), f.getName());
				DatasetInteger dr = (DatasetInteger) findElement(ints,idx);
				if(dr == null)
				{	
					dr = new DatasetInteger();
					indexObject iox = new indexObject(idx,dr);
					ints.add(iox);
				}
				dr.setUnits(value);
				//ints.add(idx, dr);
			}
			else if(f.getName().startsWith("datasetInteger_value")){
				int idx = this.getEndingDigit("datasetInteger_value".length(), f.getName());
				DatasetInteger dr = (DatasetInteger) findElement(ints,idx);
				if(dr == null)
				{	
					dr = new DatasetInteger();
					indexObject iox = new indexObject(idx,dr);
					ints.add(iox);
				}
				dr.setValue(Integer.valueOf(value));
				//ints.add(idx, dr);
			}
			
			/* -------------------------------------------
			 * DATASET CHARS
			 * -------------------------------------------
			 */
			else if(f.getName().startsWith("datasetCharacter_deId")){
				int idx = this.getEndingDigit("datasetCharacter_deId".length(), f.getName());
				DatasetCharacter dr = (DatasetCharacter) findElement(chars,idx);
				if(dr == null)
				{	
					dr = new DatasetCharacter();
					indexObject iox = new indexObject(idx,dr);
					chars.add(iox);
				}
				dr.getDatasetElement().setDeId(Integer.valueOf(value));
			}
			else if(f.getName().startsWith("datasetCharacter_elementId")){
				//System.out.println("CharacterElementId: " + value);
				int idx = this.getEndingDigit("datasetCharacter_elementId".length(), f.getName());
				DatasetCharacter dr = (DatasetCharacter) findElement(chars,idx);
				if(dr == null)
				{	
					dr = new DatasetCharacter();
					indexObject iox = new indexObject(idx,dr);
					chars.add(iox);
				}
				dr.getDatasetElement().getElementDD().setElementId(Integer.valueOf(value));
				//reals.add(idx, dr);
			}
			else if(f.getName().startsWith("datasetCharacter_value")){
				int idx = this.getEndingDigit("datasetCharacter_value".length(), f.getName());
				DatasetCharacter dr = (DatasetCharacter) findElement(chars,idx);
				if(dr == null)
				{	
					dr = new DatasetCharacter();
					indexObject iox = new indexObject(idx,dr);
					chars.add(iox);
				}
				dr.setValue(value);
			}
			
			/* -------------------------------------------
			 * DATASET DATETIME
			 * -------------------------------------------
			 */
			else if(f.getName().startsWith("datasetDateTime_deId")){
				int idx = this.getEndingDigit("datasetDateTime_deId".length(), f.getName());
				DatasetDateTime dr = (DatasetDateTime) findElement(datetime,idx);
				if(dr == null)
				{	
					dr = new DatasetDateTime();
					indexObject iox = new indexObject(idx,dr);
					datetime.add(iox);
				}
				dr.getDatasetElement().setDeId(Integer.valueOf(value));
			}
			else if(f.getName().startsWith("datasetDateTime_elementId")){
				int idx = this.getEndingDigit("datasetDateTime_elementId".length(), f.getName());
				DatasetDateTime dr = (DatasetDateTime) findElement(datetime,idx);
				if(dr == null)
				{	
					dr = new DatasetDateTime();
					indexObject iox = new indexObject(idx,dr);
					datetime.add(iox);
				}
				dr.getDatasetElement().getElementDD().setElementId(Integer.valueOf(value));
				//reals.add(idx, dr);
			}
			else if(f.getName().startsWith("datasetDateTime_value")){
				int idx = this.getEndingDigit("datasetDateTime_value".length(), f.getName());
				DatasetDateTime dr = (DatasetDateTime) findElement(datetime,idx);
				if(dr == null)
				{	
					dr = new DatasetDateTime();
					indexObject iox = new indexObject(idx,dr);
					datetime.add(iox);
				}
				dr.setValueLong(Long.valueOf(value));
			}
			
			/* -------------------------------------------
			 * METAHISTORY
			 * -------------------------------------------
			 */
			else if(f.getName().equals("datasetMetaHistory_versionId")){
				DatasetMetaHistory dmh = getMetaHistory(d);
				dmh.getMetaHistoryPK().setVersionId(Integer.valueOf(value));
				addMetaHistory(d,dmh);
			}
			else if(f.getName().equals("datasetMetaHistory_creationDateLong")){
				DatasetMetaHistory dmh = getMetaHistory(d);
				dmh.setCreationDateLong(Long.valueOf(value));
				addMetaHistory(d,dmh);
			}
			else if(f.getName().equals("datasetMetaHistory_lastRevisionDate")){
				DatasetMetaHistory dmh = getMetaHistory(d);
				dmh.setLastRevisionDateLong(new Date().getTime()); //should we set the time here? Yes, so we always get the most recent time in the LRT
				addMetaHistory(d,dmh);
			}
			else if(f.getName().equals("datasetMetaHistory_revisionHistory")){
				DatasetMetaHistory dmh = getMetaHistory(d);
				dmh.setRevisionHistory(value);
				addMetaHistory(d,dmh);
			}
			
				
			/* -------------------------------------------
			 * POLICY
			 * -------------------------------------------
			 */
			else if(f.getName().equals("datasetPolicy_dataClass")){
				d.getDatasetPolicy().setDataClass(value);
			}
			else if(f.getName().equals("datasetPolicy_dataFrequency")){
				try{d.getDatasetPolicy().setDataFrequency(Integer.valueOf(value));
				}catch(Exception e){}
				
			}
			else if(f.getName().equals("datasetPolicy_dataVolume")){
				try{d.getDatasetPolicy().setDataVolume(Integer.valueOf(value));
				}catch(Exception e){}
			}
			else if(f.getName().equals("datasetPolicy_dataDuration")){
				try{d.getDatasetPolicy().setDataDuration(Integer.valueOf(value));
				}catch(Exception e){}
			}
			else if(f.getName().equals("datasetPolicy_dataLatency")){
				try{d.getDatasetPolicy().setDataLatency(Integer.valueOf(value));
				}catch(Exception e){}
			}
			else if(f.getName().equals("datasetPolicy_accessType")){
				d.getDatasetPolicy().setAccessType(value);
			}
			else if(f.getName().equals("datasetPolicy_basePathAppendType")){
				d.getDatasetPolicy().setBasePathAppendType(value);
			}
			else if(f.getName().equals("datasetPolicy_dataFormat")){
				d.getDatasetPolicy().setDataFormat(value);
			}
			else if(f.getName().equals("datasetPolicy_compressType")){
				d.getDatasetPolicy().setCompressType(value);	
			}
			else if(f.getName().equals("datasetPolicy_checksumType")){
				d.getDatasetPolicy().setChecksumType(value);
			}
			else if(f.getName().equals("datasetPolicy_spatialType")){
				d.getDatasetPolicy().setSpatialType(value);	
			}
			else if(f.getName().equals("datasetPolicy_accessConstraint")){
				d.getDatasetPolicy().setAccessConstraint(value);	
			}
			else if(f.getName().equals("datasetPolicy_useConstraint")){
				d.getDatasetPolicy().setUseConstraint(value);
			}
			else if(f.getName().equals("datasetPolicy_viewOnline")){
				d.getDatasetPolicy().setViewOnline(value);
			}
//			else if(f.getName().equals("datasetPolicy_versioned")){
//				if(value.equals("true"))
//					d.getDatasetPolicy().setVersioned("Y");
//				else
//					d.getDatasetPolicy().setVersioned("F");
//			}
//			else if(f.getName().equals("datasetPolicy_versionPolicy")){
//				try{if(!value.equals("null"))
//				d.getDatasetPolicy().setVersionPolicy(value);
//				}catch(Exception e){}
//			}
			
			/* -------------------------------------------
			 * PROJECT
			 * -------------------------------------------
			 */
			else if(f.getName().equals("datasetProject_projectId")){
				if(d.getProjectSet().isEmpty())
				{
					DatasetProject p = new DatasetProject();
					Project proj = new Project();
					proj.setProjectId(Integer.valueOf(value));
					p.getDatasetProjectPK().setProject(proj);
					p.getDatasetProjectPK().setDataset(d);
					d.getProjectSet().add(p);
				}
				else{
					//d.getProjectSet().clear();
					DatasetProject p = new DatasetProject();
					Project proj = new Project();
					proj.setProjectId(Integer.valueOf(value));
					p.getDatasetProjectPK().setProject(proj);
					p.getDatasetProjectPK().setDataset(d);
					d.getProjectSet().add(p);
				}
			}
			

			/* -------------------------------------------
			 * SOFTWARE
			 * -------------------------------------------
			 */
			else if(f.getName().equals("datasetSoftware_name")){
				DatasetSoftware ds = getSoftware(d);
				ds.setSoftwareName(value);
				addSoftware(d,ds);
			}
			else if(f.getName().equals("datasetSoftware_type")){
				DatasetSoftware ds = getSoftware(d);
				ds.setSoftwareType(value);
				addSoftware(d,ds);
			}
			else if(f.getName().equals("datasetSoftware_path")){
				DatasetSoftware ds = getSoftware(d);
				ds.setSoftwarePath(value);
				addSoftware(d,ds);
			}
			else if(f.getName().equals("datasetSoftware_releaseDate")){
				DatasetSoftware ds = getSoftware(d);
				try{ds.setSoftwareDateLong(Long.valueOf(value));
				}catch(Exception e){}
				addSoftware(d,ds);
			}
			else if(f.getName().equals("datasetSoftware_version")){
				DatasetSoftware ds = getSoftware(d);
				ds.setSoftwareVersion(value);
				addSoftware(d,ds);
			}
			else if(f.getName().equals("datasetSoftware_language")){
				DatasetSoftware ds = getSoftware(d);
				ds.setSoftwareLanguage(value);
				addSoftware(d,ds);
			}
			else if(f.getName().equals("datasetSoftware_platform")){
				DatasetSoftware ds = getSoftware(d);
				ds.setSoftwarePlatform(value);
				addSoftware(d,ds);
			}
			else if(f.getName().equals("datasetSoftware_description")){
				DatasetSoftware ds = getSoftware(d);
				ds.setSoftwareDescription(value);
				addSoftware(d,ds);
			}
			/* -------------------------------------------
			 * PARAMETER
			 * -------------------------------------------
			 */
			else if(f.getName().startsWith("datasetParameter_category")){
				int idx = this.getEndingDigit("datasetParameter_category".length(), f.getName());
				DatasetParameter dp = (DatasetParameter) findElement(parameters,idx);
				if(dp == null)
				{	
					dp = new DatasetParameter();
					indexObject iox = new indexObject(idx,dp);
					parameters.add(iox);
				}
				dp.setCategory(value);
				//parameters.add(idx, dp);
			}
			else if(f.getName().startsWith("datasetParameter_topic")){
				int idx = this.getEndingDigit("datasetParameter_topic".length(), f.getName());
				
				DatasetParameter dp = (DatasetParameter) findElement(parameters,idx);
				if(dp == null)
				{	
					dp = new DatasetParameter();
					indexObject iox = new indexObject(idx,dp);
					parameters.add(iox);
				}
				dp.setTopic(value);
			//	parameters.add(idx, dp);
			}
			else if(f.getName().startsWith("datasetParameter_term")){
				int idx = this.getEndingDigit("datasetParameter_term".length(), f.getName());
				
				DatasetParameter dp = (DatasetParameter) findElement(parameters,idx);
				if(dp == null)
				{	
					dp = new DatasetParameter();
					indexObject iox = new indexObject(idx,dp);
					parameters.add(iox);
				}
				dp.setTerm(value);
				//parameters.add(idx, dp);
			}
			else if(f.getName().startsWith("datasetParameter_variableDetail")){
				int idx = this.getEndingDigit("datasetParameter_variableDetail".length(), f.getName());
				
				DatasetParameter dp = (DatasetParameter) findElement(parameters,idx);
				if(dp == null)
				{	
					dp = new DatasetParameter();
					indexObject iox = new indexObject(idx,dp);
					parameters.add(iox);
				}
				dp.setVariableDetail(value);
				//parameters.add(idx, dp);
			}
			else if(f.getName().startsWith("datasetParameter_variable")){
				int idx = this.getEndingDigit("datasetParameter_variable".length(), f.getName());
				DatasetParameter dp = (DatasetParameter) findElement(parameters,idx);
				if(dp == null)
				{	
					dp = new DatasetParameter();
					indexObject iox = new indexObject(idx,dp);
					parameters.add(iox);
				}
				dp.setVariable(value);
				//parameters.add(idx, dp);
			}
			/*
			 * REGIONS
			 */
			 
			else if(f.getName().startsWith("datasetRegion_regionDetail")){
				int idx = this.getEndingDigit("datasetRegion_regionDetail".length(), f.getName());
				DatasetRegion dp = (DatasetRegion) findElement(regions,idx);
				if(dp == null)
				{	
					dp = new DatasetRegion();
					indexObject iox = new indexObject(idx,dp);
					regions.add(iox);
				}
				dp.setRegionDetail(value);
				//parameters.add(idx, dp);
			}
			else if(f.getName().startsWith("datasetRegion_region")){
				int idx = this.getEndingDigit("datasetRegion_region".length(), f.getName());
				
				DatasetRegion dp = (DatasetRegion) findElement(regions,idx);
				if(dp == null)
				{	
					dp = new DatasetRegion();
					indexObject iox = new indexObject(idx,dp);
					regions.add(iox);
				}
				dp.setRegion(value);
				//parameters.add(idx, dp);
			}
			
			/* -------------------------------------------
			 * RESOURCE
			 * -------------------------------------------
			 */
			else if(f.getName().startsWith("datasetResource_name")){
				int idx = this.getEndingDigit("datasetResource_name".length(), f.getName());
				DatasetResource dr = (DatasetResource) findElement(resources,idx);
				if(dr == null)
				{	
					dr = new DatasetResource();
					indexObject iox = new indexObject(idx,dr);
					resources.add(iox);
				}
				dr.setResourceName((value));
			}
			else if(f.getName().startsWith("datasetResource_path")){
				int idx = this.getEndingDigit("datasetResource_path".length(), f.getName());
				DatasetResource dr = (DatasetResource) findElement(resources,idx);
				if(dr == null)
				{	
					dr = new DatasetResource();
					indexObject iox = new indexObject(idx,dr);
					resources.add(iox);
				}
				dr.setResourcePath((value));
			}
			else if(f.getName().startsWith("datasetResource_type")){
				int idx = this.getEndingDigit("datasetResource_type".length(), f.getName());
				DatasetResource dr = (DatasetResource) findElement(resources,idx);
				if(dr == null)
				{	
					dr = new DatasetResource();
					indexObject iox = new indexObject(idx,dr);
					resources.add(iox);
				}
				dr.setResourceType((value));
			}
			else if(f.getName().startsWith("datasetResource_description")){
				int idx = this.getEndingDigit("datasetResource_description".length(), f.getName());
				DatasetResource dr = (DatasetResource) findElement(resources,idx);
				if(dr == null)
				{	
					dr = new DatasetResource();
					indexObject iox = new indexObject(idx,dr);
					resources.add(iox);
				}
				dr.setResourceDescription((value));
			}
			
			/* -------------------------------------------
			 * SOURCE
			 * -------------------------------------------
			 */
			else if(f.getName().startsWith("datasetSource_source")){
				int idx = this.getEndingDigit("datasetSource_source".length(), f.getName());
				DatasetSource ds = null;
				ds = (DatasetSource) findElement(sources, idx);
				if(ds == null)
				{	
					ds = new DatasetSource();
					indexObject iox = new indexObject(idx,ds);
					sources.add(iox);
				}
				Source s = new Source();
				s.setSourceId(Integer.valueOf(value));
				ds.getDatasetSourcePK().setSource(s);
				
			}
			else if(f.getName().startsWith("datasetSource_sensor")){
				int idx = this.getEndingDigit("datasetSource_sensor".length(), f.getName());
				DatasetSource ds = null;
				ds = (DatasetSource) findElement(sources, idx);
				if(ds == null){
					ds = new DatasetSource();
					indexObject iox = new indexObject(idx,ds);
					sources.add(iox);
				}
				Sensor s = new Sensor();
				s.setSensorId(Integer.valueOf(value));
				ds.getDatasetSourcePK().setSensor(s);
			}

			/* -------------------------------------------
			 * VERSION
			 * -------------------------------------------
			 */
			else if(f.getName().startsWith("datasetVersion_versionId")){
				int idx = this.getEndingDigit("datasetVersion_versionId".length(), f.getName());
				DatasetVersion dv = (DatasetVersion) findElement(versions,idx);
				if(dv == null)
				{	
					dv = new DatasetVersion();
					indexObject iox = new indexObject(idx,dv);
					versions.add(iox);
				}
				dv.getDatasetVersionPK().setVersionId(Integer.valueOf(value));
			}
			else if(f.getName().startsWith("datasetVersion_versionDate")){
				int idx = this.getEndingDigit("datasetVersion_versionDate".length(), f.getName());
				DatasetVersion dv = (DatasetVersion) findElement(versions,idx);
				if(dv == null)
				{	
					dv = new DatasetVersion();
					indexObject iox = new indexObject(idx,dv);
					versions.add(iox);
				}
				try{dv.setVersionDateLong(Long.valueOf(value));
				}catch(Exception e){}
			}
			else if(f.getName().startsWith("datasetVersion_version")){
				int idx = this.getEndingDigit("datasetVersion_version".length(), f.getName());
				DatasetVersion dv = (DatasetVersion) findElement(versions,idx);
				if(dv == null)
				{	
					dv = new DatasetVersion();
					indexObject iox = new indexObject(idx,dv);
					versions.add(iox);
				}
				dv.setVersion(value);
			}
			else if(f.getName().startsWith("datasetVersion_descritpion")){
				int idx = this.getEndingDigit("datasetVersion_descritpion".length(), f.getName());
				DatasetVersion dv = (DatasetVersion) findElement(versions,idx);
				if(dv == null)
				{	
					dv = new DatasetVersion();
					indexObject iox = new indexObject(idx,dv);
					versions.add(iox);
				}
				dv.setDescription(value);
			}
			}
			catch(Exception e){
				e.printStackTrace();
				throw new InventoryException("Field/Value pair in Manifest caused exception: " + fname + ":" + value);
			}

		}	
		
		//checks
		if(d.getAcrossTrackResolution() ==null){
			
			//d.setAcrossTrackResolution(0);
		}
		if(d.getAlongTrackResolution() ==null){
			//d.setAlongTrackResolution(0);
		}
//		if(d.getAscendingNodeTime() == null){
//			d.setAscendingNodeTime(0L);
//		}
		try{
		if(d.getLatitudeResolution() == null){
			d.setLatitudeResolution(null);
		}}
		catch(NullPointerException npe){
			d.setLatitudeResolution(null);
		}
		try{
		if(d.getLongitudeResolution() == null){
			d.setLongitudeResolution(null);
		}}
		catch(NullPointerException npe){
			d.setLongitudeResolution(null);
		}
		
		
		//reals
		for(indexObject o: reals){
			DatasetReal dr  = (DatasetReal) o.o;
			for(indexObject iox :datasetElements){
				DatasetElement dEl = (DatasetElement) iox.o;
				if(dr.getDatasetElement().getDeId() != null){
					if(dr.getDatasetElement().getDeId().equals(dEl.getDeId())){
						dr.getDatasetElement().getElementDD().setElementId(dEl.getElementDD().getElementId());
					}
				}
			}
		}
		
		for(indexObject o: chars){
			DatasetCharacter dr  = (DatasetCharacter) o.o;
			for(indexObject iox :datasetElements){
				DatasetElement dEl = (DatasetElement) iox.o;
				if(dr.getDatasetElement().getDeId() != null){
					if(dr.getDatasetElement().getDeId().equals(dEl.getDeId())){
						dr.getDatasetElement().getElementDD().setElementId(dEl.getElementDD().getElementId());
					}
				}
			}
		}
		
		for(indexObject o: ints){
			DatasetInteger dr  = (DatasetInteger) o.o;
			for(indexObject iox :datasetElements){
				DatasetElement dEl = (DatasetElement) iox.o;
				if(dr.getDatasetElement().getDeId() != null){
					if(dr.getDatasetElement().getDeId().equals(dEl.getDeId())){
						dr.getDatasetElement().getElementDD().setElementId(dEl.getElementDD().getElementId());
					}
				}
			}
		}
		for(indexObject o: datetime){
			DatasetDateTime dr  = (DatasetDateTime) o.o;
			for(indexObject iox :datasetElements){
				DatasetElement dEl = (DatasetElement) iox.o;
				if(dr.getDatasetElement().getDeId() != null){
					if(dr.getDatasetElement().getDeId().equals(dEl.getDeId())){
						dr.getDatasetElement().getElementDD().setElementId(dEl.getElementDD().getElementId());
					}
				}
			}
		}
		
		
		if(isAdd){
			log.debug("removing DEIds");
			for(indexObject iox :datasetElements){
				DatasetElement dEl = (DatasetElement) iox.o;
				dEl.setDeId(null);
			}
		}
	
	
		if(!datasetElements.isEmpty()){
			d.getDatasetElementSet().clear();
			Iterator<indexObject> it = datasetElements.iterator();
			while(it.hasNext()){
				DatasetElement de =(DatasetElement)it.next().o;
				if(de.getElementDD().getElementId()!=null || de.getDeId() != null){

					de.setDataset(d);
					d.getDatasetElementSet().add(de);
				}
			}
		}
		//System.out.println("element List size: " + d.getDatasetElementSet().size());
		
		//add params
		if(!parameters.isEmpty()){
			d.getParameterSet().clear();
			Iterator<indexObject> it = parameters.iterator();
			while(it.hasNext()){
				DatasetParameter dp =(DatasetParameter) it.next().o;
				if(dp.getCategory()!=null){
					//System.out.println(dp.getCategory());
					d.getParameterSet().add(dp);
				}
			}
		}
		if(!regions.isEmpty()){
			d.getRegionSet().clear();
			Iterator<indexObject> it = regions.iterator();
			while(it.hasNext()){
				DatasetRegion dp = (DatasetRegion)it.next().o;
				if(dp.getRegion()!=null){
					//System.out.println(dp.getCategory());
					d.getRegionSet().add(dp);
				}
			}
		}
		//add resources
		if(!resources.isEmpty()){
			d.getResourceSet().clear();
			Iterator<indexObject> it = resources.iterator();
			while(it.hasNext()){
				DatasetResource dr = (DatasetResource)it.next().o;
				if(dr.getResourceName()!=null){
					//set dataset Source
					d.getResourceSet().add(dr);
				}
			}
		}
		
		return d;
	}

	private int getEndingDigit(int prefixLength, String complete) throws InventoryException{
		try{
		return Integer.valueOf(complete.substring(prefixLength));
		}catch(Exception e){
			throw new InventoryException("Error Parsing ID from fieldName.");
			}
	}
	
	private DatasetCitation getCitation(Dataset d){
		if(d == null){
			return new DatasetCitation();
		}
		Set<DatasetCitation> cites = d.getCitationSet(); //we only have one per dataset, set not really needed...
		DatasetCitation citation;
		if(cites.isEmpty())
			citation = new DatasetCitation();
		else
			citation = cites.iterator().next();
		return citation;
	}

	private void addCitation(Dataset d, DatasetCitation dc){
		if(d == null){
			return;
		}
		Set<DatasetCitation> cites = d.getCitationSet(); //we only have one per dataset, set not really needed...
		if(cites.isEmpty())
			d.getCitationSet().add(dc);
		else
			d.getCitationSet().clear();
		d.getCitationSet().add(dc);
	}
	
	private DatasetMetaHistory getMetaHistory(Dataset d){
		if(d == null){
			return new DatasetMetaHistory();
		}
		Set<DatasetMetaHistory> hists = d.getMetaHistorySet(); //we only have one per dataset, set not really needed...
		DatasetMetaHistory hist;
		if(hists.isEmpty())
			hist = new DatasetMetaHistory();
		else
			hist = hists.iterator().next();
		return hist;
	}

	private void addMetaHistory(Dataset d, DatasetMetaHistory dmh){
		if(d == null){
			return;
		}
		Set<DatasetMetaHistory> hists = d.getMetaHistorySet(); //we only have one per dataset, set not really needed...
		if(hists.isEmpty())
			d.getMetaHistorySet().add(dmh);
		else
		{	d.getMetaHistorySet().clear();
			d.getMetaHistorySet().add(dmh);
		}
		dmh.getMetaHistoryPK().setDataset(d);
	}
	
	private DatasetSoftware getSoftware(Dataset d){
		if(d == null){
			return new DatasetSoftware();
		}
		Set<DatasetSoftware> softwares = d.getSoftwareSet(); //we only have one per dataset, set not really needed...
		DatasetSoftware soft;
		if(softwares.isEmpty())
			soft = new DatasetSoftware();
		else
			soft = softwares.iterator().next();
		return soft;
	}

	private void addSoftware(Dataset d, DatasetSoftware ds){
		if(d == null){
			return;
		}
		Set<DatasetSoftware> softwares = d.getSoftwareSet(); //we only have one per dataset, set not really needed...
		if(softwares.isEmpty())
			d.getSoftwareSet().add(ds);
		else
			d.getSoftwareSet().clear();
		d.getSoftwareSet().add(ds);
	}
	
	public Object findElement(List<indexObject> list, int searchIdx){

		for(indexObject io : list){
			if(io.index == searchIdx)
				return io.o;
		}
		return null;
		
	}
	
	public class indexObject{
		
		public int index;
		public Object o;

		public indexObject(){
			
		}
		
		public indexObject(int i, Object o){
			this.index = i;
			this.o = o;
		}
	
		
		
	}
	
}

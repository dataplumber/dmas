package gov.nasa.podaac.inventory.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

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
import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.model.CollectionContact;
import gov.nasa.podaac.inventory.model.CollectionDataset;
import gov.nasa.podaac.inventory.model.CollectionLegacyProduct;
import gov.nasa.podaac.inventory.model.CollectionProduct;


public class CollectionMetadataImpl implements MetadataObject {
	Query q = null;
	DataManager manager;
	CollectionProduct cp = new CollectionProduct();
	List<CollectionDataset> cds = new ArrayList<CollectionDataset>(50);
	List<Integer> contacts = new ArrayList<Integer>(50);
	public CollectionMetadataImpl(){
		q = QueryFactory.getInstance().createQuery();
		 manager = DataManagerFactory.getInstance()
		.createDataManager();
	}
	
	public MetadataManifest addNew(MetadataManifest mf)
			throws InventoryException {
		
		for(int i = 0; i<50; i++){
			cds.add(i, new CollectionDataset());
		}
		
		Collection s = new Collection();
		s = mapXmlToCollection(mf, null);
		manager.addCollection(s);
		s.setCollectionProduct(cp);
		cp.setCollection(s);
		
		s.getCollectionDatasetSet().clear();
		Iterator<CollectionDataset> it = cds.iterator();
		while(it.hasNext()){
			CollectionDataset cd = it.next();
			if(cd.getCollectionDatasetPK().getDataset().getDatasetId() != null){
				cd.getCollectionDatasetPK().setCollection(s);
				manager.addCollectionDataset(cd);
			}
		}
		
		Iterator<Integer> it2 = contacts.iterator();
		while(it2.hasNext()){
			Integer cd = it2.next();
			if(cd != null){
				CollectionContact cc = new CollectionContact();
				cc.getCollectionContactPK().setCollection(s);
				cc.getCollectionContactPK().getContact().setContactId(cd);
				manager.addCollectionContact(cc);
			}
		}
		//cc.getCollectionContactPK().getContact().setContactId(Integer.valueOf(fval));
		
		manager.addCollection(s);
		//manager.add
	
		MetadataManifest mani = new MetadataManifest();
		mani.setObject(Constant.ObjectType.COLLECTION);
		mani.setType(Constant.ActionType.LIST);
		MetadataField field = new MetadataField();
		field.setName("report");
		field.setValue("Collection: " + s.getShortName() + "was successfully added");
		field.setType("char");
		mani.getFields().add(field);
		field = new MetadataField();
		field.setName("object_id");
		field.setValue(s.getCollectionId());
		field.setType("int");
		mani.getFields().add(field);
		return mani;
	}

	private MetadataManifest mapCollectionToFields(Collection s){
		
		MetadataManifest mm = new MetadataManifest();
		
		mm.setObject(Constant.ObjectType.COLLECTION);
		mm.setType(Constant.ActionType.TEMPLATE);
		
		//go through the dataset and create fields for each of the items.
		MetadataField mf = new MetadataField(); 
		
		//COLLECTION
		
		mf.setName("collection_collectionId");
		try{
		mf.setValue(s.getCollectionId());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(true);
		mf.setType("int");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("collection_shortName");
		mf.setValue(s.getShortName());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("collection_longName");
		mf.setValue(s.getLongName());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("collection_type");
		mf.setValue(s.getType());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("collection_description");
		mf.setValue(s.getDescription());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("collection_fullDescription");
		mf.setValue(s.getFullDescription());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("collection_aggregate");
		mf.setValue(s.getAggregate());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		//COLLECTION CONTACT
		int i=1;
		for(CollectionContact c : s.getCollectionContactSet()){
			
			mf = new MetadataField();
			mf.setName("collectionContact_contactId" + i);
			mf.setValue(c.getCollectionContactPK().getContact().getContactId());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf); 
			i++;
			
		}
		
		//COLLECTION DATASET
		i=1;
		for(CollectionDataset cd : s.getCollectionDatasetSet()){
			
			mf = new MetadataField();
			mf.setName("collectionDataset_datasetId" + i);
			mf.setValue(cd.getCollectionDatasetPK().getDataset().getDatasetId());
			mf.setRequired(true);
			mf.setType("int");
			mm.getFields().add(mf); 
			
			mf = new MetadataField();
			mf.setName("collectionDataset_granuleFlag" + i);
			mf.setValue(cd.getGranuleFlag());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf);
			
			mf = new MetadataField();
			mf.setName("collectionDataset_granuleRange360" + i);
			mf.setValue(cd.getGranuleRange360());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf);
			mf = new MetadataField();
			mf.setName("collectionDataset_startGranuleId" + i);
			mf.setValue(cd.getStartGranuleId());
			mf.setRequired(false);
			mf.setType("int");
			mm.getFields().add(mf);
			mf = new MetadataField();
			mf.setName("collectionDataset_stopGranuleId" + i);
			mf.setValue(cd.getStopGranuleId());
			mf.setRequired(false);
			mf.setType("int");
			mm.getFields().add(mf);
			
			i++;
		}
		//COLLECTION LEGACY PRODUCT
		i=1;
		for(CollectionLegacyProduct clp : s.getCollectionLegacyProductSet()){
			mf = new MetadataField();
			mf.setName("collectionLegacyProduct_legacyProductId");
			mf.setValue(clp.getLegacyProductId());
			mf.setRequired(false);
			mf.setType("int");
			mm.getFields().add(mf); mf = new MetadataField();
			i++;
		}
		//COLLECTION PRDUCT
		CollectionProduct cp = s.getCollectionProduct();
		
		mf = new MetadataField();
		mf.setName("collectionProduct_visibleFlag");
		mf.setValue(cp.getVisibleFlag());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("collectionProduct_productId");
		mf.setValue(cp.getProductId());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf);
		
		return mm;
	}
//	
	private Collection mapXmlToCollection(MetadataManifest mf, Collection s) throws InventoryException {
		
		if(s == null)
			s = new Collection();
		for(MetadataField f :mf.getFields())
		{
			String fname = f.getName();
			String fval = f.getValue(); 
			try{
				if(f.getName().equals("collection_collectionId")){
					s.setCollectionId(Integer.valueOf(fval));	
				}
				else if(f.getName().equals("collection_shortName")){
					s.setShortName(fval);	
				}
				else if(f.getName().equals("collection_longName")){
					s.setLongName(fval);	
				}
				else if(f.getName().equals("collection_type")){
					s.setType(fval);	
				}
				else if(f.getName().equals("collection_description")){
					s.setDescription(fval);	
				}
				else if(f.getName().equals("collection_fullDescription")){
					s.setFullDescription(fval);	
				}
				else if(f.getName().equals("collection_aggregate")){
					s.setAggregate(fval);	
				}
				//CONTACT
				else if(f.getName().startsWith("collectionContact_contactId")){
					
					contacts.add(Integer.valueOf(fval));
					//cc.getCollectionContactPK().getContact().setContactId(Integer.valueOf(fval));
					
				}
				
				else if(f.getName().equals("collectionLegacyProduct_legacyProductId")){
					CollectionLegacyProduct clp = getCollectionLegacyProduct(s);
					try{
						clp.setLegacyProductId(Integer.valueOf(fval));
						addCollectionLegacyProduct(s, clp);
					}catch(Exception e){throw new InventoryException("Attempted to set legacy product id as non-numeric value: "+ fval);}
					
				}

				//DATASET STUFF
				else if(f.getName().startsWith("collectionDataset_datasetId")){
					int idx = getEndingDigit("collectionDataset_datasetId".length(), fname);
					CollectionDataset cd = null;
					try{
					cd = cds.get(idx);
					}catch(IndexOutOfBoundsException e){
						cd = new CollectionDataset();
					}
					cd.getCollectionDatasetPK().getDataset().setDatasetId(Integer.valueOf(fval));
					cds.set(idx, cd);
				}
				
				else if(f.getName().startsWith("collectionDataset_granuleFlag")){
					int idx = getEndingDigit("collectionDataset_granuleFlag".length(), fname);
					CollectionDataset cd = null;
					try{
					cd = cds.get(idx);
					}catch(ArrayIndexOutOfBoundsException e){
						cd = new CollectionDataset();
					}
					cd.setGranuleFlag((fval.charAt(0)));
					cds.set(idx, cd);
				}
				
				else if(f.getName().startsWith("collectionDataset_granuleRange360")){
					int idx = getEndingDigit("collectionDataset_granuleRange360".length(), fname);
					CollectionDataset cd = null;
					try{
					cd = cds.get(idx);
					}catch(ArrayIndexOutOfBoundsException e){
						cd = new CollectionDataset();
					}
					cd.setGranuleRange360((fval.charAt(0)));
					cds.set(idx, cd);
				}
				else if(f.getName().startsWith("collectionDataset_startGranuleId")){
					int idx = getEndingDigit("collectionDataset_startGranuleId".length(), fname);
					CollectionDataset cd = null;
					try{
					cd = cds.get(idx);
					}catch(ArrayIndexOutOfBoundsException e){
						cd = new CollectionDataset();
					}
					try{cd.setStartGranuleId(Integer.valueOf(fval));
					}catch(Exception e){}
					cds.set(idx, cd);
				}
				else if(f.getName().startsWith("collectionDataset_stopGranuleId")){
					int idx = getEndingDigit("collectionDataset_stopGranuleId".length(), fname);
					CollectionDataset cd = null;
					try{
					cd = cds.get(idx);
					}catch(ArrayIndexOutOfBoundsException e){
						cd = new CollectionDataset();
					}
					try{cd.setStopGranuleId(Integer.valueOf(fval));
					}catch(Exception e){}
					cds.set(idx, cd);
				}
				
				else if(f.getName().equals("collectionProduct_visibleFlag")){
					cp.setVisibleFlag(fval.charAt(0));
					cp.setCollection(s);
				}
				else if(f.getName().equals("collectionProduct_productId")){
					if(fval == null || fval.equals(""))
						cp.setProductId(null);
					else
						cp.setProductId(fval);
					cp.setCollection(s);
				}
			}catch(Exception e){
				e.printStackTrace();
				throw new InventoryException("Field/Value pair in Manifest caused exception: " + fname + ":" + fval);
			}
			//add collectionDatasets
		}	
		return s;
	}

	public MetadataManifest delete(MetadataManifest mf)
			throws InventoryException {
		// TODO Auto-generated method stub
		return null;
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
			throw new InventoryException("Collection id not found or improper format.");
		}
		
		Collection s = q.fetchCollectionById(id, "collectionProduct","collectionContactSet", "collectionDatasetSet","collectionLegacyProductSet", "CollectionProduct");
		MetadataManifest mm = mapCollectionToFields(s);
		return mm;
	}

	public MetadataManifest getTemplate() throws InventoryException {
		InputStream is = DatasetMetadataImpl.class.getResourceAsStream("/gov/nasa/podaac/inventory/xml/collectionTemplate.xml");
		if(is == null){
			throw new InventoryException("Resource collectionTemplate not found");
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

	private CollectionLegacyProduct getCollectionLegacyProduct(Collection c){
		if(c == null){
			return new CollectionLegacyProduct();
		}
		Set<CollectionLegacyProduct> clps = c.getCollectionLegacyProductSet(); //we only have one per dataset, set not really needed...
		CollectionLegacyProduct soft;
		if(clps.isEmpty())
			soft = new CollectionLegacyProduct();
		else
			soft = clps.iterator().next();
		return soft;
	}

	private void addCollectionLegacyProduct(Collection c, CollectionLegacyProduct clp){
		if(c == null){
			return;
		}
		Set<CollectionLegacyProduct> clps = c.getCollectionLegacyProductSet(); //we only have one per dataset, set not really needed...
		if(clps.isEmpty())
			c.getCollectionLegacyProductSet().add(clp);
		else
			c.getCollectionLegacyProductSet().clear();
		c.getCollectionLegacyProductSet().add(clp);
	}
	
	private int getEndingDigit(int prefixLength, String complete) throws InventoryException{
		try{
		return Integer.valueOf(complete.substring(prefixLength));
		}catch(Exception e){
			throw new InventoryException("Error Parsing ID from fieldName.");
			}
	}
	public MetadataManifest update(MetadataManifest mf)
			throws InventoryException {
		
		for(int i = 0; i<50; i++){
			cds.add(i, new CollectionDataset());
		}
		
//		int id = 0;
//		try{
//			for(MetadataField mef : mf.getFields()){
//				if(mef.getName().equals("object_id") || mef.getName().equals("dataset_datasetId"))
//					id = Integer.valueOf(mef.getValue());
//			}
//			}catch(Exception e){
//				throw new InventoryException("Dataset id not found or improper format.");
//			}
//		Collection s = q.fetchCollectionById(id);
		Collection s = new Collection();
		s = mapXmlToCollection(mf, null);
		
		manager.addCollection(s);
		
		//System.out.println("Adding cds, contacnts, and product...");
		
		
		s.setCollectionProduct(cp);
		cp.setCollection(s);
		
		q.runQuery("DELETE FROM collection_product where collection_id="+s.getCollectionId() +"");
		q.runQuery("DELETE FROM collection_contact where collection_id="+s.getCollectionId() +"");
		//q.runQuery("DELETE FROM collection_dataset where collection_id="+s.getCollectionId() +"");
		
		s.getCollectionDatasetSet().clear();
		Iterator<CollectionDataset> it = cds.iterator();
		while(it.hasNext()){
			CollectionDataset cd = it.next();
			if(cd.getCollectionDatasetPK().getDataset().getDatasetId() != null){
				cd.getCollectionDatasetPK().setCollection(s);
				manager.addCollectionDataset(cd);
			}
		}
		
		Iterator<Integer> it2 = contacts.iterator();
		while(it2.hasNext()){
			Integer cd = it2.next();
			if(cd != null){
				CollectionContact cc = new CollectionContact();
				cc.getCollectionContactPK().setCollection(s);
				cc.getCollectionContactPK().getContact().setContactId(cd);
				manager.addCollectionContact(cc);
			}
		}
		//cc.getCollectionContactPK().getContact().setContactId(Integer.valueOf(fval));
		
		manager.addCollection(s);
		//manager.add
	
		MetadataManifest mani = new MetadataManifest();
		mani.setObject(Constant.ObjectType.COLLECTION);
		mani.setType(Constant.ActionType.LIST);
		MetadataField field = new MetadataField();
		field.setName("report");
		field.setValue("Collection: " + s.getShortName() + "was successfully update");
		field.setType("char");
		mani.getFields().add(field);
		
		field = new MetadataField();
		field.setName("object_id");
		field.setValue(s.getCollectionId());
		field.setType("int");
		mani.getFields().add(field);
		return mani;
	}

}

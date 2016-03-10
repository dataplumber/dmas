package gov.nasa.podaac.inventory.core;

import gov.nasa.podaac.common.api.metadatamanifest.Constant;
import gov.nasa.podaac.common.api.metadatamanifest.MetadataField;
import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifest;
import gov.nasa.podaac.common.api.metadatamanifest.Constant.ObjectType;
import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.model.Contact;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.Project;
import gov.nasa.podaac.inventory.model.Provider;
import gov.nasa.podaac.inventory.model.Sensor;
import gov.nasa.podaac.inventory.model.Source;

import java.util.List;
import java.util.Set;

public class MetadataManager {
	

	public static MetadataManifest getListing(ObjectType oType) throws InventoryException{
		Query q = QueryFactory.getInstance().createQuery();
		
		MetadataManifest mf =  new MetadataManifest();
		mf.setObject(oType);
		mf.setType(Constant.ActionType.LIST);
		
		if(oType.equals(Constant.ObjectType.DATASET))
		{	
			List<Dataset> dList = q.listDataset();
			
			if(!dList.isEmpty()){
				int i = 1;
				for(Dataset d : dList){
					MetadataField fn = new MetadataField();
					MetadataField fid = new MetadataField();
					MetadataField fType = new MetadataField();
					fn.setName("dataset_shortName" + i);
					fn.setType("char");
					fn.setValue(d.getShortName());
					
					fid.setName("dataset_datasetId" + i);
					fid.setType("int");
					fid.setValue(d.getDatasetId().toString());
					
					fType.setName("datasetPolicy_accessType"+ i);
					fType.setType("char");
					fType.setValue(d.getDatasetPolicy().getAccessType());
					
					
					mf.getFields().add(fn);
					mf.getFields().add(fid);
					mf.getFields().add(fType);
					i++;
				}
				
				return mf; 
			}
			else{
				throw new InventoryException("No Datasets can be Found");
			}
			
		}
		if(oType.equals(Constant.ObjectType.SOURCE))
		{	
			List<Source> sList = q.listSource();
			if(!sList.isEmpty()){
				int i = 1;
				for(Source s : sList){
					MetadataField fn = new MetadataField();
					MetadataField fid = new MetadataField();
					fn.setName("source_shortName" + i);
					fn.setType("char");
					fn.setValue(s.getSourceShortName());
					fid.setName("source_sourceId" + i);
					fid.setType("int");
					fid.setValue(s.getSourceId().toString());
					mf.getFields().add(fn);
					mf.getFields().add(fid);
					i++;
				}
				
				return mf; 
			}
			else{
				throw new InventoryException("No Sources can be Found");
			}
			
		}
		
		if(oType.equals(Constant.ObjectType.COLLECTION))
		{	
			List<Collection> sList = q.listCollection();
			if(!sList.isEmpty()){
				int i = 1;
				for(Collection s : sList){
					MetadataField fn = new MetadataField();
					MetadataField fid = new MetadataField();
					fn.setName("collection_shortName" + i);
					fn.setType("char");
					fn.setValue(s.getShortName());
					fid.setName("collection_collectionId" + i);
					fid.setType("int");
					fid.setValue(s.getCollectionId());
					mf.getFields().add(fn);
					mf.getFields().add(fid);
					i++;
				}
				
				return mf; 
			}
			else{
				throw new InventoryException("No Collection can be Found");
			}
			
		}
		
		if(oType.equals(Constant.ObjectType.SENSOR))
		{	
			List<Sensor> sList = q.listSensor();
			if(!sList.isEmpty()){
				int i = 1;
				for(Sensor s : sList){
					MetadataField fn = new MetadataField();
					MetadataField fid = new MetadataField();
					fn.setName("sensor_shortName" + i);
					fn.setType("char");
					fn.setValue(s.getSensorShortName());
					fid.setName("sensor_sensorId" + i);
					fid.setType("int");
					fid.setValue(s.getSensorId().toString());
					mf.getFields().add(fn);
					mf.getFields().add(fid);
					i++;
				}
				
				return mf; 
			}
			else{
				throw new InventoryException("No Sensors can be Found");
			}
			
		}
		if(oType.equals(Constant.ObjectType.PROVIDER))
		{	
			List<Provider> pList = q.listProvider();
			if(!pList.isEmpty()){
				int i = 1;
				for(Provider p : pList){
					MetadataField fn = new MetadataField();
					MetadataField fid = new MetadataField();
					fn.setName("provider_shortName" + i);
					fn.setType("char");
					fn.setValue(p.getShortName());
					fid.setName("provider_providerId" + i);
					fid.setType("int");
					fid.setValue(p.getProviderId().toString());
					mf.getFields().add(fn);
					mf.getFields().add(fid);
					i++;
				}
				
				return mf; 
			}
			else{
				throw new InventoryException("No Providers can be Found");
			}
			
		}
		if(oType.equals(Constant.ObjectType.PROJECT))
		{	
			List<Project> pList = q.listProject();
			if(!pList.isEmpty()){
				int i = 1;
				for(Project p : pList){
					MetadataField fn = new MetadataField();
					MetadataField fid = new MetadataField();
					fn.setName("project_shortName" + i);
					fn.setType("char");
					fn.setValue(p.getProjectShortName());
					fid.setName("project_projectId" + i);
					fid.setType("int");
					fid.setValue(p.getProjectId().toString());
					mf.getFields().add(fn);
					mf.getFields().add(fid);
					i++;
				}
				
				return mf; 
			}
			else{
				throw new InventoryException("No Projects can be Found");
			}
			
		}
		if(oType.equals(Constant.ObjectType.CONTACT))
		{	
			List<Contact> cList = q.listContact();
			if(!cList.isEmpty()){
				int i = 1;
				for(Contact c : cList){
					MetadataField fn = new MetadataField();
					MetadataField fid = new MetadataField();
					fn.setName("contact_name" + i);
					fn.setType("char");
					if(c.getProvider() != null)
						fn.setValue(c.getLastName()+", "+c.getFirstName() +" ("+c.getRole()+", "+c.getProvider().getShortName()+")");
					else
						fn.setValue(c.getLastName()+", "+c.getFirstName() +" ("+c.getRole()+", No Provider Listed)");
					fid.setName("contact_contactId" + i);
					fid.setType("int");
					fid.setValue(c.getContactId().toString());
					mf.getFields().add(fn);
					mf.getFields().add(fid);
					i++;
				}
				
				return mf; 
			}
			else{
				throw new InventoryException("No Contacts can be Found");
			}
			
		}
		if(oType.equals(Constant.ObjectType.ELEMENT))
		{	
			List<ElementDD> eList = q.listElementDD();
			if(!eList.isEmpty()){
				int i = 1;
				for(ElementDD e : eList){
					MetadataField fn = new MetadataField();
					MetadataField fid = new MetadataField();
					MetadataField fdesc = new MetadataField();
					fn.setName("elementDD_shortName" + i);
					fn.setType("char");
					fn.setValue(e.getShortName());
					
					fid.setName("elementDD_elementId" + i);
					fid.setType("int");
					fid.setValue(e.getElementId().toString());
					
					fdesc.setName("elementDD_elementDesc" + i);
					fdesc.setType("char");
					fdesc.setValue(e.getDescription());
					
					mf.getFields().add(fn);
					mf.getFields().add(fid);
					mf.getFields().add(fdesc);
					i++;
				}
				
				return mf; 
			}
			else{
				throw new InventoryException("No Elements can be Found");
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
}

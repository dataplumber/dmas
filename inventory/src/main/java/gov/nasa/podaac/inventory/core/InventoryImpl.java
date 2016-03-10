// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
 
package gov.nasa.podaac.inventory.core;

import gov.nasa.podaac.common.api.metadatamanifest.Constant;
import gov.nasa.podaac.common.api.metadatamanifest.MetadataField;
import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifest;
import gov.nasa.podaac.common.api.metadatamanifest.Constant.ObjectType;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveFileInfo;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveGranule;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveProfile;
import gov.nasa.podaac.common.api.serviceprofile.GranuleFile;
import gov.nasa.podaac.common.api.serviceprofile.IngestDetails;
import gov.nasa.podaac.common.api.serviceprofile.IngestProfile;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile;
import gov.nasa.podaac.common.api.util.StringUtility;
import gov.nasa.podaac.inventory.api.GranuleMetadata;
import gov.nasa.podaac.inventory.api.GranuleMetadataFactory;
import gov.nasa.podaac.inventory.api.Inventory;
import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.api.Constant.AppendBasePathType;
import gov.nasa.podaac.inventory.api.Constant.GranuleArchiveType;
import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;
import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.hibernate.HibernateSessionFactory;
import gov.nasa.podaac.inventory.model.Contact;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleSIP;
import gov.nasa.podaac.inventory.model.Project;
import gov.nasa.podaac.inventory.model.Provider;
import gov.nasa.podaac.inventory.model.Sensor;
import gov.nasa.podaac.inventory.model.Source;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains the implementation for Inventory Interfaces.
 *
 * @author clwong
 * @version $Id: InventoryImpl.java 9634 2012-02-28 15:14:02Z gangl $
 */
public class InventoryImpl implements Inventory {
		
	private static Log log = LogFactory.getLog(InventoryImpl.class);
	private ServiceProfile sp;
	private Date reqTime;
	private Date acqTime;
	
	public synchronized void storeServiceProfile(ServiceProfile serviceProfile)
								throws InventoryException {
		log.debug("storeServiceProfile: ");
		this.sp = serviceProfile;	
		//log.info(sp.toString());
		sp.getProductProfile().setArchiveProfile(sp.getProductProfile().createArchiveProfile());		
		storeIngestProfile(sp.getProductProfile().getIngestProfile());		
	}
	
	public synchronized void closeSession() {
		log.debug("closeSession");
		HibernateSessionFactory.close();
	}
	
	public synchronized void storeIngestProfile(IngestProfile ingestProfile) 
								throws InventoryException {
		log.debug("storeIngestProfile: ");
		Set<gov.nasa.podaac.common.api.serviceprofile.Granule> 
			sipGranuleSet = ingestProfile.getCompleteContent().getGranules();
		
		acqTime = ingestProfile.getHeader().getAcquired();
		reqTime = ingestProfile.getHeader().getRequested();
		
		long time=System.currentTimeMillis();
		for (gov.nasa.podaac.common.api.serviceprofile.Granule sipGranule : sipGranuleSet) {
			//System.out.println("SIP Granule String: " + sipGranule);
		
			storeGranuleMetadata(sipGranule);
		}
		log.info("Inventory "+sipGranuleSet.size()+" granules took "+(System.currentTimeMillis()-time)/1000.+" seconds.");
		log.info("Average per granule = "+((System.currentTimeMillis()-time)/1000./sipGranuleSet.size()));
	}

	private void storeGranuleMetadata (
			gov.nasa.podaac.common.api.serviceprofile.Granule sipGranule) 
			throws InventoryException {
		log.debug("storeGranuleMetadata: ");
		GranuleMetadata gm = GranuleMetadataFactory.getInstance().createGranuleMetadata();
		gm.setMetadata(sipGranule);		
		gm.mapToGranule();
		//version stuff here?
		sipGranule.getVersion();
		
		
		Set<GranuleFile> granuleFileSet = sipGranule.getFiles();
		
		// set ingestTime of granule metadata using the first granule file ingest time
		gm.getGranule().setIngestTime(granuleFileSet.iterator().next().getIngestDetails().getIngestStartTime());
		
		// Append subdirectory to basePath here with BATCH mode
		if (gm.getGranule().getDataset().getDatasetPolicy().getBasePathAppendType().equals(
				AppendBasePathType.BATCH.toString())) {
			gm.setArchiveSubDir(sp.getProductProfile().getIngestProfile().getHeader().getBatch());
		}
		ArchiveProfile archiveProfile = sp.getProductProfile().getArchiveProfile();
		ArchiveGranule archiveGranule = archiveProfile.createGranule();
		for (GranuleFile granuleFile : granuleFileSet) {			
			IngestDetails ingestInfo = granuleFile.getIngestDetails();
			
			// attach GranuleArchive
			Character compressFlag = 'N';
			//System.out.println("compression["+granuleFile.getType().toString()+"]: " + ingestInfo.getSource().getCompressionAlgorithm());
			if (ingestInfo.getSource().getCompressionAlgorithm() != null) compressFlag ='Y';
			
			String filename = null;
			if (ingestInfo.getLocalStaging() != null) {
				filename =  new File(ingestInfo.getLocalStaging()).getName();
			} else {
				filename = new File(ingestInfo.getRemoteStaging()).getName();
			}
			// type needed for setting GranuleReference
			String type = granuleFile.getType().toString();
			// destination needed for GranuleArchive and AIP
			String destination = StringUtility.cleanPaths(gm.getArchiveBasePath(),gm.getArchiveSubDir(),filename);
			String rootPath = gm.getArchiveBasePath();
			String relPath = gm.getArchiveSubDir();
			gm.setGranuleRootPath(rootPath);
			gm.setGranuleRelPath(relPath);
			// attach GranuleArchive
			
			if(!gm.getGranule().getDataset().isRemote()){
				log.debug("Adding granuleArchive: " + filename);
				gm.addGranuleArchive(
						ingestInfo.getChecksum(), compressFlag, ingestInfo.getSize(), filename, type);
			}
			
			// attach GranuleReferences
			if (type.trim().equalsIgnoreCase(GranuleArchiveType.DATA.toString())) 
			{
				//System.out.println("DATA FILE FOUND, creating reference.");
				//System.out.println("File ref: "+filename);
				//System.out.println("File dest: "+destination);
				gm.addGranuleReference(filename);
			}
			

			// compose AIP List to return to Ingest
			ArchiveFileInfo archiveFileInfo = archiveGranule.createFile();
			try {
				archiveFileInfo.setDestination(new URI(destination));
			} catch (URISyntaxException e) {
				throw new InventoryException(e.getMessage());
			}			
			// set staging information
			archiveFileInfo.setIngestDetails(ingestInfo);
			archiveFileInfo.setType(granuleFile.getType());
			archiveGranule.addFile(archiveFileInfo);			
		}
		
		// replacement
		String replaceName = sipGranule.getReplace();
		if (replaceName != null) {
		   if (!replaceName.trim().equals("")) {
			   archiveGranule.setDeleteName(replaceName);
			   Query q = QueryFactory.getInstance().createQuery();
			   Granule g = q.findGranule(replaceName, gm.getGranule().getDataset());
			   g = q.fetchGranuleById(g.getGranuleId(), "granuleArchiveSet");
			   Set<GranuleArchive> granuleArchiveSet = g.getGranuleArchiveSet();
			   for (GranuleArchive archive : granuleArchiveSet) {
				  log.debug("Remove GranuleArchive: " + archive.getName());
			      archiveGranule.addDeleteUri(StringUtility.cleanPaths(g.getRootPath(), g.getRelPath(), archive.getName()));
			   }
		   }
		}
		
		//add set Archive Status, acquired and request time
		try{
			gm.getGranule().setAcquiredTime(acqTime);
		}catch(NullPointerException npe){}
		try{
			gm.getGranule().setRequestedTime(reqTime);
		}catch(NullPointerException npe){}
		
		
		
		gm.getGranule().setStatus(GranuleStatus.OFFLINE);
		//gm.getGranule().setArchiveTime(new Date());
		
		
		
		//set the granuleSip value to the current sip
		GranuleSIP gsip = new GranuleSIP();
		gsip.setGranule(gm.getGranule());
		gsip.setSip(this.sp.toString());
		gm.getGranule().add(gsip);
		
		// save granule and its associates into database
		gm.addGranule();
		
		gm.addGranuleHistory();
		
		
		
		
		// update granule id
		sipGranule.setId(gm.getGranule().getGranuleId().longValue());
		archiveGranule.setGranuleId(gm.getGranule().getGranuleId());
		archiveGranule.setName(sipGranule.getName());
		archiveGranule.setDatasetName(sipGranule.getDatasetName());
		archiveGranule.setProductType(sipGranule.getProductType());
		archiveGranule.setArchiveSuccess(false);
		archiveProfile.addGranule(archiveGranule);      
	}
	public MetadataManifest processManifest(MetadataManifest metadataManifest) throws InventoryException
	{
		return processManifest(metadataManifest, null);
	}
	
	public MetadataManifest processManifest(MetadataManifest metadataManifest, String user)	throws InventoryException
	{
			MetadataManifest m2 = null;
			String userId = user;
			
			try{
			m2 = processManifestImpl(metadataManifest,user);
			//save the manifest
			
			
			if(metadataManifest.getActionType().equals(Constant.ActionType.CREATE.toString()) || metadataManifest.getActionType().equals(Constant.ActionType.UPDATE.toString())){
			
				if(metadataManifest.getObjectType().equals(Constant.ObjectType.DATASET.toString()) || metadataManifest.getObjectType().equals(Constant.ObjectType.COLLECTION.toString()))
				{
					Query q = QueryFactory.getInstance().createQuery();
				
					gov.nasa.podaac.inventory.model.MetadataManifest mf = new gov.nasa.podaac.inventory.model.MetadataManifest(); 	
					mf.setManifest(metadataManifest.generateXml());
					mf.setSubmissionDate(new Date().getTime());
					mf.setType(metadataManifest.getObjectType());
					Integer objectId = null;
					//get user
					for(MetadataField mef : m2.getFields()){
						if(mef.getName().equals("object_id"))
							objectId = Integer.valueOf(mef.getValue());
					}
					
					for(MetadataField mef : metadataManifest.getFields()){
						if(mef.getName().equals("user"))
							userId = mef.getValue();
					}
					
					mf.setUser(userId);
					//get objectId
					if(userId == null)
						mf.setUser("User");
					
					mf.setItemId(objectId);
					//Set the diff to what the note is from the update, if it exists.
					mf.setDiff(m2.getNote());
					q.saveMetadataManifest(mf);
				}
			}
			}
			catch(InventoryException e){
				log.error(e.getMessage());
				throw e;
			}
			
			return m2;
			
	}
	
	public MetadataManifest processManifestImpl(MetadataManifest metadataManifest, String user)
	throws InventoryException {
		//MetadataManager mm = new MetadataManager
		
		//dataset, ///granule, collection////, source, sensor, provider, project, contact, element
		if(metadataManifest.getObjectType().equals(Constant.ObjectType.DATASET.toString())){
			if(metadataManifest.getActionType().equals(Constant.ActionType.CREATE.toString()))
				return new DatasetMetadataImpl(user).addNew(metadataManifest);
			else if(metadataManifest.getActionType().equals(Constant.ActionType.UPDATE.toString()))
				return new DatasetMetadataImpl(user).update(metadataManifest);
			else if(metadataManifest.getActionType().equals(Constant.ActionType.DELETE.toString()))
				{}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.LIST.toString()))
				{ 
					if(metadataManifest.hasField("object_id")){
						return new DatasetMetadataImpl().getInstance(metadataManifest);
					}
					else{
						
						return MetadataManager.getListing(Constant.ObjectType.DATASET);
					}
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.TEMPLATE.toString()))
				{ return new DatasetMetadataImpl().getTemplate();}
			else
				throw new InventoryException("Unsupported action-type: "+metadataManifest.getActionType().toString());
		}
		else if(metadataManifest.getObjectType().equals(Constant.ObjectType.GRANULE.toString())){
			if(metadataManifest.getActionType().equals(Constant.ActionType.CREATE.toString()))
				{
				return new CollectionMetadataImpl().addNew(metadataManifest);
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.UPDATE.toString()))
				{
				return new CollectionMetadataImpl().update(metadataManifest);
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.DELETE.toString()))
				{}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.LIST.toString()))
				{
					if(metadataManifest.hasField("object_id")){
						return new CollectionMetadataImpl().getInstance(metadataManifest);
					}
					else{
						
						return MetadataManager.getListing(Constant.ObjectType.GRANULE);
					}
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.TEMPLATE.toString()))
				{ 
					return new CollectionMetadataImpl().getTemplate();}
			else
				throw new InventoryException("Unsupported action-type: "+metadataManifest.getActionType().toString());
		}
		else if(metadataManifest.getObjectType().equals(Constant.ObjectType.COLLECTION.toString())){
			if(metadataManifest.getActionType().equals(Constant.ActionType.CREATE.toString()))
				{
				return new CollectionMetadataImpl().addNew(metadataManifest);
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.UPDATE.toString()))
				{
				return new CollectionMetadataImpl().update(metadataManifest);
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.DELETE.toString()))
				{}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.LIST.toString()))
				{
					if(metadataManifest.hasField("object_id")){
						return new CollectionMetadataImpl().getInstance(metadataManifest);
					}
					else{
						
						return MetadataManager.getListing(Constant.ObjectType.COLLECTION);
					}
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.TEMPLATE.toString()))
				{ //return new CollectionMetadataImpl().getTemplate();
					return new CollectionMetadataImpl().getTemplate();
				}
			else
				throw new InventoryException("Unsupported action-type: "+metadataManifest.getActionType().toString());
		}
		else if(metadataManifest.getObjectType().equals(Constant.ObjectType.SOURCE.toString())){
			if(metadataManifest.getActionType().equals(Constant.ActionType.CREATE.toString()))
				{
					return new SourceMetadataImpl().addNew(metadataManifest);
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.UPDATE.toString()))
				{
				return new SourceMetadataImpl().update(metadataManifest);
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.DELETE.toString()))
				{
				
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.LIST.toString()))
				{
					if(metadataManifest.hasField("object_id")){
						return new SourceMetadataImpl().getInstance(metadataManifest);
					}
					else{
						return MetadataManager.getListing(Constant.ObjectType.SOURCE);
					}
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.TEMPLATE.toString()))
			{ 	return new SourceMetadataImpl().getTemplate();}
			else
				throw new InventoryException("Unsupported action-type: "+metadataManifest.getActionType().toString());
		}
		else if(metadataManifest.getObjectType().equals(Constant.ObjectType.SENSOR.toString())){
			if(metadataManifest.getActionType().equals(Constant.ActionType.CREATE.toString()))
				{
					return new SensorMetadataImpl().addNew(metadataManifest);
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.UPDATE.toString()))
				{
					return new SensorMetadataImpl().update(metadataManifest);
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.DELETE.toString()))
				{}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.LIST.toString()))
				{
					if(metadataManifest.hasField("object_id")){
						return new SensorMetadataImpl().getInstance(metadataManifest);
					}
					else{
						return MetadataManager.getListing(Constant.ObjectType.SENSOR);
					}
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.TEMPLATE.toString()))
			{ 	return new SensorMetadataImpl().getTemplate();}
			else
				throw new InventoryException("Unsupported action-type: "+metadataManifest.getActionType().toString());
		}
		else if(metadataManifest.getObjectType().equals(Constant.ObjectType.PROVIDER.toString())){
			if(metadataManifest.getActionType().equals(Constant.ActionType.CREATE.toString()))
				{
				return new ProviderMetadataImpl().addNew(metadataManifest);
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.UPDATE.toString()))
				{
				return new ProviderMetadataImpl().update(metadataManifest);
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.DELETE.toString()))
				{
				//return new ProviderMetadataImpl().addNew(metadataManifest);
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.LIST.toString()))
				{
					if(metadataManifest.hasField("object_id")){
						return new ProviderMetadataImpl().getInstance(metadataManifest);
					}
					else{
						return MetadataManager.getListing(Constant.ObjectType.PROVIDER);
					}
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.TEMPLATE.toString()))
				{ return new ProviderMetadataImpl().getTemplate();}
			else
				throw new InventoryException("Unsupported action-type: "+metadataManifest.getActionType().toString());
		}
		else if(metadataManifest.getObjectType().equals(Constant.ObjectType.PROJECT.toString())){
			if(metadataManifest.getActionType().equals(Constant.ActionType.CREATE.toString()))
			{
					return new ProjectMetadataImpl().addNew(metadataManifest);
			}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.UPDATE.toString()))
			{
				return new ProjectMetadataImpl().update(metadataManifest);
			}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.DELETE.toString()))
			{
				
			}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.LIST.toString()))
			{
				if(metadataManifest.hasField("object_id")){
					return new ProjectMetadataImpl().getInstance(metadataManifest);
				}
				else{
					return MetadataManager.getListing(Constant.ObjectType.PROJECT);
				}
			}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.TEMPLATE.toString()))
				{ 
					return new ProjectMetadataImpl().getTemplate();}
			else
				throw new InventoryException("Unsupported action-type: "+metadataManifest.getActionType().toString());
		}
		else if(metadataManifest.getObjectType().equals(Constant.ObjectType.CONTACT.toString())){
			if(metadataManifest.getActionType().equals(Constant.ActionType.CREATE.toString()))
				{
				return new ContactMetadataImpl().addNew(metadataManifest);
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.UPDATE.toString()))
				{
				return new ContactMetadataImpl().update(metadataManifest);
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.DELETE.toString()))
				{}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.LIST.toString()))
				{
					if(metadataManifest.hasField("object_id")){
						return new ContactMetadataImpl().getInstance(metadataManifest);
					}
					else{
						return MetadataManager.getListing(Constant.ObjectType.CONTACT);
					}
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.TEMPLATE.toString()))
			{ return new ContactMetadataImpl().getTemplate();}
			else
				throw new InventoryException("Unsupported action-type: "+metadataManifest.getActionType().toString());
		}
		else if(metadataManifest.getObjectType().equals(Constant.ObjectType.ELEMENT.toString())){
			if(metadataManifest.getActionType().equals(Constant.ActionType.CREATE.toString()))
				{
					return new ElementMetadataImpl().addNew(metadataManifest);
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.UPDATE.toString()))
				{
					return new ElementMetadataImpl().update(metadataManifest);
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.DELETE.toString()))
				{}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.LIST.toString()))
				{
					if(metadataManifest.hasField("object_id")){
						return new ElementMetadataImpl().getInstance(metadataManifest);
					}
					else{
						return MetadataManager.getListing(Constant.ObjectType.ELEMENT);
					}
				}
			else if(metadataManifest.getActionType().equals(Constant.ActionType.TEMPLATE.toString()))
			{ return new ElementMetadataImpl().getTemplate();}
			else
				throw new InventoryException("Unsupported action-type: "+metadataManifest.getActionType().toString());
		}
		else
		{
			throw new InventoryException("non-dataset manifests are not accepted at this time.");
		}
		
		return null;
	}
	
}

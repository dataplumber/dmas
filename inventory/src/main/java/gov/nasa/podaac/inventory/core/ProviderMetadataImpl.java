package gov.nasa.podaac.inventory.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.Provider;
import gov.nasa.podaac.inventory.model.ProviderResource;

public class ProviderMetadataImpl implements MetadataObject {
	Query q = null;
	DataManager manager;
	
	public ProviderMetadataImpl(){
		q = QueryFactory.getInstance().createQuery();
		 manager = DataManagerFactory.getInstance()
		.createDataManager();
	}
	
	public MetadataManifest addNew(MetadataManifest mf)
			throws InventoryException {
		// TODO Auto-generated method stub
		Provider s = new Provider();
		s = mapXmlToProvider(mf, null);
		manager.addProvider(s);
		
		MetadataManifest mani = new MetadataManifest();
		mani.setObject(Constant.ObjectType.PROVIDER);
		mani.setType(Constant.ActionType.LIST);
		MetadataField field = new MetadataField();
		field.setName("report");
		field.setValue("Provider: " + s.getShortName() + "was successfully added");
		field.setType("char");
		mani.getFields().add(field);
		return mani;
	}

	private MetadataManifest mapProviderToFields(Provider s){
		
		MetadataManifest mm = new MetadataManifest();
		
		mm.setObject(Constant.ObjectType.PROVIDER);
		mm.setType(Constant.ActionType.TEMPLATE);
		try{
		//go through the dataset and create fields for each of the items.
		MetadataField mf = new MetadataField(); 
		
		mf.setName("provider_providerId");
		try{
		mf.setValue(s.getProviderId());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(true);
		mf.setType("int");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("provider_shortName");
		mf.setValue(s.getShortName());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("provider_longName");
		mf.setValue(s.getLongName());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("provider_type");
		mf.setValue(s.getType());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); 
		
		int i = 1;
		for(ProviderResource pr : s.getProviderResourceSet()){
			mf  = new MetadataField();
			mf.setName("provider_path" + i);
			mf.setValue(pr.getResourcePath());
			mf.setRequired(true);
			mf.setType("char");
			mm.getFields().add(mf);
			i++;
		}
		return mm;
		}catch(NullPointerException npe){
			return null;
			
		}
	}
	
	private Provider mapXmlToProvider(MetadataManifest mf, Provider s) throws InventoryException {
		
		if(s == null)
			s = new Provider();
		for(MetadataField f :mf.getFields())
		{
			String fname = f.getName();
			String fval = f.getValue(); 
			try{
				if(f.getName().equals("provider_providerId")){
					s.setProviderId(Integer.valueOf(fval));	
				}
				else if(f.getName().equals("provider_shortName")){
					s.setShortName(fval);	
				}
				else if(f.getName().equals("provider_longName")){
					s.setLongName(fval);	
				}
				else if(f.getName().equals("provider_type")){
					s.setType(fval);	
				}
				else if(f.getName().startsWith("provider_path")){ //provider_path2
					ProviderResource pr = new ProviderResource();
					pr.setResourcePath(fval);
					s.getProviderResourceSet().add(pr);
				}

			}catch(Exception e){
				throw new InventoryException("Field/Value pair in Manifest caused exception: " + fname + ":" + fval);
			}
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
			throw new InventoryException("Provider id not found or improper format.");
		}
		
		Provider s = q.fetchProviderById(id);
		//Provider s = q.fetchProviderById(id);
		MetadataManifest mm = mapProviderToFields(s);
		if(mm == null){
			throw new InventoryException("Error mapping provider to XML");
		}
		return mm;
	}

	public MetadataManifest getTemplate() throws InventoryException {
		InputStream is = DatasetMetadataImpl.class.getResourceAsStream("/gov/nasa/podaac/inventory/xml/providerTemplate.xml");
		if(is == null){
			throw new InventoryException("Resource providerTemplate not found");
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

	public MetadataManifest update(MetadataManifest mf)
			throws InventoryException {
		
	 return addNew(mf);
		
	}

}

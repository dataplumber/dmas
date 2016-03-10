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
import gov.nasa.podaac.inventory.model.Source;

public class SourceMetadataImpl implements MetadataObject {
	Query q = null;
	DataManager manager;
	
	public SourceMetadataImpl(){
		q = QueryFactory.getInstance().createQuery();
		 manager = DataManagerFactory.getInstance()
		.createDataManager();
	}
	
	public MetadataManifest addNew(MetadataManifest mf)
			throws InventoryException {
		Source s = new Source();
		s = mapXmlToSource(mf, null);
		manager.addSource(s);
	
		MetadataManifest mani = new MetadataManifest();
		mani.setObject(Constant.ObjectType.SOURCE);
		mani.setType(Constant.ActionType.LIST);
		MetadataField field = new MetadataField();
		field.setName("report");
		field.setValue("Source: " + s.getSourceShortName() + "was successfully added");
		field.setType("char");
		mani.getFields().add(field);
		return mani;
	}

	private MetadataManifest mapSourceToFields(Source s){
		
		MetadataManifest mm = new MetadataManifest();
		
		mm.setObject(Constant.ObjectType.SOURCE);
		mm.setType(Constant.ActionType.TEMPLATE);
		
		//go through the dataset and create fields for each of the items.
		MetadataField mf = new MetadataField(); 
		
		mf.setName("source_sourceId");
		try{
		mf.setValue(s.getSourceId());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(true);
		mf.setType("int");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("source_shortName");
		mf.setValue(s.getSourceShortName());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("source_longName");
		mf.setValue(s.getSourceLongName());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("source_type");
		mf.setValue(s.getSourceType());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("source_orbitPeriod");
		try{
			mf.setValue(s.getOrbitPeriod());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(true);
		mf.setType("float");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("source_inclinationAngle");
		try{
			mf.setValue(s.getInclinationAngle());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(true);
		mf.setType("float");
		mm.getFields().add(mf); mf = new MetadataField();
		
		
		mf.setName("source_description");
		mf.setValue(s.getSourceDescription());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		
		
		return mm;
	}
	
	private Source mapXmlToSource(MetadataManifest mf, Source s) throws InventoryException {
		
		if(s == null)
			s = new Source();
		for(MetadataField f :mf.getFields())
		{
			String fname = f.getName();
			String fval = f.getValue(); 
			try{
				if(f.getName().equals("source_sourceId")){
					s.setSourceId(Integer.valueOf(fval));	
				}
				else if(f.getName().equals("source_shortName")){
					s.setSourceShortName(fval);	
				}
				else if(f.getName().equals("source_longName")){
					s.setSourceLongName(fval);	
				}
				else if(f.getName().equals("source_type")){
					s.setSourceType(fval);	
				}
				else if(f.getName().equals("source_orbitPeriod")){
					s.setOrbitPeriod(Float.valueOf(fval));	
				}
				else if(f.getName().equals("source_inclinationAngle")){
					s.setInclinationAngle(Float.valueOf(fval));	
				}
				else if(f.getName().equals("source_description")){
					s.setSourceDescription(fval);	
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
			throw new InventoryException("Source id not found or improper format.");
		}
		
		Source s = q.fetchSourceById(id);
		MetadataManifest mm = mapSourceToFields(s);
		return mm;
	}

	public MetadataManifest getTemplate() throws InventoryException {
		InputStream is = DatasetMetadataImpl.class.getResourceAsStream("/gov/nasa/podaac/inventory/xml/sourceTemplate.xml");
		if(is == null){
			throw new InventoryException("Resource sourceTemplate not found");
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

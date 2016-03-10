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
import gov.nasa.podaac.inventory.model.ElementDD;

public class ElementMetadataImpl implements MetadataObject {
	Query q = null;
	DataManager manager;
	
	public ElementMetadataImpl(){
		q = QueryFactory.getInstance().createQuery();
		 manager = DataManagerFactory.getInstance()
		.createDataManager();
	}
	
	public MetadataManifest addNew(MetadataManifest mf)
			throws InventoryException {
		ElementDD s = new ElementDD();
		s = mapXmlToElement(mf, null);
		manager.addElementDD(s);
	
		MetadataManifest mani = new MetadataManifest();
		mani.setObject(Constant.ObjectType.ELEMENT);
		mani.setType(Constant.ActionType.LIST);
		MetadataField field = new MetadataField();
		field.setName("report");
		field.setValue("Element: " + s.getShortName() + "was successfully added");
		field.setType("char");
		mani.getFields().add(field);
		return mani;
	}

	private MetadataManifest mapElementToFields(ElementDD s){
		
		MetadataManifest mm = new MetadataManifest();
		
		mm.setObject(Constant.ObjectType.ELEMENT);
		mm.setType(Constant.ActionType.TEMPLATE);
		
		//go through the dataset and create fields for each of the items.
		MetadataField mf = new MetadataField(); 
		
		mf.setName("element_elementId");
		try{
		mf.setValue(s.getElementId());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(true);
		mf.setType("int");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("element_shortName");
		mf.setValue(s.getShortName());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("element_longName");
		mf.setValue(s.getLongName());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("element_type");
		mf.setValue(s.getType());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("element_description");
		mf.setValue(s.getDescription());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
				
		mf.setName("element_maxLength");
		try{
			mf.setValue(s.getMaxLength());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(true);
		mf.setType("int");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("element_scope");
		mf.setValue(s.getScope());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		
		
		return mm;
	}
	
	private ElementDD mapXmlToElement(MetadataManifest mf, ElementDD s) throws InventoryException {
		
		if(s == null)
			s = new ElementDD();
		for(MetadataField f :mf.getFields())
		{
			String fname = f.getName();
			String fval = f.getValue(); 
			try{
				if(f.getName().equals("element_elementId")){
					s.setElementId(Integer.valueOf(fval));	
				}
				else if(f.getName().equals("element_shortName")){
					s.setShortName(fval);	
				}
				else if(f.getName().equals("element_longName")){
					s.setLongName(fval);	
				}
				else if(f.getName().equals("element_type")){
					s.setType(fval);	
				}
				else if(f.getName().equals("element_description")){
					s.setDescription(fval);	
				}
				else if(f.getName().equals("element_maxLength")){
					s.setMaxLength(Integer.valueOf(fval));	
				}
				else if(f.getName().equals("element_scope")){
					s.setScope(fval);	
				}
			}catch(Exception e){
				e.printStackTrace();
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
			throw new InventoryException("Element id not found or improper format.");
		}
		
		ElementDD s = q.fetchElementDDById(id);
		MetadataManifest mm = mapElementToFields(s);
		return mm;
	}

	public MetadataManifest getTemplate() throws InventoryException {
		InputStream is = DatasetMetadataImpl.class.getResourceAsStream("/gov/nasa/podaac/inventory/xml/elementTemplate.xml");
		if(is == null){
			throw new InventoryException("Resource elementTemplate not found");
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


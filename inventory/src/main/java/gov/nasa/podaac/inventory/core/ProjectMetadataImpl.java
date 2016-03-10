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
import gov.nasa.podaac.inventory.model.Project;

public class ProjectMetadataImpl implements MetadataObject {
	Query q = null;
	DataManager manager;
	
	public ProjectMetadataImpl(){
		q = QueryFactory.getInstance().createQuery();
		 manager = DataManagerFactory.getInstance()
		.createDataManager();
	}
	
	public MetadataManifest addNew(MetadataManifest mf)
			throws InventoryException {
		// TODO Auto-generated method stub
		Project s = new Project();
		s = mapXmlToProject(mf, null);
		manager.addProject(s);
		
		MetadataManifest mani = new MetadataManifest();
		mani.setObject(Constant.ObjectType.PROJECT);
		mani.setType(Constant.ActionType.LIST);
		MetadataField field = new MetadataField();
		field.setName("report");
		field.setValue("Project: " + s.getProjectShortName() + "was successfully added");
		field.setType("char");
		mani.getFields().add(field);
		return mani;
	}

	private MetadataManifest mapProjectToFields(Project s){
		
		MetadataManifest mm = new MetadataManifest();
		
		mm.setObject(Constant.ObjectType.PROJECT);
		mm.setType(Constant.ActionType.TEMPLATE);
		
		//go through the dataset and create fields for each of the items.
		MetadataField mf = new MetadataField(); 
		
		mf.setName("project_projectId");
		try{
		mf.setValue(s.getProjectId());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(true);
		mf.setType("int");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("project_shortName");
		mf.setValue(s.getProjectShortName());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("project_longName");
		mf.setValue(s.getProjectLongName());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		return mm;
	}
	
	private Project mapXmlToProject(MetadataManifest mf, Project s) throws InventoryException {
		
		if(s == null)
			s = new Project();
		for(MetadataField f :mf.getFields())
		{
			String fname = f.getName();
			String fval = f.getValue(); 
			try{
				if(f.getName().equals("project_projectId")){
					s.setProjectId(Integer.valueOf(fval));	
				}
				else if(f.getName().equals("project_shortName")){
					s.setProjectShortName(fval);	
				}
				else if(f.getName().equals("project_longName")){
					s.setProjectLongName(fval);	
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
			throw new InventoryException("Project id not found or improper format.");
		}
		
		Project s = q.fetchProjectById(id);
		//Project s = q.fetchProjectById(id);
		MetadataManifest mm = mapProjectToFields(s);
		return mm;
	}

	public MetadataManifest getTemplate() throws InventoryException {
		InputStream is = DatasetMetadataImpl.class.getResourceAsStream("/gov/nasa/podaac/inventory/xml/projectTemplate.xml");
		if(is == null){
			throw new InventoryException("Resource projectTemplate not found");
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

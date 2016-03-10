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
import gov.nasa.podaac.inventory.model.Contact;

public class ContactMetadataImpl implements MetadataObject {
	Query q = null;
	DataManager manager;
	
	public ContactMetadataImpl(){
		q = QueryFactory.getInstance().createQuery();
		 manager = DataManagerFactory.getInstance()
		.createDataManager();
	}
	
	public MetadataManifest addNew(MetadataManifest mf)
			throws InventoryException {
		Contact s = new Contact();
		s = mapXmlToContact(mf, null);
		manager.addContact(s);
	
		MetadataManifest mani = new MetadataManifest();
		mani.setObject(Constant.ObjectType.CONTACT);
		mani.setType(Constant.ActionType.LIST);
		MetadataField field = new MetadataField();
		field.setName("report");
		field.setValue("Contact: " + s.getFirstName() + "was successfully added");
		field.setType("char");
		mani.getFields().add(field);
		return mani;
	}

	private MetadataManifest mapContactToFields(Contact s){
		
		MetadataManifest mm = new MetadataManifest();
		
		mm.setObject(Constant.ObjectType.CONTACT);
		mm.setType(Constant.ActionType.TEMPLATE);
		
		//go through the dataset and create fields for each of the items.
		MetadataField mf = new MetadataField(); 
		
		mf.setName("contact_contactId");
		try{
		mf.setValue(s.getContactId());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(true);
		mf.setType("int");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("contact_role");
		mf.setValue(s.getRole());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("contact_firstName");
		mf.setValue(s.getFirstName());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("contact_middleName");
		mf.setValue(s.getMiddleName());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("contact_lastName");
		mf.setValue(s.getLastName());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		mf.setName("contact_email");
		mf.setValue(s.getEmail());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		mf.setName("contact_phone");
		mf.setValue(s.getPhone());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		mf.setName("contact_fax");
		mf.setValue(s.getFax());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		mf.setName("contact_address");
		mf.setValue(s.getAddress());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("contact_providerId");
		try{
			mf.setValue(s.getProvider().getProviderId());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(true);
		mf.setType("int");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("contact_notifyType");
		mf.setValue(s.getNotifyType());
		mf.setRequired(false);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		
		
		return mm;
	}
	
	private Contact mapXmlToContact(MetadataManifest mf, Contact s) throws InventoryException {
		
		if(s == null)
			s = new Contact();
		for(MetadataField f :mf.getFields())
		{
			String fname = f.getName();
			String fval = f.getValue(); 
			try{
				if(f.getName().equals("contact_contactId")){
					s.setContactId(Integer.valueOf(fval));	
				}
				else if(f.getName().equals("contact_role")){
					s.setRole(fval);	
				}
				else if(f.getName().equals("contact_firstName")){
					s.setFirstName(fval);	
				}
				else if(f.getName().equals("contact_middleName")){
					s.setMiddleName(fval);	
				}
				else if(f.getName().equals("contact_lastName")){
					s.setLastName(fval);	
				}
				else if(f.getName().equals("contact_email")){
					s.setEmail(fval);	
				}
				else if(f.getName().equals("contact_phone")){
					s.setPhone(fval);	
				}
				else if(f.getName().equals("contact_fax")){
					s.setFax(fval);	
				}
				else if(f.getName().equals("contact_address")){
					s.setAddress(fval);	
				}
				else if(f.getName().equals("contact_providerId")){
					s.getProvider().setProviderId(Integer.valueOf(fval));	
				}
				else if(f.getName().equals("contact_notifyType")){
					s.setNotifyType(fval);	
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
			throw new InventoryException("Contact id not found or improper format.");
		}
		
		Contact s = q.fetchContactById(id, "provider");
		MetadataManifest mm = mapContactToFields(s);
		return mm;
	}

	public MetadataManifest getTemplate() throws InventoryException {
		InputStream is = DatasetMetadataImpl.class.getResourceAsStream("/gov/nasa/podaac/inventory/xml/contactTemplate.xml");
		if(is == null){
			throw new InventoryException("Resource contactTemplate not found");
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


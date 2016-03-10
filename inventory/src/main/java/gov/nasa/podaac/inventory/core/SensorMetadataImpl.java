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
import gov.nasa.podaac.inventory.model.Sensor;

public class SensorMetadataImpl implements MetadataObject {
	Query q = null;
	DataManager manager;
	
	public SensorMetadataImpl(){
		q = QueryFactory.getInstance().createQuery();
		 manager = DataManagerFactory.getInstance()
		.createDataManager();
		
	}
	
	public MetadataManifest addNew(MetadataManifest mf)
			throws InventoryException {
		// TODO Auto-generated method stub
		Sensor s = new Sensor();
		s = mapXmlToSensor(mf, null);
		manager.addSensor(s);
		
		MetadataManifest mani = new MetadataManifest();
		mani.setObject(Constant.ObjectType.SENSOR);
		mani.setType(Constant.ActionType.LIST);
		MetadataField field = new MetadataField();
		field.setName("report");
		field.setValue("Sensor: " + s.getSensorShortName() + "was successfully added");
		field.setType("char");
		mani.getFields().add(field);
		return mani;
	}

	private MetadataManifest mapSensorToFields(Sensor s){
		
		MetadataManifest mm = new MetadataManifest();
		
		mm.setObject(Constant.ObjectType.SENSOR);
		mm.setType(Constant.ActionType.TEMPLATE);
		
		//go through the dataset and create fields for each of the items.
		MetadataField mf = new MetadataField(); 
		
		mf.setName("sensor_sensorId");
		try{
		mf.setValue(s.getSensorId());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(true);
		mf.setType("int");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("sensor_shortName");
		mf.setValue(s.getSensorShortName());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("sensor_longName");
		mf.setValue(s.getSensorLongName());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("sensor_swathWidth");
		try{
			mf.setValue(s.getSwathWidth());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(true);
		mf.setType("float");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("sensor_sampleFrequency");
		try{
			mf.setValue(s.getSampleFrequency());
		}catch(NullPointerException npe){
			mf.setValue("");
		}
		mf.setRequired(false);
		mf.setType("float");
		mm.getFields().add(mf); mf = new MetadataField();
		
		mf.setName("sensor_description");
		mf.setValue(s.getSensorDescription());
		mf.setRequired(true);
		mf.setType("char");
		mm.getFields().add(mf); mf = new MetadataField();
		
		
		
		return mm;
	}
	
	private Sensor mapXmlToSensor(MetadataManifest mf, Sensor s) throws InventoryException {
		
		if(s == null)
			s = new Sensor();
		for(MetadataField f :mf.getFields())
		{
			String fname = f.getName();
			String fval = f.getValue(); 
			try{
				if(f.getName().equals("sensor_sensorId")){
					s.setSensorId(Integer.valueOf(fval));	
				}
				else if(f.getName().equals("sensor_shortName")){
					s.setSensorShortName(fval);	
				}
				else if(f.getName().equals("sensor_longName")){
					s.setSensorLongName(fval);	
				}
				else if(f.getName().equals("sensor_swathWidth")){
					s.setSwathWidth(Float.valueOf(fval));	
				}
				else if(f.getName().equals("sensor_sampleFrequency")){
					try{
						s.setSampleFrequency(Float.valueOf(fval));	
					}catch(Exception e){
						s.setSampleFrequency(null);
					}
				}
				else if(f.getName().equals("sensor_description")){
					s.setSensorDescription(fval);	
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
			throw new InventoryException("Sensor id not found or improper format.");
		}
		
		Sensor s = q.fetchSensorById(id);
		MetadataManifest mm = mapSensorToFields(s);
		return mm;
	}

	public MetadataManifest getTemplate() throws InventoryException {
		InputStream is = DatasetMetadataImpl.class.getResourceAsStream("/gov/nasa/podaac/inventory/xml/sensorTemplate.xml");
		if(is == null){
			throw new InventoryException("Resource sensorTemplate not found");
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

package gov.nasa.podaac.inventory.test;
import gov.nasa.podaac.inventory.api.Constant;
import gov.nasa.podaac.inventory.api.DataManager;
import gov.nasa.podaac.inventory.api.DataManagerFactory;
import gov.nasa.podaac.inventory.api.Inventory;
import gov.nasa.podaac.inventory.api.InventoryFactory;
import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;

import gov.nasa.podaac.inventory.api.Service;
import gov.nasa.podaac.inventory.api.ServiceFactory;

import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetParameter;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleSpatial;
import junit.framework.TestCase;

public class ServiceFactoryIT extends TestCase {

	final static String HOST = "http://localhost:9191";
	
	public void testCallService(){
	
		Service s = ServiceFactory.getInstance().createService(HOST, 9192);
		try{
			Dataset d = s.getDataset(606);
			System.out.println("Dataset Shortname -> " + d.getShortName());
		}catch(InventoryException e){
			System.out.println("TEst fialed, exception caught for dataset.");
		}
		
		
	}
	
}

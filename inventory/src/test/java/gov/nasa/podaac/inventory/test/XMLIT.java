//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.inventory.test;

import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;
import junit.framework.TestCase;


/**
 * 
 * @author clwong
 *
 * @version
 * $Id: XMLTest.java 4221 2009-11-10 23:04:04Z gangl $
 */
public class XMLIT extends TestCase {

	public void testElementDDXML() {
		Query q = QueryFactory.getInstance().createQuery();
		q.mapElementDDToXML("/tmp/granuleElementDDList.xml");
	}

	public void testContactXML() {
		Query q = QueryFactory.getInstance().createQuery();
		q.mapContactToXML("/tmp/contactList.xml");
	}

	public void testProviderXML() {
		Query q = QueryFactory.getInstance().createQuery();
		q.mapProviderToXML("/tmp/providerList.xml");
	}

	public void testSensorXML() {
		Query q = QueryFactory.getInstance().createQuery();
		q.mapSensorToXML("/tmp/sensorList.xml");
	}

	public void testSourceXML() {
		Query q = QueryFactory.getInstance().createQuery();
		q.mapSourceToXML("/tmp/sourceList.xml");
	}

	public void testCollectionElementDDXML() {
		Query q = QueryFactory.getInstance().createQuery();
		q.mapCollectionElementDDToXML("/tmp/collectionElementDDList.xml");
	}
	
	public void testDatasetXML() {
		Query q = QueryFactory.getInstance().createQuery();
		q.mapDatasetToXML("/tmp/datasetList.xml");
	}
	public void testCollectionXML() {
		Query q = QueryFactory.getInstance().createQuery();
		q.mapCollectionToXML("/tmp/collectionList.xml");
	}
	public void testGranuleXML() {
		Query q = QueryFactory.getInstance().createQuery();
		q.mapGranuleToXML("/tmp/granuleList.xml");
	}
}

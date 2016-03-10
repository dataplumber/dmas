// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.api;

import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.model.CollectionContact;
import gov.nasa.podaac.inventory.model.CollectionDataset;
import gov.nasa.podaac.inventory.model.CollectionElementDD;
import gov.nasa.podaac.inventory.model.Contact;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetCoverage;
import gov.nasa.podaac.inventory.model.DatasetElement;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.DatasetReal;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleElement;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory;
import gov.nasa.podaac.inventory.model.Project;
import gov.nasa.podaac.inventory.model.Provider;
import gov.nasa.podaac.inventory.model.Sensor;
import gov.nasa.podaac.inventory.model.Source;

/**
 * This class defines interfaces for management of database entities.
 * 
 * @author clwong
 * @version $Id: DataManager.java 4752 2010-04-20 16:14:47Z gangl $
 *
 */
public interface DataManager {

	//-------------------  Manage Granule ------
	/**
	 * This method adds a granule into the database.
	 * @param granule
	 * @return Granule
	 */
	public Granule addGranule(Granule granule);
	
	/**
	 * This method updates a granule in the database.
	 * @param granule
	 */
	public void updateGranule(Granule granule);
	
	/**
	 * This method deletes a granule from the database.
	 * @param granule
	 */
	public void deleteGranule(Granule granule);

	//-------------------  Manage Granule Element Dictionary ------	
	/**
	 * This method adds a granule element definition into database.
	 * @param element
	 * @return ElementDD
	 */
	public ElementDD addElementDD(ElementDD element);
	
	public DatasetReal addDatasetReal(DatasetReal dr);
	
	/**
	 * This method deletes a granule element definition from the database.
	 * @param element
	 */
	public void deleteElementDD(ElementDD element);

	//-------------------  Manage Granule Metadata History ------
	/**
	 * This method adds a granule metadata history into database.
	 * @param metaHistory
	 * @return GranuleMetaHistory
	 */
	public GranuleMetaHistory addGranuleMetaHistory(GranuleMetaHistory metaHistory);

	//-------------------  Manage Granule Element ------
	/**
	 * This method adds a granule element into database.
	 * @param granuleElement
	 * @return GranuleElement
	 */
	public GranuleElement addGranuleElement(GranuleElement granuleElement);

	//-------------------  Manage Dataset ------	
	/**
	 * @param dataset
	 * @return
	 */
	public Dataset addDataset(Dataset dataset);
	
	public void updateDataset(Dataset dataset);
	public void updateDatasetDirty(Dataset dataset);
	
	public DatasetPolicy addDatasetPolicy(DatasetPolicy policy);
	
	public DatasetCoverage addDatasetCoverage(DatasetCoverage coverage);
	
	public void deleteDataset(Dataset dataset);
	
	//-------------------  Manage Source ------
	public Source addSource(Source source);
	
	//-------------------  Manage Sensor ------
	public Sensor addSensor(Sensor sensor);
	
	//-------------------  Manage Provider ------
	public Provider addProvider(Provider provider);
	
	public void deleteProvider(Provider provider);
	public void deleteCollectionDataset(CollectionDataset cd);
	
	//-------------------  Manage  Contact ------
	public Contact addContact(Contact contact);
	
	//-------------------  Manage Collection ------
	public Collection addCollection(Collection collection);
	
	//-------------------  Manage Collection Element Dictionary ------
	public CollectionElementDD addCollectionElementDD(CollectionElementDD collectionElementDD);	
	
	//-------------------  Manage Collection Dataset ------
	public CollectionDataset addCollectionDataset(CollectionDataset collectionDataset);

	//-------------------  Manage Collection Contact ------
	public CollectionContact addCollectionContact(CollectionContact collectionContact);

	public DatasetElement addDatasetElement(DatasetElement datasetElement);

	public Project addProject(Project s);
}

// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.tool;

import gov.nasa.podaac.inventory.api.DataManager;
import gov.nasa.podaac.inventory.api.DataManagerFactory;
import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.dao.GenericManager;
import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetPolicy;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author clwong
 * $Id: PolicyTool.java 4209 2009-11-10 00:18:58Z gangl $
 */
public class PolicyTool {
	
	private static Log log = LogFactory.getLog(PolicyTool.class);
	/**
	 * @param args
	 * @throws InventoryException 
	 */
	
	
	public static void main(String[] args) throws InventoryException {
		
		System.out.println("Usage: DatasetName PolicyCSVFile");
		String datasetName = args[0];
		Query q = QueryFactory.getInstance().createQuery();
		Dataset dataset = q.findDatasetByShortName(datasetName);
		if (dataset == null) {
			dataset = new Dataset();
		} else {
			dataset = q.fetchDatasetById(dataset.getDatasetId(), "policySet");
		}
		
		File file = new File(args[1]);
		LineIterator it = null; 
		try {
		   it = FileUtils.lineIterator(file);
		   while (it.hasNext()) {
		     String line = it.nextLine();
		     StringTokenizer st = new StringTokenizer(line,"\t");
			 	while (st.hasMoreTokens())
			 	{  
			 		String dataClass = st.nextToken();
			 		Integer dataFrequency = new Integer(st.nextToken());
			 		Integer dataDuration = new Integer(st.nextToken());
			 		String accessType = st.nextToken();
			 		String dataFormat = st.nextToken();
			 		String compressType = st.nextToken();
			 		String checksumType = st.nextToken();
			 		String accessConstraint = st.nextToken();
			 		String useConstraint = st.nextToken();
			 		
			 		DatasetPolicy policy = new DatasetPolicy();
			 		policy.setAccessConstraint(accessConstraint);
			 		policy.setAccessType(accessType);
			 		policy.setChecksumType(checksumType);
			 		policy.setCompressType(compressType);
			 		policy.setDataClass(dataClass);
			 		policy.setDataDuration(dataDuration);
			 		policy.setDataFormat(dataFormat);
			 		policy.setDataFrequency(dataFrequency);
			 		policy.setUseConstraint(useConstraint);
			 		
			 		dataset.setDatasetPolicy(policy);				
			 	}
		   }
		} catch (IOException e) {
				log.error(e.getMessage());	
		} finally {
		   LineIterator.closeQuietly(it);
		}
		
		// save dataset
		DataManager manager = DataManagerFactory.getInstance().createDataManager();
		manager.addDataset(dataset);

		// Verify
		Dataset result = q.fetchDatasetById(dataset.getDatasetId());
		DatasetPolicy datasetPolicy= result.getDatasetPolicy();
		
		if (datasetPolicy!=null) {
				System.out.println(datasetPolicy.toString());
		}
	}
}

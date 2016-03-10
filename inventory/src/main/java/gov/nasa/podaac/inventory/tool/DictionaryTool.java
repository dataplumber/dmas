/**
 * 
 */
package gov.nasa.podaac.inventory.tool;

import gov.nasa.podaac.inventory.api.DataManager;
import gov.nasa.podaac.inventory.api.DataManagerFactory;
import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.dao.GenericManager;
import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.model.ElementDD;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author clwong
 * $Id: DictionaryTool.java 4209 2009-11-10 00:18:58Z gangl $
 */
public class DictionaryTool {
	/**
	 * @param args
	 * @throws InventoryException 
	 */
	private static Log log = LogFactory.getLog(DictionaryTool.class);
	
	public static void main(String[] args) throws InventoryException {
			
		String fileName = "src/main/resources/GranuleAttribute.txt";
		if (args.length>0) {
			fileName = args[0];
			System.out.println("Use file: "+fileName);
		}

		File file = new File(fileName);
		List<ElementDD> elementList = new ArrayList<ElementDD>();		
		LineIterator it = null; 
		Query q = QueryFactory.getInstance().createQuery();
		try {
		   it = FileUtils.lineIterator(file);
		   while (it.hasNext()) {
		     String line = it.nextLine();
		     StringTokenizer st = new StringTokenizer(line,"\t");
			 	while (st.hasMoreTokens())
			 	{
			 		String shortName = st.nextToken();
			 		String longName = st.nextToken();
			 		String def = st.nextToken();
			 		String type = st.nextToken();
			 		String table = new String("");
			 		
			 		if (type.equals("character")) table = "GRANULE_CHARACTER";
			 		else if (type.equals("date") || type.equals("time") ||
			 				type.equals("timestamp")) table = "GRANULE_DATETIME";
			 		else if (type.equals("integer")) table = "GRANULE_INTEGER";
			 		else if (type.equals("real")) table = "GRANULE_REAL";
			 		else table="UNKNOWN";
			 		
			 		ElementDD exist = q.findElementDDByShortName(shortName);
			 		if (exist != null) {
			 			exist.setLongName(longName);
			 			exist.setDescription(def);
			 			exist.setType(type);
			 			exist.setDbTable(table);
			 			elementList.add(exist);
			 		} else {
			 			elementList.add(
			 				new ElementDD(shortName, longName, type, table, def)
			 			);
			 		}
			 	}
		   }
		} catch (IOException e) {
				log.error(e.getMessage());
		} finally {
		   LineIterator.closeQuietly(it);
		}
		
		// add elements to database
		DataManager manager = DataManagerFactory.getInstance().createDataManager();
 		for(ElementDD element : elementList) 
 			manager.addElementDD(element);
 		
 		// verify  
		List<ElementDD> resultList = q.listElementDD();
		assert resultList.size() == elementList.size();
		for(ElementDD element : resultList) {
			System.out.println(element.toString());
		}
		
		// generate xml file
		q.mapElementDDToXML("ElementDD.xml");
	}
}

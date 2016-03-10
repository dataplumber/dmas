package gov.nasa.podaac.inventory.tool;

import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.exceptions.InventoryException;

import java.util.Iterator;

public class PrintTableData {

	public static void main(String[] args) throws InventoryException {
		
		System.out.println("Usage: Tablename");
		String tableName = args[0];
		Query q = QueryFactory.getInstance().createQuery();
		Iterator results = q.list(tableName).iterator();
		while ( results.hasNext() ) { 
			Object[] row = (Object[]) results.next();
			for (int i=0; i<row.length; i++) {
				System.out.print(row[i]+"|");
			}
			System.out.println("");
		}
	}
}

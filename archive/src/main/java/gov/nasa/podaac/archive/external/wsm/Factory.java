/*
 * Created on Aug 26, 2010
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gov.nasa.podaac.archive.external.wsm;

import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import gov.nasa.podaac.archive.core.ArchiveProperty;
import gov.nasa.podaac.archive.external.InventoryAccess;
import gov.nasa.podaac.archive.external.InventoryFactory;
import gov.nasa.podaac.archive.external.InventoryQuery;

public class Factory extends InventoryFactory
{
   final private Access access;
   final private Query query;
   
   private static Log log = LogFactory.getLog(Factory.class);

   
   public Factory()
   {
       //Utilities u = null;
       
//       try
//       {
//           u = Utilities.getInstance();
//       }
//       catch (JAXBException jaxbe)
//       {
//           jaxbe.printStackTrace();
//           System.exit (-3);
//       }
//       catch (SAXException saxe)
//       {
//           saxe.printStackTrace();
//           System.exit (-4);
//       }
//       catch (URISyntaxException urise)
//       {
//           urise.printStackTrace();
//           System.exit (-5);
//       }
//       finally
//       {
	   	log.debug("Inventory URL: "+ArchiveProperty.getInstance().getProperty(ArchiveProperty.INVENTORY_URL));
	   	log.debug("Inventory PORT: "+ArchiveProperty.getInstance().getProperty(ArchiveProperty.INVENTORY_PORT));
    	   this.access= new Access(ArchiveProperty.getInstance().getProperty(ArchiveProperty.INVENTORY_URL),
    			   Integer.valueOf(ArchiveProperty.getInstance().getProperty(ArchiveProperty.INVENTORY_PORT)));
           this.query = new Query(ArchiveProperty.getInstance().getProperty(ArchiveProperty.INVENTORY_URL),
        		    Integer.valueOf(ArchiveProperty.getInstance().getProperty(ArchiveProperty.INVENTORY_PORT)));

//       }
   }
   
   @Override
   public InventoryAccess getAccess()
   {
      return this.access;
   }

   @Override
   public InventoryQuery getQuery()
   {
      return this.query;
   }
}

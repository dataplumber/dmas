package gov.nasa.podaac.distribute.subscriber.cache;

import gov.nasa.podaac.distribute.subscriber.cache.SubscriberCache.*;
import gov.nasa.podaac.distribute.subscriber.cache.SubscriberCache.Granule.*;
import gov.nasa.podaac.distribute.subscriber.cache.SubscriberCache.Granule.References.*;
import gov.nasa.podaac.distribute.subscriber.cache.SubscriberCache.Granule.Files.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.*;

public class SubscriberXml {

	//JAXBContext jaxbContext=JAXBContext.newInstance("generated");
	private static JAXBContext jaxbContext = null;
	
	public SubscriberXml()
	{
		//do nothing;
	}
	
	public static void toXml(HashSet<gov.nasa.podaac.distribute.subscriber.model.Granule> granuleList ,String xmlDoc)
	{
		try {
			 jaxbContext=JAXBContext.newInstance("gov.nasa.podaac.distribute.subscriber.cache");
			 Marshaller marshaller=jaxbContext.createMarshaller();
			 marshaller.setProperty(marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
			 ObjectFactory factory=new ObjectFactory(); 
			 SubscriberCache cache=(SubscriberCache)(factory.createSubscriberCache());
		
			 for(gov.nasa.podaac.distribute.subscriber.model.Granule g: granuleList)
			 {
				 Granule granule = (Granule)(factory.createSubscriberCacheGranule());
				 GranuleInfo gInfo = (GranuleInfo)(factory.createSubscriberCacheGranuleGranuleInfo());
				 Files gFiles = (Files)(factory.createSubscriberCacheGranuleFiles());
				 References gRefs = (References)(factory.createSubscriberCacheGranuleReferences());
				 
				 //set granule Info
				 gInfo.setId(g.getId());
				 gInfo.setName(g.getName());
				 gInfo.setStatus(g.getStatus());
				 
				 //set granule Refs
				 for(gov.nasa.podaac.distribute.subscriber.model.GranuleReference ref : g.getReferenceList())
				 {
					Reference gref = (Reference) (factory.createSubscriberCacheGranuleReferencesReference());
					gref.setPath(ref.getPath());
					gref.setStatus(ref.getStatus());
					gref.setType(ref.getType());
					
					gRefs.getReference().add(gref); 
				 }
				 granule.setReferences(gRefs);
				 //granuleFiles
				 for(gov.nasa.podaac.distribute.subscriber.model.GranuleFile file : g.getFileList())
				 {
					 File gfile = (File) (factory.createSubscriberCacheGranuleFilesFile());
					 
					 gfile.setName(file.getName());
					 gfile.setPath(file.getPath());
					 gfile.setStatus(file.getStatus());
					 gfile.setType(file.getType());
					 
					 gFiles.getFile().add(gfile);
				 }
				  granule.setFiles(gFiles);
				 granule.setGranuleInfo(gInfo);
				 cache.getGranule().add(granule);
			 }
 
			 marshaller.marshal(cache, new FileOutputStream(xmlDoc));
		
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
	}
	
	public static HashSet<gov.nasa.podaac.distribute.subscriber.model.Granule> fromXml(String fname)
	{
		JAXBContext jc = null;
		Unmarshaller u = null;
		SubscriberCache cache = null;
		try {
			jc = JAXBContext.newInstance("gov.nasa.podaac.distribute.subscriber.cache");
			u = jc.createUnmarshaller();
			URL url = new URL("file://" + fname );
		    // regions = (Regions)u.unmarshal(new FileInputStream("RegionMaster.xml"));
			cache = (SubscriberCache)u.unmarshal(url);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HashSet<gov.nasa.podaac.distribute.subscriber.model.Granule> granuleList = new HashSet<gov.nasa.podaac.distribute.subscriber.model.Granule>();
		
		for(Granule granule:cache.getGranule())
		{
			gov.nasa.podaac.distribute.subscriber.model.Granule g = new gov.nasa.podaac.distribute.subscriber.model.Granule();
			
			g.setId(granule.getGranuleInfo().getId());
			g.setName(granule.getGranuleInfo().getName());
			g.setStatus(granule.getGranuleInfo().getStatus());
			
			for(File f :granule.getFiles().getFile())
			{
				gov.nasa.podaac.distribute.subscriber.model.GranuleFile gf = new gov.nasa.podaac.distribute.subscriber.model.GranuleFile();
				gf.setName(f.getName());
				gf.setPath(f.getPath());
				gf.setStatus(f.getStatus());
				gf.setType(f.getType());
				
				g.getFileList().add(gf);
			}
			
			for(Reference r :granule.getReferences().getReference())
			{
				gov.nasa.podaac.distribute.subscriber.model.GranuleReference gr = new gov.nasa.podaac.distribute.subscriber.model.GranuleReference();
				
				gr.setPath(r.getPath());
				gr.setStatus(r.getStatus());
				gr.setType(r.getType());
			
				g.getReferenceList().add(gr);
			
			}
			granuleList.add(g);
		}
		
		return granuleList;

		/*List regionList = cache.getGranule(); 
		for (Iterator iter = regionList.iterator();
		iter.hasNext();) {
			Granule region = (Granule)iter.next();
			System.out.println(region.getGranuleInfo().getName()+ "\n");
			
		}*/
	}
	
	
}

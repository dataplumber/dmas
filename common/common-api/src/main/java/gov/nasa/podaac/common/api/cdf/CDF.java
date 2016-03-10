package gov.nasa.podaac.common.api.cdf;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public interface CDF {
	
	
	public static String MD_NAME = "podaac_name";
	public static String MD_START_TIME = "podaac_start_time";
	public static String MD_STOP_TIME = "podaac_stop_time";
	public static String MD_CYCLE = "podaac_cycle";
	public static String MD_DATASET= "podaac_dataset";
	public static String MD_CDF_VERSION= "podaac_cdf_version";
	
	public boolean registerMetadata(Metadata md);
	public boolean registerMetadata(List<Metadata> mdList);	
	public Metadata getMetadata(String key);
	public List<Metadata> getMetadata();
	
	public boolean toCDFFull(String output) throws IOException;
	public boolean toCDFHeader(String output) throws IOException;
	
	public void configure(String file, Properties props);
	public void close() throws IOException;
	
}

package gov.nasa.podaac.common.api.cdf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFileWriteable;

public class CDFImpl implements CDF {

	
	private static String CDF_VERSION = "PODAAC CDF V1.0";
	
	List<Metadata> mdList = null;
	//NetcdfDataset nd;
	NetcdfFileWriteable dataFile = null;
	String filename = null;
	
	public CDFImpl(String scratchDir){
		mdList = new ArrayList<Metadata>();
		String fname = scratchDir + File.separator + UUID.randomUUID() + ".cdf";
		filename = fname;
		this.registerMetadata(new Metadata(CDF.MD_CDF_VERSION,CDF_VERSION));
	}
	
	@Override
	public boolean registerMetadata(Metadata md) {
		return mdList.add(md);
	}

	@Override
	public boolean registerMetadata(List<Metadata> mdList) {
		return this.mdList.addAll(mdList);
	}

	@Override
	public Metadata getMetadata(String key) {
		for(Metadata m: mdList){
			if(m.getKey().equals(key)){
				return m;
			}
		}
//		for(Attribute at: dataFile.getGlobalAttributes()){
//			if(at.getName().equals(key))
//					return new Metadata(key,at.getValue(0));
//		}
		return null;
	}
	
	@Override
	public void close() throws IOException {
		if(dataFile != null)
			dataFile.close();
	}

	@Override
	public boolean toCDFFull(String output) {
		NetcdfFileWriteable dataFile;
		try {
			dataFile = NetcdfFileWriteable.createNew(filename, false);
			for(Metadata md : mdList){
	        	dataFile.addGlobalAttribute(md.getKey(), md.getValue().toString());
	        }
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean toCDFHeader(String output) {
		NetcdfFileWriteable dataFile;
		try {
			dataFile = NetcdfFileWriteable.createNew(filename, false);
			for(Metadata md : mdList){
	        	dataFile.addGlobalAttribute(md.getKey(), md.getValue().toString());
	        }
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	@Override
	public void configure(String file, Properties props) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Metadata> getMetadata() {
		List<Metadata> ll = new ArrayList<Metadata>(mdList);
		return ll;
	}

}

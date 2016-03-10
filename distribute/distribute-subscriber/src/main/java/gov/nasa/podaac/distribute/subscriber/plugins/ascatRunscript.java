package gov.nasa.podaac.distribute.subscriber.plugins;

import gov.nasa.podaac.distribute.datacasting.*;

public class ascatRunscript {

	public static void main(String[] args){
	
		String gName = args[0];
		String dName = args[1];
		String outDirectory = args[2];
		String extentsInfo = args[3];

		//use that info here
		Datacast.writeGranuleFile(gName, dName, extentsInfo, outDirectory);
		
	}	
}

package gov.nasa.podaac.distribute.subscriber.api;

import java.io.IOException;
import java.util.List;

import gov.nasa.podaac.distribute.subscriber.model.*;

public interface DataRetriever {

	/*
	 * @method get - takes a granule object and retrieves the files for each placing them in the data/dataset/granule/directory
	 */
	public List<String> get(Granule granule, String outputDir) throws IOException;
	
}

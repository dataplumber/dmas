package gov.nasa.podaac.distribute.subscriber.api;

import gov.nasa.podaac.distribute.subscriber.model.*;
import java.util.Date;

public interface Subscriber {

	/*
	 * @method list - Interface which will query for granules add to a dataset since lastRunTime
	 * @param dataset - the dataset object to which we will add granule name/information 
	 * @param lastRunTime -the start time of the subscribers last run/query
	 */
	public boolean list(Dataset dataset, Date lastRunTime) ;
	
	
}

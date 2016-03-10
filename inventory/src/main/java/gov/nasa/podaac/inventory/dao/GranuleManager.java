package gov.nasa.podaac.inventory.dao;

import gov.nasa.podaac.inventory.dao.EntityManager.GranuleDAO;
import gov.nasa.podaac.inventory.model.Granule;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;

public class GranuleManager extends GenericManager<Granule, Integer>
							implements GranuleDAO {

	private static Log log = LogFactory.getLog( GranuleManager.class);

	public List<Granule> listByIngestTimeRange(Date startTime, Date endTime) {
		log.debug("GranuleManager.findByIngestTimeRange: "+startTime + " " + endTime);
		List<Granule> result = findByCriteria(Restrictions.between("ingestTime", startTime, endTime));
		log.debug("GranuleManager.findByIngestTimeRange: done.");
		return result;
	}
}

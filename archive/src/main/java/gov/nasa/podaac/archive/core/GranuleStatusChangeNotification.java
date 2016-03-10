package gov.nasa.podaac.archive.core;

import java.util.Map;

import gov.nasa.podaac.archive.external.ArchiveMetadata;

public interface GranuleStatusChangeNotification {

	public void notifyChange(GranuleStatusChange gsc, Integer gId, Map<String,Object> mp);

	public void notifyChange(GranuleStatusChange gsc, ArchiveMetadata g, Map<String,Object> mp);

}

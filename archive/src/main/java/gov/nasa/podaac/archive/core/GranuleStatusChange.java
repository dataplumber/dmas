package gov.nasa.podaac.archive.core;

public enum GranuleStatusChange {
	DELETE,FULL_DELETE,ROLLING_STORE,REASSOCIATE,RELOCATE;

	public static String getOperation(GranuleStatusChange gsc) {
		switch(gsc){
			case DELETE:
				return "DELETE";
			case FULL_DELETE:
				return "FULL_DELETE";	
			case ROLLING_STORE:
				return "ROLLING_STORE";
			case REASSOCIATE:
				return "REASSOCIATE";
			case RELOCATE:
				return "RELOCATE";
			default: return "UNKNOWN OPERATION";	
		}
		
		
	}
}

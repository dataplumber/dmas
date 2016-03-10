package gov.nasa.podaac.archive.core;

public class GranuleStatusChangeFactory {

	private static GranuleStatusChangeNotification notifier;
	
	
	public static GranuleStatusChangeNotification getInstance(){
		if(notifier == null)
		{
			notifier = new GranuleStatusChangeFileNotifier();
		}
		return notifier;
	}
	
}

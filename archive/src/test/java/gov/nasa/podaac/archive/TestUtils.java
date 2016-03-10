package gov.nasa.podaac.archive;

public class TestUtils {

	
	public static void printTestInfo(String desc, int ticketNumber){
		System.out.println("---------------------------------------------------------");
		System.out.println("Running archive test: " + ticketNumber);
		System.out.println(desc);
		System.out.println("https://podaac-redmine.jpl.nasa.gov/issues/"+ticketNumber);
		System.out.println("---------------------------------------------------------");
	}
	
}

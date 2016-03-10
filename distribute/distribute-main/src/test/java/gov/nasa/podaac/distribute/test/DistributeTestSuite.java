package gov.nasa.podaac.distribute.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
 
@RunWith(Suite.class)
@Suite.SuiteClasses({
  //EMSTest.class,
  DistributeReleaseRegressionTest.class,
  DistributeRelease_4_3_0_Test.class
  
})
public class DistributeTestSuite {
    // the class remains completely empty, 
    // being used only as a holder for the above annotations


	public static void printTestInfo(String desc, int ticketNumber){
		System.out.println("---------------------------------------------------------");
		System.out.println("Running distribute test: " + ticketNumber);
		System.out.println(desc);
		System.out.println("https://podaac-redmine.jpl.nasa.gov/issues/"+ticketNumber);
		System.out.println("---------------------------------------------------------");
	}
}


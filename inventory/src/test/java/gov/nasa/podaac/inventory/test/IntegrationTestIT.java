package gov.nasa.podaac.inventory.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class IntegrationTestIT {

	//mvn clean test-compile failsafe:integration-test
	
	@Test
	public void aTest(){
		System.out.println("An Integration test!");
	}
	
}

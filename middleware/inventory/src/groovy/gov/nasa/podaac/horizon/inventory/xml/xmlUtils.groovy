package gov.nasa.podaac.horizon.inventory.xml;

import java.io.BufferedReader;

class xmlUtils {

	public static String requestXML(BufferedReader reader){
		StringBuilder sb = new StringBuilder();
		def line = reader.readLine()
				while(line != null){
			sb = sb.append(line);
			line = reader.readLine();
		}
		return sb.toString();
	}
	
	
}

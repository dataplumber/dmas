package gov.nasa.podaac.common.api.reader.api;


import java.io.IOException;
import java.util.List;

public abstract class DataFile {
	
	private List<Attribute> atList = null;
	
	public abstract DataVariable findVariable(String varName) ;
	public abstract Long[] getDimensions(String var);
	public abstract String getGlobalAttribute(String var);
	public abstract void open(String fileName) throws IOException;
	public abstract void write(String filename) throws IOException;
	public abstract void addVariable(String name, DataVariable dv);
	public abstract List<Attribute> getGlobalMetadata();
	public abstract List<Attribute> getDimensions();
	
	public  List<Attribute> getAttributeList(){
		return atList;
	}
	
}

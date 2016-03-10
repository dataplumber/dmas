package gov.nasa.podaac.common.api.reader.api;

import java.io.IOException;
import java.util.List;

public abstract class DataVariable {

	public abstract Long[] getDimensions();
	public abstract DataValue findAttributeValue(String attribute);
	//public abstract long findAttributeValue(String attribute, Class c);
	public abstract DataArray read() throws IOException;
	public abstract String getName();
	public abstract void setName(String name);
	public abstract String getDataType();
	public abstract List<Attribute> getAttributeMetaData();
	public abstract String getDimensionString(); 
	
}

package gov.nasa.podaac.common.api.reader.impl;

import gov.nasa.podaac.common.api.reader.api.DataArray;
import gov.nasa.podaac.common.api.reader.api.DataIterator;
import gov.nasa.podaac.common.api.reader.api.DataValue;

public class HDF4DataArray extends DataArray {

	Object data = null;
	String type = null;
	
	public HDF4DataArray(Object o, String type){
		this.data = o;
		this.type = type;
	}
	
	public HDF4DataArray() {
		//do nothing
	}

	@Override
	public Long getSize() {
		if(type == null)
			return 0L;
		if(data == null)
			return 0L;
		if(type.equals("short"))
			return new Long(((short[]) data).length);
		if(type.equals("int"))
			return new Long(((int[]) data).length);
		if(type.equals("long"))
			return new Long(((long[]) data).length);
		return null;
	}

	@Override
	public DataIterator getIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataValue getVal(int index) {
		if(type.equals("short"))
			return new DataValue(((short[]) data)[index], Short.class);
		if(type.equals("float"))
			return new DataValue(((float[]) data)[index], Float.class);
		if(type.equals("long"))
			return new DataValue(((long[]) data)[index], Long.class);
		if(type.equals("int"))
			return new DataValue(((int[]) data)[index], Integer.class);
		else
			return new DataValue();
		
	}

}

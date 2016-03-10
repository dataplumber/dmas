package gov.nasa.podaac.common.api.reader.impl;

import ucar.ma2.Array;
import ucar.ma2.Index;
import gov.nasa.podaac.common.api.reader.api.DataArray;
import gov.nasa.podaac.common.api.reader.api.DataIterator;
import gov.nasa.podaac.common.api.reader.api.DataValue;


public class NetCDFDataArray extends DataArray {

	private Array m_array = null;
	private String type;
	
	
	public NetCDFDataArray(Array a, String type){
		m_array = a;
		this.type = type;
		
	}
	
	@Override
	public Long getSize() {
		//System.out.println(m_array.getSize());
		return (Long)m_array.getSize();
	}
	
	
	

	@Override
	public DataIterator getIterator() {
		return new NetCDFDataIterator(m_array.getIndexIterator(), type);
	}
	
	@Override
	public DataValue getVal(int index) {
	
		Index i = m_array.getIndex();
		i.setCurrentCounter(index);
		//i.set(index);
		if(type.equals("float"))
			return new DataValue(m_array.getFloat(i), Float.class);
		if(type.equals("long"))
			return new DataValue(m_array.getLong(i), Long.class);
		if(type.equals("double"))
			return new DataValue(m_array.getDouble(i), Double.class);
		if(type.equals("int"))
			return new DataValue(m_array.getInt(i), Integer.class);
		if(type.equals("short"))
			return new DataValue(m_array.getInt(i), Integer.class);
		if(type.equals("byte"))
			return new DataValue(m_array.getByte(i), Byte.class);
		
		
		System.out.println("Unsupported type: " + type);
		return null;
	}

}

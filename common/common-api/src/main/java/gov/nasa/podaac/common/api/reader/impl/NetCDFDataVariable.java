package gov.nasa.podaac.common.api.reader.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ncsa.hdf.object.Datatype;
import ucar.ma2.DataType;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;
import gov.nasa.podaac.common.api.reader.api.Attribute;
import gov.nasa.podaac.common.api.reader.api.DataArray;
import gov.nasa.podaac.common.api.reader.api.DataValue;
import gov.nasa.podaac.common.api.reader.api.DataVariable;

public class NetCDFDataVariable extends DataVariable {

	private Variable m_var = null;
	private String name = null;
	
	public NetCDFDataVariable(Variable v, String name){
		m_var = v;
		this.setName(name);
	}
	
	@Override
	public Long[] getDimensions() {
		
		 Long[] dims = new Long[m_var.getDimensions().size()];
		 int c = 0;
		 for(Dimension d : m_var.getDimensions()){
			 //System.out.println(d.getLength());
			 dims[c] = new Long(d.getLength());
			 c++;
		 }
		 return dims;
	}

	@Override
	public DataValue findAttributeValue(String attribute) {
		
		if(m_var.findAttribute(attribute) == null)
			return new DataValue();
		
		m_var.findAttribute(attribute).getDataType();
		if(m_var.findAttribute(attribute).getDataType().equals(DataType.FLOAT))
			return new DataValue((m_var.findAttribute(attribute).getNumericValue()).floatValue(), Float.class);
		else if(m_var.findAttribute(attribute).getDataType().equals(DataType.DOUBLE))
			return new DataValue((m_var.findAttribute(attribute).getNumericValue()).doubleValue(), Double.class);
		else if(m_var.findAttribute(attribute).getDataType().equals(DataType.INT))
			return new DataValue((m_var.findAttribute(attribute).getNumericValue()).intValue(), Integer.class);
		else if(m_var.findAttribute(attribute).getDataType().equals(DataType.SHORT))
			return new DataValue((m_var.findAttribute(attribute).getNumericValue()).intValue(), Integer.class);
		else if(m_var.findAttribute(attribute).getDataType().equals(DataType.LONG))
			return new DataValue((m_var.findAttribute(attribute).getNumericValue()).longValue(),Long.class);
		
		return new DataValue();
		
	}

	@Override
	public String getDataType() {
		return 	m_var.getDataType().name(); 
	}
	
	
	
	@Override
	public DataArray read() throws IOException {
		return new NetCDFDataArray(m_var.read(),m_var.getDataType().toString() );
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public List<Attribute> getAttributeMetaData() {

		List<Attribute> lst = new ArrayList<Attribute>();
		for(ucar.nc2.Attribute a : m_var.getAttributes())
		{
			//System.out.println("processing attr: " + a.getName());
			Attribute att = null;
			if(a.getDataType().equals(DataType.STRING)){
				//System.out.println("STRING datatype");
				att = new Attribute(a.getName(), a.getStringValue());
				att.setType(a.getDataType().name());
			}else{
				//System.out.println(a.getDataType().name()+" datatype");
				if(a.getDataType().equals(DataType.FLOAT))
					att = new Attribute(a.getName(), (Float) a.getNumericValue().floatValue());
				if(a.getDataType().equals(DataType.DOUBLE))
					att = new Attribute(a.getName(), (Double) a.getNumericValue().doubleValue());
				if(a.getDataType().equals(DataType.SHORT))
					att = new Attribute(a.getName(), (Integer) a.getNumericValue().intValue());
				if(a.getDataType().equals(DataType.INT))
					att = new Attribute(a.getName(), (Integer) a.getNumericValue().intValue());
				if(a.getDataType().equals(DataType.LONG))
					att = new Attribute(a.getName(), (Long) a.getNumericValue().longValue());
				if(a.getDataType().equals(DataType.BYTE))
					att = new Attribute(a.getName(), (Byte) a.getNumericValue().byteValue());
				att.setType(a.getDataType().name());
			}
			
			lst.add(att);
			
		}
		return lst;
	}

	@Override
	public String getDimensionString() {
		String dims = "";
		boolean first = true;
		for(Dimension d: m_var.getDimensions()){
			if(first){
				dims += d.getName();
				first = false;
			}
			else
				dims += "," +d.getName();
		}
		
		return dims;
	}

}

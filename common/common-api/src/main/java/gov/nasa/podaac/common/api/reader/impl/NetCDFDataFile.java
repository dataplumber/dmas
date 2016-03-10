package gov.nasa.podaac.common.api.reader.impl;

import gov.nasa.podaac.common.api.reader.api.DataFile;
import gov.nasa.podaac.common.api.reader.api.DataVariable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;

public final class NetCDFDataFile extends DataFile{

	private NetcdfFile m_ncdfFile= null;
	
	
	@Override
	public DataVariable findVariable(String varName) {
		Variable v = m_ncdfFile.findVariable(varName);
		return new NetCDFDataVariable(v, varName);
	}

	@Override
	public void open(String fileName) throws IOException {
		m_ncdfFile = NetcdfFile.open(fileName);
	}
	
	@Override
	public Long[] getDimensions(String var) {
		//for netcdf, the string var is not needed. Dimensions are given in the header...
		 Long[] dims = new Long[m_ncdfFile.getDimensions().size()];
		 int c = 0;
		 for(Dimension d : m_ncdfFile.getDimensions()){
			 //System.out.println(d.getLength());
			 dims[c] = new Long(d.getLength());
			 c++;
		 }
		 
		 return dims;
	}
	
	@Override
	public List<gov.nasa.podaac.common.api.reader.api.Attribute> getDimensions() {
		
		List<gov.nasa.podaac.common.api.reader.api.Attribute> lst = new ArrayList<gov.nasa.podaac.common.api.reader.api.Attribute >();
		for(Dimension d: m_ncdfFile.getDimensions()){
			gov.nasa.podaac.common.api.reader.api.Attribute att = new gov.nasa.podaac.common.api.reader.api.Attribute(d.getName(),d.getLength());
			att.setType(DataType.INT.name());
			lst.add(att);
		}
		return lst;
	}

	@Override
	public String getGlobalAttribute(String var) {
		List<Attribute> atList = m_ncdfFile.getGlobalAttributes();
		for(Attribute a:atList){
			if(a.getName().equals(var)){
				return a.getStringValue();
				//return a.getValue(0).toString();
			}
		}
		return null;
	}

	@Override
	public void write(String filename) throws IOException {
		NetcdfFileWriteable writable = NetcdfFileWriteable.createNew(filename); 
		this.getAttributeList();
	}

	@Override
	public void addVariable(String name, DataVariable dv) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public List<gov.nasa.podaac.common.api.reader.api.Attribute> getAttributeList(){
		
		List<gov.nasa.podaac.common.api.reader.api.Attribute> ll = new ArrayList<gov.nasa.podaac.common.api.reader.api.Attribute>();
		for(Variable v : m_ncdfFile.getVariables()){
			ll.add(new gov.nasa.podaac.common.api.reader.api.Attribute(v.getName()));
		}
		
		return ll;
		//return atList;
	}

	@Override
	public List<gov.nasa.podaac.common.api.reader.api.Attribute> getGlobalMetadata() {
		List<gov.nasa.podaac.common.api.reader.api.Attribute> lst = new ArrayList<gov.nasa.podaac.common.api.reader.api.Attribute>();
		for(ucar.nc2.Attribute a : m_ncdfFile.getGlobalAttributes())
		{
			//System.out.println("processing attr: " + a.getName());
			gov.nasa.podaac.common.api.reader.api.Attribute att = null;
			if(a.getDataType().equals(DataType.STRING)){
				//System.out.println("STRING datatype");
				att = new gov.nasa.podaac.common.api.reader.api.Attribute(a.getName(), a.getStringValue());
				att.setType(a.getDataType().name());
			}else{
				//System.out.println(a.getDataType().name()+" datatype");
				if(a.getDataType().equals(DataType.FLOAT))
					att = new gov.nasa.podaac.common.api.reader.api.Attribute(a.getName(), (Float) a.getNumericValue().floatValue());
				if(a.getDataType().equals(DataType.DOUBLE))
					att = new gov.nasa.podaac.common.api.reader.api.Attribute(a.getName(), (Double) a.getNumericValue().doubleValue());
				if(a.getDataType().equals(DataType.SHORT))
					att = new gov.nasa.podaac.common.api.reader.api.Attribute(a.getName(), (Integer) a.getNumericValue().intValue());
				if(a.getDataType().equals(DataType.INT))
					att = new gov.nasa.podaac.common.api.reader.api.Attribute(a.getName(), (Integer) a.getNumericValue().intValue());
				if(a.getDataType().equals(DataType.LONG))
					att = new gov.nasa.podaac.common.api.reader.api.Attribute(a.getName(), (Long) a.getNumericValue().longValue());
				if(a.getDataType().equals(DataType.BYTE))
					att = new gov.nasa.podaac.common.api.reader.api.Attribute(a.getName(), (Byte) a.getNumericValue().byteValue());
				att.setType(a.getDataType().name());
			}
			lst.add(att);
		}
		return lst;
	}
}

package gov.nasa.podaac.common.api.reader.impl;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.HObject;



import gov.nasa.podaac.common.api.reader.api.DataArray;
import gov.nasa.podaac.common.api.reader.api.DataValue;
import gov.nasa.podaac.common.api.reader.api.DataVariable;

public class HDF4DataVariable extends DataVariable {

	private Dataset dataset = null;
	private String name = null;
	
	public HDF4DataVariable(Dataset d){
		this.dataset = d;
	}
	
	@Override
	public Long[] getDimensions() {
		return ArrayUtils.toObject(dataset.getDims());
	}

	
	
	@Override
	public DataValue findAttributeValue(String attribute) {
		 List<Attribute> meta = null;
		try {
			meta = dataset.getMetadata();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       for (Attribute a: meta){
    	   	
    	   if(a.getName().equals(attribute)){
    		   String type = a.getType().getDatatypeDescription();
    		   if(type.equals("64-bit floating-point")){
    			   return new DataValue(((double[])a.getValue())[0], Double.class);
//    			   System.out.println(a.getType().getDatatypeDescription());
//    			   double[] vals = (double[])a.getValue();
    		   }
//    		   System.out.println(a + ": " + vals[0]);
    	   }
       }
		
       return new DataValue();
	}

	@Override
	public DataArray read() throws IOException {
		try{
			String type= dataset.getDatatype().getDatatypeDescription();
			dataset.init();
			if(type.equals("16-bit unsigned integer")){
				int[] dLonInt = null;
	        	dLonInt = (int[])Dataset.convertFromUnsignedC((short[])dataset.read(), dLonInt);
				return new HDF4DataArray(dLonInt,"int");
			}
			else if(type.equals("32-bit floating-point")){
				float[] dLonInt = null;
	        	dLonInt = (float[])dataset.getData();
				return new HDF4DataArray(dLonInt,"float");
			}
			else if(type.equals("32-bit unsigned integer")){
				long[] dLonInt = null;
				dLonInt = (long[])Dataset.convertFromUnsignedC((int[])dataset.read(), dLonInt);
				return new HDF4DataArray(dLonInt,"long");
			}
			else if(type.equals("16-bit integer")){
				short dataLat[] = (short[])dataset.getData();
				return new HDF4DataArray(dataLat,"short");
			}
			
		}catch(Exception e){
			throw new IOException("Error reading variable: "+e.getMessage());
		}
		
		return new HDF4DataArray();
		
	}

	@Override
	public String getName() {
		return dataset.getName();
	}

	@Override
	public void setName(String name) {
		this.name = name;
		
	}

	@Override
	public String getDataType() {
		Datatype dt = dataset.getDatatype();
		dt.getName();
		
		return dt.getName(); 
	}

	@Override
	public List<gov.nasa.podaac.common.api.reader.api.Attribute> getAttributeMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDimensionString() {
		// TODO Auto-generated method stub
		return null;
	}

}

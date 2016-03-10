package gov.nasa.podaac.common.api.reader.impl;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import gov.nasa.podaac.common.api.reader.api.Attribute;
import gov.nasa.podaac.common.api.reader.api.DataFile;
import gov.nasa.podaac.common.api.reader.api.DataVariable;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;

public final class HDF4DataFile extends DataFile {

	
	private Group rootGroup = null;
	private FileFormat hdf4File = null;
	
	@Override
	public DataVariable findVariable(String varName) {
		for(Object mr : rootGroup.getMemberList()){
			HObject member = (HObject)mr;
			//System.out.println(member.getName());
			if(member.getName().equals(varName)){
				Dataset d = (Dataset)member;
				d.init(); //need to get the dataset info into memory. This is lame.
				DataVariable dv = new HDF4DataVariable(d);
				return dv;
			}
		}
		return null;
	}

	@Override
	public void open(String fileName) throws IOException {
		 FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF4);

//	        if (fileFormat == null)
//	        {
//	        	throw new IOException("Could not locate HDF4 File Formate");
//	        }

	        // open the file with read and write access
	        FileFormat testFile = null;
			try {
				testFile = fileFormat.createInstance(fileName, FileFormat.WRITE);
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new IOException("Error Opening HDF4 file: "+e1.getMessage());
			}

	        if (testFile == null)
	        {
	            System.err.println("Failed to open file: "+fileName);
	            return;
	        }

	        // open the file and retrieve the file structure
	        try {
				testFile.open();
			} catch (Exception e) {
				throw new IOException("Error Opening HDF4 file: "+e.getMessage());
			}
			
	        this.rootGroup = (Group)((javax.swing.tree.DefaultMutableTreeNode)testFile.getRootNode()).getUserObject();
	        this.hdf4File = testFile;
	}

	@Override
	public Long[] getDimensions(String var) {
		//use the var, generally lat, to get the dimensions.
		Dataset d = null;
		for(Object mr : rootGroup.getMemberList()){
			HObject member = (HObject)mr;
			//System.out.println(member.getName());
			if(member.getName().equals(var)){
				d = (Dataset)member;
				d.init(); //need to get the dataset info into memory. This is lame.
				long[] l = d.getDims();
				//System.out.println(l[0]+"x"+l[1]);
				return ArrayUtils.toObject(d.getDims());
			}
		}
		
		return null;
	}

	@Override
	public String getGlobalAttribute(String var) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(String filename) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addVariable(String name, DataVariable dv) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Attribute> getGlobalMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Attribute> getDimensions() {
		// TODO Auto-generated method stub
		return null;
	}

}

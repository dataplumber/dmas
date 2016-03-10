package gov.nasa.podaac.common.api.reader.impl;

import ucar.ma2.IndexIterator;
import gov.nasa.podaac.common.api.reader.api.DataIterator;

public class NetCDFDataIterator extends DataIterator {

	private IndexIterator m_it = null;
	private String type = "";
	
	public NetCDFDataIterator(IndexIterator it, String type){
		m_it = it;
		this.type = type;
	}
	
	@Override
	public boolean hasNext() {
		return m_it.hasNext();
	}

	
	
	@Override
	public Double next() {
		return m_it.getDoubleNext();
	}

}

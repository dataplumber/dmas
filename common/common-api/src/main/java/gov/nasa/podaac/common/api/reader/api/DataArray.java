package gov.nasa.podaac.common.api.reader.api;

import java.util.Iterator;

public abstract class DataArray {

	public abstract Long getSize();
	public abstract DataIterator getIterator();
	public abstract DataValue getVal(int index);
}

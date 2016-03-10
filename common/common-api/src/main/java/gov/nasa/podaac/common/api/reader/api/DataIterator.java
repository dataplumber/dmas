package gov.nasa.podaac.common.api.reader.api;

public abstract class DataIterator {

	public abstract boolean hasNext();
	public abstract Double next();

}

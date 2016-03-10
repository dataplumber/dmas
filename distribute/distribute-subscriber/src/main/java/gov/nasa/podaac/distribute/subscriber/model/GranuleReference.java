package gov.nasa.podaac.distribute.subscriber.model;

public class GranuleReference {

	String path, type, status;

	public GranuleReference(String path, String status, String type) {
		super();
		this.path = path;
		this.status = status;
		this.type = type;
	}
	
	public GranuleReference()
	{
		
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}	
	
}

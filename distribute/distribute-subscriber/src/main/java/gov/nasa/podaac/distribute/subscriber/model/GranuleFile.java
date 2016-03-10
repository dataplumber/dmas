package gov.nasa.podaac.distribute.subscriber.model;

public class GranuleFile {

	String path, type, status, name;

	public GranuleFile(String name, String path, String status, String type) {
		super();
		this.name = name;
		this.path = path;
		this.status = status;
		this.type = type;
	}
	
	public GranuleFile()
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
}

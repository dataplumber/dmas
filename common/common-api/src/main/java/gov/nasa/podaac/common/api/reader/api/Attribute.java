package gov.nasa.podaac.common.api.reader.api;

public class Attribute {

	private String name;
	private Object value;
	private String type;
	
	public Attribute(String name){
		this.name = name;
		this.value = null;
	}
	
	public Attribute(String name, Object value){
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	
	
	
}

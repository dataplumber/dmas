package gov.nasa.podaac.common.api.cdf;

public class Metadata {

	String key;
	Object value;
	
	public Metadata(String k, Object v){
		this.key = k;
		this.value = v;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
}

package gov.nasa.podaac.inventory.model;

import java.util.Date;

public class MetadataManifest {

	Integer manifestId;
	Integer itemId;
	String type;
	String diff;
	String manifest;
	String user;
	Long submissionDate;
	
	public MetadataManifest(Integer manifestId, Integer itemId, String user, String type, String manifest, String diff){
		
		this.manifestId = manifestId;
		this.manifest = manifest;
		this.type = type;
		this.itemId = itemId;
		this.user = user;
		this.diff = diff;
		this.submissionDate = new Date().getTime();
	}
	
	public MetadataManifest( Integer itemId, String user, String type, String manifest, String diff){
		this.diff = diff;
		this.manifest = manifest;
		this.type = type;
		this.itemId = itemId;
		this.user = user;
		this.submissionDate = new Date().getTime();
	}
	
	public MetadataManifest(){
		
	}
	public String getDiff() {
		return diff;
	}

	public void setDiff(String diff) {
		this.diff = diff;
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Long getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Long submissionDate) {
		this.submissionDate = submissionDate;
	}
	
	public Integer getManifestId() {
		return manifestId;
	}
	public void setManifestId(Integer manifestId) {
		this.manifestId = manifestId;
	}
	public Integer getItemId() {
		return itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getManifest() {
		return manifest;
	}
	public void setManifest(String manifest) {
		this.manifest = manifest;
	}
}

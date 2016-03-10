package gov.nasa.podaac.horizon.inventory.api;

class response {

	private String descritpion;
	private Integer status;
	private String error;

	public response(){
		description = "Ok"
		status = 200
		error = ""
	}
	
	public response(String desc, int stat, String err){
		this.descritpion=desc;
		this.status=stat;
		this.error = err;
	}
	
}

package gov.nasa.podaac.common.api.tool;

import java.io.IOException;
import java.util.Properties;

public class CLIApp {
	
	//Application info
	private String version = "unknown";
	private String appName= "unknown";
	
	//Build info
	private String buildDate = "unknown";
	
	//Package info
	private String packageVersion = "unknown";
	private String packageName = "unknown";

	/**
	 * CLI App constructor that loads version information from a given file.
	 * @param props - the resource file from which to load application information.
	 */
	public CLIApp(String props){
		loadProps(props);
	}
	
	/**
	 * Will attempt to load application from "application.properties" file.
	 **/
	public CLIApp(){
		loadProps("application.properties");
	}
	
	private void loadProps(String resource){
		Properties props = new Properties();
		try {
			props.load(this.getClass().getResourceAsStream ("/" + resource));
			setAppName(props.getProperty("application.name"));
			setVersion(props.getProperty("application.version"));
			
			setPackageVersion(props.getProperty("package.version"));
			setPackageName(props.getProperty("package.name"));
			
			setBuildDate(props.getProperty("application.build.date"));
		} catch (IOException e) {
			//log messages here
		}
	}
	
	public String getVersionString(){
		return String.format("%s version %s (Package: %s; Package Version: %s; build: %s)", getAppName(), getVersion(), getPackageName(), getPackageVersion(), getBuildDate());
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	public String getVersion() {
		return version;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppName() {
		return appName;
	}

	public void setBuildDate(String buildDate) {
		this.buildDate = buildDate;
	}

	public String getBuildDate() {
		return buildDate;
	}

	public void setPackageVersion(String packageVersion) {
		this.packageVersion = packageVersion;
	}

	public String getPackageVersion() {
		return packageVersion;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageName() {
		return packageName;
	}
}

package gov.nasa.podaac.common.api.zookeeper.api;

import gov.nasa.podaac.common.api.zookeeper.api.constants.JobPriority;
import gov.nasa.podaac.common.api.zookeeper.api.constants.RegistrationStatus;

import java.io.IOException;
import java.util.Map;

import org.apache.zookeeper.Watcher;


public interface ZkAccess {
	
	public static final String ARCHIVE_QUEUE = "/manager/queue/archive";
	public static final String INGEST_QUEUE = "/manager/queue/ingest";
	public static final String JOB_PROCESS_ROOT = "/manager/jobs";
	public static final String ENGINE_REGISTRATION_ROOT = "/engines";

	
	public boolean needsInit() ;
	/**
	 * @param storageEngine the name of the storage location this engine is using
	 * @param engineName the name of the engine that is registering
	 * @return path of the node created via registration
	 */
	public String register(String storageEngine, String engineName) throws IOException;
	
	/**
	 * @param storageEngine - the name of the storage location this engine is using
	 * @param engineName - the name of the engine that is checking registration
	 * @return true if the engine is still registered, false if not.
	 */
	public RegistrationStatus checkRegistration(String storageEngine, String engineName) throws IOException;
	
	
	/**
	 * @return Map with the following elements:
	 * [id:Long, password:String]. This should be saved to reaquire a lost session.
	 */
	public Map<String,Object> getSessionInfo();
	
//	/**
//	 * Method for pinging the zkServer.
//	 * @return returns true if a connection is available, returns false if not.
//	 */
//	public boolean ping();
//		
//	/**
//	 * 
//	 * @param nodeName - Path to the nod you want to watch 
//	 * @param w - Class implementing the 'Watcher' interface. Will call the 'process' method on any event changes.
//	 * @return
//	 */
//	public boolean addWatcherToNode(String nodeName, Watcher w); 
	
	/**
	 * 
	 * @param nodeName - the path of a node to check for
	 * @return true if the node exists, false if it doens't. You'll need to use the 'getNode' function to get the data of the node.
	 * @throws IOException 
	 */
	public boolean nodeExists(String nodeName) throws IOException; //check to see if a node exists
	
	/**
	 * This function adds a job to the Ingest queue.
	 * @param msg - the data package bundled with the node.
	 * @param String storageEngine - the string name of the storage engine this is to be read from/to 
	 * @return
	 */
	public String addToIngestQueue(String storageEngine,String msg, Watcher w) throws IOException; //manager to add ingest job
	
	public String addToIngestQueue(String storageEngine, String msg, JobPriority priority, Watcher w) throws IOException; //manager to add ingest job
	/**
	 * This function adds a job to the Archive queue.
	 * @param msg - the data package bundled with the node.
	 * @param String storageEngine - the string name of the storage engine this is to be read from/to 
	 * @return
	 */
	public String addToArchiveQueue(String storageEngine, String msg, Watcher w) throws IOException; //manager to add archive job
	
	public String addToArchiveQueue(String storageEngine, String msg, JobPriority priority, Watcher w) throws IOException; //manager to add archive job
	
	/**
	 * The function gets the 'data' portion of a znode. 
	 * @param nodeName - the path of a node to retrieve data from. 
	 * @return - Data associated with a node
	 */
	public String getNode(String nodeName) throws IOException;
	
	/**
	 * Wrapper method to grab a job from the Archive Q
	 * @return the data associated with job. 
	 */
	public String getArchiveJob(String storageEngine) throws IOException; //archive to get a job
	
	/**
	 * Wrapper method to grab a job from the Archive Q without blocking
	 * @return the data associated with job. 
	 */
	public String getArchiveJobNoBlock(String storageEngine) throws IOException; //archive to get a job
	
	/**
	 * Wrapper method to grab a job from the Ingest Q
	 * @return the data associated with job. 
	 */
	public String getIngestJob(String storageEngine) throws IOException; //ingest to get job
	
	/**
	 * Wrapper method to grab a job from the Ingest Q without blocking
	 * @return the data associated with job. 
	 */
	public String getIngestJobNoBlock(String storageEngine) throws IOException; //ingest to get job
	
	
	/**
	 * This function creates a node in the process tree. Usually done by ingest/archive engines to track and store job status for manager to check. 
	 * @param nodeName - Path of the node to create
	 * @param msg - the data portion. Can be something like 'in process' or other code.
	 * @return String representation of the node created. Should be the same as nodeName
	 */
	public String createProcessNode(String nodeName, String msg) throws IOException;
	
	/**
	 * This function updates a node in the process tree. Usually done by ingest/archive engines to track and store job status for manager to check. 
	 * @param nodeName - Path of the node to update
	 * @param msg - the data portion. Can be something like 'Success' or 'Failure' or other code.
	 * @return boolean true if successful, false if not updated.
	 */
	public boolean updateProcessNode(String nodeName, String msg) throws IOException;
	
	/**
	 * Same as 'getNode'
	 * @param nodeName - the path of a node to retrieve data from.
	 * @param watcher - Watcher code to attach to the znode (for code callback) 
	 * @return - Data associated with a node
	 */
	public String readProcessNode(String nodeName, Watcher watcher) throws IOException;
	
	public boolean removeProcessNode(String nodeName) throws IOException;
	public boolean removeNode(String nodeName) throws IOException;
	
	//utility create node commands
	/**
	 * 
	 * @param nodePath - the Path of the node to create
	 * @param message - the message/text to attach to the node
	 * @return String representation of the node created. Should be the same as nodePath
	 * @throws IOException
	 */
	public String createNode(String nodePath, String message) throws IOException;
	
	/**
	 * @param nodePath - the Path of the node to create
	 * @param message - the message/text to attach to the node
	 * @param isEphemeral - boolean for specifying if the node is ephemeral or not
	 * @return String representation of the node created. Should be the same as nodePath
	 * @throws IOException
	 * 
	 */
	public String createNode(String nodePath, String message, boolean isEphemeral) throws IOException;
	
	//basic close command
	public void closeConnection() throws IOException;
	
	
	
}

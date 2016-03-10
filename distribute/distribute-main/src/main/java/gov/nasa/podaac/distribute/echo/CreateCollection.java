/**
 *  Copyright (c) 2008, by the California Institute of Technology.
 *  ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 *
 *  $Id$
 */

package gov.nasa.podaac.distribute.echo;

import gov.nasa.podaac.distribute.echo.jaxb.collection.Collection;
import gov.nasa.podaac.distribute.echo.jaxb.collection.CollectionMetaDataFile;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Contact;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ContactPerson;
import gov.nasa.podaac.distribute.echo.jaxb.collection.CoordinateSystem;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Geometry;
import gov.nasa.podaac.distribute.echo.jaxb.collection.GranuleSpatialRepresentation;
import gov.nasa.podaac.distribute.echo.jaxb.collection.HorizontalSpatialDomain;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ScienceKeyword;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Instrument;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfCollections;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfContactPersons;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfContacts;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfScienceKeywords;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfEmails;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfInstruments;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfKeywords;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfOnlineAccessURLs;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfOnlineResources;
import gov.nasa.podaac.distribute.echo.jaxb.collection.BoundingRectangle;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfPhones;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfPlatforms;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfSensors;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ObjectFactory;
import gov.nasa.podaac.distribute.echo.jaxb.collection.OnlineAccessURL;
import gov.nasa.podaac.distribute.echo.jaxb.collection.OnlineResource;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Phone;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Platform;
import gov.nasa.podaac.distribute.echo.jaxb.collection.RangeDateTime;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Sensor;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Spatial;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Temporal;
import gov.nasa.podaac.inventory.hibernate.HibernateSessionFactory;
import gov.nasa.podaac.inventory.model.Dataset;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;


import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import org.xml.sax.SAXException;

/**
 *  This class extracts the appropriate metadata from the database and maps
 *  those values into the ECHO Collection schema for submission to ECHO. The
 *  end result is an ECHO-formatted XML file containing the metadata.
 *
 *  @author N. Barbic
 *  @version
 *  $Id$
 */
public class CreateCollection {
//	private static boolean useOracleQuery = true;
//	private static Log log = null;
//	private static String arguments = "";
//
//	/**
//	 *  Class name of Oracle JDBC driver
//	 */
//	private String driver = "oracle.jdbc.driver.OracleDriver";
//
//	/**
//	 *  Initial url fragment
//	 */
//	private String url = "jdbc:oracle:thin:@";
//
//	/**
//	 *  Standard Oracle listener port
//	 */
//	private String port = "1526";
//
//	/**
//	 *  Oracle style of calling a stored procedure
//	 */
//	private static String oraCollectionListQuery = "begin ? := echo.get_collection_list(); end;";
//	private static String oraCollectionQuery = "begin ? := echo.get_collection(?); end;";
//	private static String oraVersionQuery = "begin ? := echo.get_version(?); end;";
//	private static String oraContactQuery = "begin ? := echo.get_contact(?); end;";
//	private static String oraParameterQuery = "begin ? := echo.get_parameter(?); end;";
//	private static String oraPlatformQuery = "begin ? := echo.get_platform(?); end;";
//	private static String oraAccessQuery = "begin ? := echo.get_access(?); end;";
//	private static String oraResourceQuery = "begin ? := echo.get_resource(?); end;";
//
//	/**
//	 *  JDBC style of calling a stored procedure
//	 */
//	private static String jdbcCollectionListQuery = "{ call ? := echo.get_collection_list() }";
//	private static String jdbcCollectionQuery = "{ call ? := echo.get_collection(?) }";
//	private static String jdbcVersionQuery = "{ call ? := echo.get_version(?) }";
//	private static String jdbcContactQuery = "{ call ? := echo.get_contact(?) }";
//	private static String jdbcParameterQuery = "{ call ? := echo.get_parameter(?) }";
//	private static String jdbcPlatformQuery = "{ call ? := echo.get_platform(?) }";
//	private static String jdbcAccessQuery = "{ call ? := echo.get_access(?) }";
//	private static String jdbcResourceQuery = "{ call ? := echo.get_resource(?) }";
//
//	/**
//	 *  Connection to database
//	 */
//	private Connection conn = null;
//
//	/**
//	 *  Constructor. Loads the JDBC driver and establishes a connection to
//	 *  the database.
//	 *
//	 *  @param  host        the host the db is on
//	 *  @param  db          the database name
//	 *  @param  user        user's name
//	 *  @param  password    user's password
//	 */
//	public CreateCollection(String host, String db, String user, String password)
//			throws ClassNotFoundException, SQLException {
//		// construct the url
//		url = url + host + ":" + port + ":" + db;
//		connect(user, password);
//	}
//
//	public CreateCollection() throws ClassNotFoundException, SQLException {
//		log.info("CreateCollection: use hibernate.cfg.xml properties");	
//		HibernateSessionFactory.getInstance();
//		Configuration cfg = HibernateSessionFactory.getConfiguration();
//		//cfg.getProperties().list(System.out);
//		driver = cfg.getProperty("connection.driver_class");
//		url = cfg.getProperty("connection.url");
//		String user = cfg.getProperty("connection.username");
//		String pass = cfg.getProperty("connection.password");
//		connect(user, pass);
//	}
//
//	public void connect(String user, String password) 
//		throws ClassNotFoundException, SQLException {
//		log.debug("connect: "+user);
//		// load the Oracle driver and establish a connection
//		try {
//			Class.forName(driver);
//			conn = DriverManager.getConnection(url, user, password);
//		} catch (ClassNotFoundException ex) {
//			System.out.println("Failed to find driver class: " + driver);
//			throw ex;
//		} catch (SQLException ex) {
//			System.out.println("Failed to establish a connection to: " + url);
//			throw ex;
//		}
//	}
//
//	/**
//	 *  Create ECHO Collection submissions based on the collections
//	 *  that are ready for submission. This is determined by evaluating
//	 *  the collection's ECHO submission date against the associated
//	 *  dataset's last revision date.
//	 *
//	 *  @throws  SQLException  When any subsequent queries fail.
//	 */
//	public void createSubmission() throws SQLException {
//
//		// Set the collection query.
//		String cQuery = useOracleQuery ? oraCollectionListQuery
//				: jdbcCollectionListQuery;
//		CallableStatement cStmt = conn.prepareCall(cQuery);
//		System.out.println(cQuery);
//
//		// Register the output parameter for the query.
//		cStmt.registerOutParameter(1, OracleTypes.CURSOR);
//
//		// Execute and retrieve the result set from the collection query.
//		cStmt.execute();
//		ResultSet cRs = (ResultSet) cStmt.getObject(1);
//
//		// Create a submission for each collection returned.
//		while (cRs.next()) {
//			// Get the collection info.
//			int collectionId = cRs.getInt(1);
//			Timestamp echoSubmitDate = cRs.getTimestamp(2);
//			int datasetId = cRs.getInt(3);
//
//			// Set the version query.
//			String vQuery = useOracleQuery ? oraVersionQuery : jdbcVersionQuery;
//			CallableStatement vStmt = conn.prepareCall(vQuery);
//
//			// Register the output parameters for the version query.
//			vStmt.registerOutParameter(1, OracleTypes.CURSOR);
//			vStmt.setInt(2, datasetId);
//
//			// Execute and retrieve the result set from the version query.
//			vStmt.execute();
//			ResultSet vRs = (ResultSet) vStmt.getObject(1);
//			Timestamp lastRevisionDate = null;
//			while (vRs.next()) {
//				lastRevisionDate = vRs.getTimestamp(3);
//			}
//
//			// Check just in case. The query will come back null if there is no
//			// corresponding entry in the dataset_version table.
//			if (lastRevisionDate == null) {
//				lastRevisionDate = new Timestamp(0);
//			}
//
//			// Determine whether we should create the submission.
//			if (echoSubmitDate == null
//					|| echoSubmitDate.before(lastRevisionDate)) {
//				createSubmission(collectionId);
//			}
//
//			// Clean up just the current version query.
//			vRs.close();
//			vStmt.close();
//		}
//		cRs.close();
//		cStmt.close();
//	}
//
//	/**
//	 *  Create a specific ECHO Collection submission based on the identifier.
//	 *
//	 *  @param  collectionId  The collection identifier for the collection.
//	 */
//	public void createSubmission(int collectionId) throws SQLException {
//
//		System.out.println("Creating submission for collection '"
//				+ collectionId + "'.");
//
//		CollectionMetaDataFile collectionFile = new CollectionMetaDataFile();
//		ListOfCollections collections = new ListOfCollections();
//		Collection collection = new Collection();
//		int datasetId = getCollection(collection, collectionId);
//		if(datasetId==0)
//		{
//			System.out.println("Cannot retrieve dataset information for collection '"+collectionId+"'. Skipping dataset.");
//			return;
//		}
//		
//		
//		getContact(collection, collectionId);
//		//getParameter(collection, datasetId);
//		getPlatform(collection, datasetId);
//		getAccess(collection, datasetId);
//		getSpatial(collection, datasetId);
//		getResource(collection, datasetId);
//		collections.getCollection().add(collection);
//		collectionFile.setCollections(collections);
//		createXml(collectionFile, collectionId);
//	}
//
//	/**
//	 *  Get the collection information from the database.
//	 *
//	 *  @param   collection    The collection structure to be populated.
//	 *  @param   collectionId  The collection identifier for the collection.
//	 *  @return  The dataset identifier related to the collection.
//	 *  @throws  SQLException
//	 */
//	public int getCollection(Collection collection, int collectionId)
//			throws SQLException {
//
//		// Set the query.
//		String query = useOracleQuery ? oraCollectionQuery
//				: jdbcCollectionQuery;
//		CallableStatement stmt = conn.prepareCall(query);
//		
//		// Register the output and input parameters.
//		stmt.registerOutParameter(1, OracleTypes.CURSOR);
//		stmt.setInt(2, collectionId);
//
//		
//		
//		// Execute and retrieve the result set.
//		stmt.execute();
//		ResultSet rs = (ResultSet) stmt.getObject(1);
//
//		// Populate the collection information.
//		String shortName = "";
//		int datasetId = 0;
//		while (rs.next()) {
//			shortName = rs.getString(1);
//			collection.setShortName(shortName);
//			collection.setLongName(rs.getString(2));
//			collection.setDescription(rs.getString(3));
//			boolean visible = true;
//			collection.setVisible(visible);
//			collection.setRevisionDate(currentDate());
//			collection.setProcessingCenter(rs.getString(4));
//			collection.setProcessingLevelId(rs.getString(5));
//			collection.setDataFormat(rs.getString(6));
//			ListOfKeywords spatialKeywords = new ListOfKeywords();
//			spatialKeywords.getKeyword().add(rs.getString(7));
//			collection.setSpatialKeywords(spatialKeywords);
//			Temporal temporal = new Temporal();
//			RangeDateTime range = new RangeDateTime();
//			range.setBeginningDateTime(convertDate(rs.getDate(8)));
//			range.setEndingDateTime(convertDate(rs.getDate(9)));
//			temporal.getRangeDateTime().add(range);
//			collection.setTemporal(temporal);
//			datasetId = rs.getInt(10);
//		}
//		rs.close();
//		stmt.close();
//
//		// Set the query.
//		query = useOracleQuery ? oraVersionQuery : jdbcVersionQuery;
//		stmt = conn.prepareCall(query);
//
//		// Register the output and input parameters.
//		stmt.registerOutParameter(1, OracleTypes.CURSOR);
//		stmt.setInt(2, datasetId);
//
//		// Execute and retrieve the result set.
//		stmt.execute();
//		rs = (ResultSet) stmt.getObject(1);
//
//		// Populate the version information.
//		while (rs.next()) {
//			String versionId = rs.getString(1);
//			collection.setVersionId(versionId);
//			collection.setInsertTime(convertDate(rs.getDate(2)));
//			collection.setLastUpdate(convertDate(rs.getDate(3)));
//			collection.setDataSetId(shortName + ":" + versionId);
//		}
//		rs.close();
//		stmt.close();
//
//		return datasetId;
//	}
//
//	/**
//	 *  Get the contact information from the database.
//	 *
//	 *  @param  collection    The collection structure to be populated.
//	 *  @param  collectionId  The collection identifier for the collection.
//	 *  @throws SQLException
//	 */
//	public void getContact(Collection collection, int collectionId)
//			throws SQLException {
//
//		// Set the query.
//		String query = useOracleQuery ? oraContactQuery : jdbcContactQuery;
//		CallableStatement stmt = conn.prepareCall(query);
//
//		// Register the output and input parameters.
//		stmt.registerOutParameter(1, OracleTypes.CURSOR);
//		stmt.setInt(2, collectionId);
//
//		// Execute and retrieve the result set.
//		stmt.execute();
//		ResultSet rs = (ResultSet) stmt.getObject(1);
//
//		// Populate the contact information.
//		ListOfContacts contacts = new ListOfContacts();
//		while (rs.next()) {
//			Contact contact = new Contact();
//			contact.setRole("Archiver");
//			contact.setOrganizationName(rs.getString(1));
//			ListOfPhones phones = new ListOfPhones();
//			Phone phone = new Phone();
//			phone.setNumber(rs.getString(2));
//			phone.setType("Primary");
//			phones.getPhone().add(phone);
//			contact.setOrganizationPhones(phones);
//			ListOfEmails email = new ListOfEmails();
//			email.getEmail().add(rs.getString(3));
//			contact.setOrganizationEmails(email);
//			ListOfContactPersons persons = new ListOfContactPersons();
//			ContactPerson person = new ContactPerson();
//			person.setFirstName(rs.getString(4));
//			person.setMiddleName(rs.getString(5));
//			person.setLastName(rs.getString(6));
//			person.setJobPosition(rs.getString(7));
//			persons.getContactPerson().add(person);
//			contact.setContactPersons(persons);
//			contacts.getContact().add(contact);
//		}
//		rs.close();
//		stmt.close();
//		collection.setContacts(contacts);
//	}
//
//	/**
//	 *  Get the parameter information from the database.
//	 *
//	 *  @param  collection   The collection structure to be populated.
//	 *  @param  datasetId    The dataset identifier related to the collection.
//	 *  @throws SQLException
//	 */
//	public void getParameter(Collection collection, int datasetId)
//			throws SQLException {
//
//		// Set the query.
//		String query = useOracleQuery ? oraParameterQuery : jdbcParameterQuery;
//		CallableStatement stmt = conn.prepareCall(query);
//
//		// Register the output and input parameters.
//		stmt.registerOutParameter(1, OracleTypes.CURSOR);
//		stmt.setInt(2, datasetId);
//
//		// Execute and retrieve the result set.
//		stmt.execute();
//		ResultSet rs = (ResultSet) stmt.getObject(1);
//
//		// Populate the parameter information.
//		ListOfScienceKeywords parameters = new ListOfScienceKeywords();
//		while (rs.next()) {
//			ScienceKeyword parameter = new ScienceKeyword();
//			parameter.setCategoryKeyword(rs.getString(1));
//			parameter.setTopicKeyword(rs.getString(2));
//			parameter.setTermKeyword(rs.getString(3));
//			//parameters.getScienceKeyword().add(parameter);
//			//parameter.setVariableKeyword(rs.getString(4));
//			//parameters.getDisciplineTopicParameter().add(parameter);
//		}
//		rs.close();
//		stmt.close();
//		collection.setScienceKeywords(parameters);
//	}
//
//	/**
//	 *  Get the platform (source and sensor) information from the database.
//	 *
//	 *  @param  collection   The collection structure to be populated.
//	 *  @param  datasetId    The dataset identifier related to the collection.
//	 *  @throws SQLException 
//	 */
//	public void getPlatform(Collection collection, int datasetId)
//			throws SQLException {
//
//		// Set the query.
//		String query = useOracleQuery ? oraPlatformQuery : jdbcPlatformQuery;
//		CallableStatement stmt = conn.prepareCall(query);
//
//		// Register the output and input parameters.
//		stmt.registerOutParameter(1, OracleTypes.CURSOR);
//		stmt.setInt(2, datasetId);
//
//		// Execute and retrieve the result set.
//		stmt.execute();
//		ResultSet rs = (ResultSet) stmt.getObject(1);
//
//		// Populate the platform information.
//		ListOfPlatforms platforms = new ListOfPlatforms();
//		while (rs.next()) {
//			Platform platform = new Platform();
//			platform.setShortName(rs.getString(1));
//			platform.setLongName(rs.getString(2));
//			platform.setType(rs.getString(3));
//			ListOfInstruments instruments = new ListOfInstruments();
//			Instrument instrument = new Instrument();
//			instrument.setShortName(rs.getString(4));
//			instrument.setLongName(rs.getString(5));
//			ListOfSensors sensors = new ListOfSensors();
//			Sensor sensor = new Sensor();
//			sensor.setShortName(rs.getString(4));
//			sensor.setLongName(rs.getString(5));
//			sensors.getSensor().add(sensor);
//			instrument.setSensors(sensors);
//			instruments.getInstrument().add(instrument);
//			platform.setInstruments(instruments);
//			platforms.getPlatform().add(platform);
//		}
//		rs.close();
//		stmt.close();
//		collection.setPlatforms(platforms);
//	}
//
//	/**
//	 *  Get the online access information from the database.
//	 *
//	 *  @param  collection   The collection structure to be populated.
//	 *  @param  datasetId    The dataset identifier related to the collection.
//	 *  @throws SQLException
//	 */
//	public void getAccess(Collection collection, int datasetId)
//			throws SQLException {
//
//		// Set the query.
//		String query = useOracleQuery ? oraAccessQuery : jdbcAccessQuery;
//		CallableStatement stmt = conn.prepareCall(query);
//
//		// Register the output and input parameters.
//		stmt.registerOutParameter(1, OracleTypes.CURSOR);
//		stmt.setInt(2, datasetId);
//
//		// Execute and retrieve the result set.
//		stmt.execute();
//		ResultSet rs = (ResultSet) stmt.getObject(1);
//
//		// Populate the resource information.
//		ListOfOnlineAccessURLs accessURLs = new ListOfOnlineAccessURLs();
//		while (rs.next()) {
//			OnlineAccessURL accessURL = new OnlineAccessURL();
//			accessURL.setURL(rs.getString(1));
//			String type = rs.getString(2);
//			if (type.equals("FTP"))
//				accessURL
//						.setURLDescription("The FTP base directory location for the collection.");
//			else if (type.equals("OPENDAP"))
//				accessURL
//						.setURLDescription("The OPeNDAP base directory location for the collection.");
//			else
//				accessURL
//						.setURLDescription("The base directory location for the collection.");
//			accessURLs.getOnlineAccessURL().add(accessURL);
//		}
//		rs.close();
//		stmt.close();
//		collection.setOnlineAccessURLs(accessURLs);
//	}
//
//	/**
//	 *  Get the online resource information from the database.
//	 *
//	 *  @param  collection   The collection structure to be populated.
//	 *  @param  datasetId    The dataset identifier related to the collection.
//	 *  @throws SQLException
//	 */
//	public void getSpatial(Collection collection, int datasetId)
//			throws SQLException {
//
//		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
//		Dataset d = q.fetchDatasetById(datasetId);
//		if(d == null)
//		{
//			System.out.println("couldn't fetch dataset id '"+datasetId+"'");
//		}
//		
//		//create HorizontalSpatialDomain
//		HorizontalSpatialDomain hsd = new HorizontalSpatialDomain();
//		
//		//create geometry/BoundingBox
//		Geometry geo = new Geometry();
//		BoundingRectangle br = new BoundingRectangle();
//			
//			br.setWestBoundingCoordinate(BigDecimal.valueOf(d.getDatasetCoverage().getWestLon()));
//			br.setEastBoundingCoordinate(BigDecimal.valueOf(d.getDatasetCoverage().getEastLon()));
//			br.setNorthBoundingCoordinate(BigDecimal.valueOf(d.getDatasetCoverage().getNorthLat()));
//			br.setSouthBoundingCoordinate(BigDecimal.valueOf(d.getDatasetCoverage().getSouthLat()));
//			
//		geo.getPointOrBoundingRectangleOrGPolygon().add(br);
//		geo.setCoordinateSystem(CoordinateSystem.CARTESIAN);
//		hsd.setGeometry(geo);
//		hsd.setZoneIdentifier(null);		
//		
//		//create spatial
//		
//		Spatial sp = new Spatial();
//		
//		//add these items to the SPATIAL type
//		sp.setSpatialCoverageType("HORIZONTAL");
//		sp.setGranuleSpatialRepresentation(GranuleSpatialRepresentation.CARTESIAN);
//		sp.setHorizontalSpatialDomain(hsd);
//		
//		collection.setSpatial(sp);
//	}
//	
//	
//	/**
//	 *  Get the online resource information from the database.
//	 *
//	 *  @param  collection   The collection structure to be populated.
//	 *  @param  datasetId    The dataset identifier related to the collection.
//	 *  @throws SQLException
//	 */
//	public void getResource(Collection collection, int datasetId)
//			throws SQLException {
//
//		// Set the query.
//		String query = useOracleQuery ? oraResourceQuery : jdbcResourceQuery;
//		CallableStatement stmt = conn.prepareCall(query);
//
//		// Register the output and input parameters.
//		stmt.registerOutParameter(1, OracleTypes.CURSOR);
//		stmt.setInt(2, datasetId);
//
//		// Execute and retrieve the result set.
//		stmt.execute();
//		ResultSet rs = (ResultSet) stmt.getObject(1);
//
//		// Populate the resource information.
//		ListOfOnlineResources resources = new ListOfOnlineResources();
//		while (rs.next()) {
//			OnlineResource resource = new OnlineResource();
//			resource.setURL(rs.getString(1));
//			resource.setDescription(rs.getString(2));
//			resource.setType(rs.getString(3));
//			resources.getOnlineResource().add(resource);
//		}
//		rs.close();
//		stmt.close();
//		collection.setOnlineResources(resources);
//	}
//
//	/**
//	 *  Create the XML representation of the ECHO Collection submission.
//	 *
//	 *  @param  collectionFile The collection metadata file structure.
//	 *  @param  collectionId   The collection identifier for the collection.
//	 */
//	public void createXml(CollectionMetaDataFile collectionFile,
//			int collectionId) {
//		try {
//			JAXBContext jc = JAXBContext
//					.newInstance("gov.nasa.podaac.distribute.echo.jaxb.collection");
//			JAXBElement<CollectionMetaDataFile> coElement = (new ObjectFactory())
//					.createCollectionMetaDataFile(collectionFile);
//			Marshaller m = jc.createMarshaller();
//			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//			String file = "podaac-echo-collection-" + collectionId + ".xml";
//			m.marshal(coElement, new FileOutputStream(file));
//		} catch (FileNotFoundException fe) {
//			fe.printStackTrace();
//		} catch (JAXBException je) {
//			je.printStackTrace();
//		}
//	}
//
//	/**
//	 *  Convert the date returned from the query into the appropriate format.
//	 */
//	private static XMLGregorianCalendar convertDate(java.util.Date date) {
//		try {
//			if (date == null) {
//				return (null);
//			}
//
//			TimeZone gmt = TimeZone.getTimeZone("GMT");
//			GregorianCalendar cal = new GregorianCalendar(gmt);
//			cal.setTime(date);
//			return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
//		} catch (DatatypeConfigurationException e) {
//			throw new java.lang.Error(e);
//		}
//	}
//
//	/**
//	 *  Get the current date in the appropriate format.
//	 */
//	private static XMLGregorianCalendar currentDate() {
//		try {
//			TimeZone gmt = TimeZone.getTimeZone("GMT");
//			return DatatypeFactory.newInstance().newXMLGregorianCalendar(
//					new GregorianCalendar(gmt));
//		} catch (DatatypeConfigurationException e) {
//			throw new java.lang.Error(e);
//		}
//	}
//
//	/**
//	 *  Cleanup the database connection.
//	 */
//	private void cleanup() throws SQLException {
//		if (conn != null)
//			conn.close();
//	}
//
//	/**
//	 *  Prints the usage statement to stdout.
//	 
//	private static void usage() {
//		System.out
//				.println("CreateCollection host db user password [collection_id]");
//	}
//	 */
//	private static void getProperties() {
//		Properties distributeProps = new Properties();
//		String distributeConfigFilename = System
//				.getProperty("distribute.config.file");
//		if (distributeConfigFilename != null) {
//			//log.info("Using configuration from " + distributeConfigFilename);
//			try {
//				distributeProps.load(new FileInputStream(
//						distributeConfigFilename));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			Enumeration propNames = distributeProps.propertyNames();
//			while (propNames.hasMoreElements()) {
//				String name = propNames.nextElement().toString();
//				System.setProperty(name, distributeProps.getProperty(name));
//			}
//		}
//		distributeProps = new Properties(System.getProperties());
//	}
//	
//	@SuppressWarnings("static-access")
//	private static void processCmdLine(String[] args) {
//		CommandLineParser parser = new BasicParser();
//		Options options = new Options();
//		options.addOption(OptionBuilder.withArgName("collectionId").hasArg()
//				.withDescription("Collection Id").create("c"));
//		options.addOption(OptionBuilder.withArgName( "host" )
//                .hasArg()
//                .withDescription("Host Name" )
//                .create("h"));
//		options.addOption(OptionBuilder.withArgName( "database" )
//                .hasArg()
//                .withDescription("Database Name" )
//                .create("d"));
//		options.addOption(OptionBuilder.withArgName( "username" )
//                .hasArg()
//                .withDescription("User Name" )
//                .create("u"));
//		options.addOption(OptionBuilder.withArgName( "password" )
//                .hasArg()
//                .withDescription("Password" )
//                .create("p"));
//
//		String optName = "";
//		int collectionId = 0;
//		String host = null, db = null, user = null, password = null;
//		try {
//			CommandLine cmd = parser.parse(options, args);
//			Option[] opts = cmd.getOptions();
//			for (Option opt : opts) {
//			    optName = opt.getOpt();
//				if (optName.equals("c")) {
//					collectionId = Integer.parseInt(opt.getValue());
//				} else if (optName.equals("h")) {
//					host = opt.getValue();
//				} else if (optName.equals("d")) {
//					db = opt.getValue();
//				} else if (optName.equals("u")) {
//					user = opt.getValue();
//				} else if (optName.equals("p")) {
//					password = opt.getValue();
//				}
//			}
//		} catch (Exception e) {
//			HelpFormatter formatter = new HelpFormatter();
//			formatter.printHelp("CreateCollection", options);
//			System.exit(0);
//		}
//		try {
//			CreateCollection ec = null;
//			if (host!=null && db!=null && user==null && password==null) {
//				ec = new CreateCollection(host, db, user, password);
//			} else if (host!=null || db!=null || user!=null || password!=null) {
//				HelpFormatter formatter = new HelpFormatter();
//				formatter.printHelp("CreateCollection", options);
//				System.exit(0);
//			} else { // use hibernate configuration properties
//				ec = new CreateCollection();
//			}
//			if (collectionId == 0) {
//				ec.createSubmission();
//			} else {
//				ec.createSubmission(collectionId);
//			}
//			ec.cleanup();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 *  The main method for this class. Supplying a collection identifier at the
//	 *  end of the argument list will generate an ECHO submission for just that
//	 *  collection. Not supplying a collection identifier will generate an ECHO
//	 *  submission for each candidate collection.
//	 *
//	 *  @param args  The arguments for the class.
//	 */
//	public static void main(String[] args) throws SAXException, IOException,
//			ClassNotFoundException, SQLException {
//		getProperties();
//		log = LogFactory.getLog(CreateCollection.class);
//		for (int i = 0; i < args.length; i++)
//			arguments += " " + args[i];
//		processCmdLine(args);
///*
//		if (args.length == 4) {
//			String host = args[0];
//			String db = args[1];
//			String user = args[2];
//			String password = args[3];
//			CreateCollection ec = new CreateCollection(host, db, user, password);
//			ec.createSubmission();
//			ec.cleanup();
//		} else if (args.length == 5) {
//			String host = args[0];
//			String db = args[1];
//			String user = args[2];
//			String password = args[3];
//			int collectionId = Integer.parseInt(args[4]);
//			CreateCollection ec = new CreateCollection(host, db, user, password);
//			ec.createSubmission(collectionId);
//			ec.cleanup();
//		} else {
//			CreateCollection.usage();
//			System.exit(0);
//		}
//*/
//	}
}

//Copyright 2011, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.distribute.gcmd;

import gov.nasa.podaac.distribute.common.DistributeProperty;
import gov.nasa.podaac.distribute.common.DistributeValidationEventHandler;
import gov.nasa.podaac.distribute.common.QueryFactory;
import gov.nasa.podaac.distribute.common.TimeConversion;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.ContactAddress;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.DIF;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.DataCenter;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.DataCenterName;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.DataResolution;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.DataSetCitation;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.IDNNode;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.Location;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.ObjectFactory;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.Parameters;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.Personnel;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.Reference;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.SensorName;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.SourceName;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.SpatialCoverage;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.Summary;
import gov.nasa.podaac.distribute.gcmd.jaxb.granule.TemporalCoverage;

import gov.nasa.podaac.inventory.model.Contact;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetCitation;
import gov.nasa.podaac.inventory.model.DatasetContact;
import gov.nasa.podaac.inventory.model.DatasetCoverage;
import gov.nasa.podaac.inventory.model.DatasetMetaHistory;
import gov.nasa.podaac.inventory.model.DatasetParameter;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.DatasetProject;
import gov.nasa.podaac.inventory.model.DatasetRegion;
import gov.nasa.podaac.inventory.model.DatasetSource;
import gov.nasa.podaac.inventory.model.Project;
import gov.nasa.podaac.inventory.model.Provider;
import gov.nasa.podaac.inventory.model.ProviderResource;
import gov.nasa.podaac.inventory.model.Sensor;
import gov.nasa.podaac.inventory.model.Source;
import gov.nasa.podaac.inventory.model.DatasetSource.DatasetSourcePK;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/**
 * Class to populate GCMD DIF JAXB object with values from Inventory dataset.
 * 
 * @author nchung
 * @version $Id: GCMDDatasetFile.java 9736 2012-03-12 23:42:24Z gangl $
 */
public class GCMDDatasetFile {

	private static Log log = LogFactory.getLog(GCMDDatasetFile.class);
	private static Properties properties = DistributeProperty.getInstance();
	
	private static final String SPATIAL_RESOLUTION_UNIT = "decimal degrees";

	/**
	 * Note that according to GCMD DIF Guide
	 * (http://gcmd.nasa.gov/User/difguide/data_center.html), <Entry_ID>
	 * consists of 1 to 80 alphanumeric characters of the UTF-8 set, and cannot
	 * include Backward Slash ( \), Forward Slash ( / ), Colon ( : ), Spaces ( )
	 */
	private static final String ENTRY_ID_EXCLUSION_CHAR_REGEX = "/|:|\\s|\\\\";

	private static final int MAX_ENTRY_ID_CHAR = 80;

	private static final int MAX_ENTRY_TITLE_CHAR = 220;

	/**
	 * Note that according to GCMD DIF Guide
	 * (http://gcmd.nasa.gov/User/difguide/data_center.html), <Role> is required
	 * and is not repeatable. The only allowable <Role> within <Data_Center> is
	 * "Data Center Contact".
	 */
	private static final String REQUIRED_DATA_CENTER_PERSONNEL_ROLE = "Data Center Contact";

	/**
	 * METADATA_NAME
	 */
	private static final String DEFAULT_METADATA_NAME = "CEOS IDN DIF";

	/**
	 * METADATA_VERSION
	 */
	private static final String DEFAULT_METADATA_VERSION = "9.8";

	/**
	 * DATA_SET_LANGUAGE
	 */
	private static final String DEFAULT_DATA_SET_LANGUAGE = "English";

	/**
	 * ISO_TOPIC_CATEGORY
	 */
	private static final String DEFAULT_ISO_TOPIC_CATEGORY = "Oceans";

	/**
	 * LOCATION_CATEGORY
	 */
	private static final String DEFAULT_LOCATION_CATEGORY = "Geographic Region";
	
	/**
	 * IDN_NODE SHORT_NAME
	 */
	private static final String DEFAULT_IDN_NODE_SHORT_NAME = "USA/NASA";
	
	/**
	 * CONTACT_ADDRESS
	 */
	private static final String DEFAULT_ADDRESS_LINE_1 = "Jet Propulsion Laboratory";
	private static final String DEFAULT_ADDRESS_LINE_2 = "4800 Oak Grove Drive";
	private static final String DEFAULT_CITY = "Pasadena";
	private static final String DEFAULT_PROVINCE_OR_STATE = "CA";
	private static final String DEFAULT_POSTAL_CODE = "91109";
	private static final String DEFAULT_COUNTRY = "USA";
	
	private Dataset dataset;
	private DIF dif;
	private String name;
	private boolean rangeIs360;

	/**
	 * Constructs JAXB DIF object which will be populated with values from
	 * Inventory dataset object.
	 * 
	 * @param dataset
	 *            Inventory dataset object
	 * @param rangeIs360
	 *            if true longitude range is 0 to 360, else -180 to 180
	 */
	public GCMDDatasetFile(Dataset dataset, boolean rangeIs360) {
		this.dataset = dataset;
		this.dif = new DIF();
		// Use persistent_id if exists, otherwise use dataset short_name
		if (this.dataset.getPersistentId() == null) {
			this.name = this.dataset.getShortName().replaceAll(
					ENTRY_ID_EXCLUSION_CHAR_REGEX, "-");
		} else {
			this.name = this.dataset.getPersistentId();
		}
		this.rangeIs360 = rangeIs360;
	}

	/**
	 * Exports Inventory dataset to GCMD DIF JAXB object.
	 */
	public void export() {
		log.info("Processing Dataset [" + this.dataset.getDatasetId() + "]: "
				+ this.dataset.getShortName());

		// SET ALL REQUIRED FIELDS

		// Set Entry_ID
		if (this.name.length() > MAX_ENTRY_ID_CHAR) {
			log.warn("Entry ID has more than " + MAX_ENTRY_ID_CHAR
					+ " characters and GCMD schema only allows "
					+ MAX_ENTRY_ID_CHAR);
		}
		
		this.dif.setEntryID(this.name);

		// Set Entry_Title
		if (this.dataset.getLongName().length() > MAX_ENTRY_TITLE_CHAR) {
			log.warn("Entry Title has more than " + MAX_ENTRY_TITLE_CHAR
					+ " characters and GCMD schema only allows "
					+ MAX_ENTRY_TITLE_CHAR);
		}
		this.dif.setEntryTitle(this.dataset.getLongName());

		// Set Parameters
		exportParameters(this.dataset.getParameterSet());

		// Set ISO_Topic_Category
		this.dif.getISOTopicCategory().add(properties.getProperty("gcmd.iso.topic.category"));
		
		// Set Data_Center
		exportDataCenter(this.dataset.getProvider().getProviderId());

		// Set Summary Abstract
		Summary summary = new Summary();
		JAXBElement<String> abs = (new ObjectFactory())
				.createAbstract(this.dataset.getDescription());
		summary.getContent().add(abs);
		this.dif.setSummary(summary);

		// Set Metadata_Name
		this.dif.setMetadataName(DEFAULT_METADATA_NAME);

		// Set Metadata_Version
		this.dif.setMetadataVersion(DEFAULT_METADATA_VERSION);

		// SET HIGHLY RECOMMENDED FIELDS

		// Set Data_Set_Citation
		exportCitation(this.dataset.getDatasetId());

		// Set Personnel
		exportContactSet(this.dataset.getContactSet());

		// Set Sensor_Name and Source_Name
		exportSensorSource(this.dataset.getSourceSet());

		// Get Coverage
		DatasetCoverage coverage = this.dataset.getDatasetCoverage();

		// Set Temporal_Coverage
		TemporalCoverage difTemporalCoverage = new TemporalCoverage();
		difTemporalCoverage.setStartDate(convertDate(coverage.getStartTime()));
		if (coverage.getStopTime() != null) {
			difTemporalCoverage
					.setStopDate(convertDate(coverage.getStopTime()));
		}
		this.dif.getTemporalCoverage().add(difTemporalCoverage);

		// Set Spatial_Coverage
		exportSpatialCoverage(coverage);

		// Set Location
		Set<DatasetRegion> locations = this.dataset.getRegionSet();
		for (DatasetRegion l : locations) {
			Location difLocation = new Location();
			difLocation.setLocationCategory(properties.getProperty("gcmd.location.category"));
			// TODO Is this officially removed in 2.2.0?
			difLocation.setLocationType(l.getRegion());
			this.dif.getLocation().add(difLocation);
			break;
		}

		// Set Data_Resolution
		exportDataResolution(this.dataset.getLatitudeResolution(), this.dataset
				.getLongitudeResolution(), this.dataset
				.getHorizontalResolutionRange(), this.dataset
				.getTemporalResolution(), this.dataset
				.getTemporalResolutionRange());

		// Set Project
		Set<DatasetProject> projects = this.dataset.getProjectSet();
		for (DatasetProject p : projects) {
			Project project = p.getDatasetProjectPK().getProject();
			gov.nasa.podaac.distribute.gcmd.jaxb.granule.Project difProject = new gov.nasa.podaac.distribute.gcmd.jaxb.granule.Project();
			difProject.setShortName(project.getProjectShortName());
			difProject.setLongName(project.getProjectLongName());
			this.dif.getProject().add(difProject);
		}

		DatasetPolicy policy = this.dataset.getDatasetPolicy();
		// Set Access_Constraint
		if (isNotNull(policy.getAccessConstraint()))
			this.dif.setAccessConstraints(policy.getAccessConstraint());

		// Set Use_Constraint
		if (isNotNull(policy.getUseConstraint()))
			this.dif.setUseConstraints(policy.getUseConstraint());

		// Set Data_Set_Language
		this.dif.getDataSetLanguage().add(DEFAULT_DATA_SET_LANGUAGE);

		// Set DIF_Revision_History and Last_DIF_Revision_Date
		exportMetadataHistory(this.dataset.getMetaHistorySet());

		// SET OPTIONAL FIELDS

		// Set Originating_Center
		this.dif.setOriginatingCenter(this.dataset.getOriginalProvider());

		// Set Reference
		if (isNotNull(this.dataset.getReference())) {
			Reference ref = new Reference();
			ref.getContent().add(this.dataset.getReference());
			this.dif.getReference().add(ref);
		}
		
		// Set IDN_Node
		IDNNode idnNode = new IDNNode();
		idnNode.setShortName(properties.getProperty("gcmd.idn.node.short.name"));
		this.dif.getIDNNode().add(idnNode);

		// Set DIF_Creation_Date
		this.dif.setDIFCreationDate(currentDate());
	}
	
	private void exportDataResolution(final Double latResolution,
			final Double lonResolution, final String horResolutionRange,
			final String temporalResolution, final String temporlResolutionRange) {
		if (latResolution != null || lonResolution != null
				|| isNotNull(horResolutionRange)
				|| isNotNull(temporalResolution)
				|| isNotNull(temporlResolutionRange)) {
			DataResolution difResolution = new DataResolution();
			if (latResolution != null)
				difResolution.setLatitudeResolution(latResolution + " "
						+ SPATIAL_RESOLUTION_UNIT);
			if (lonResolution != null)
				difResolution.setLongitudeResolution(lonResolution + " "
						+ SPATIAL_RESOLUTION_UNIT);
			if (isNotNull(horResolutionRange))
				difResolution.setHorizontalResolutionRange(horResolutionRange);
			if (isNotNull(temporalResolution))
				difResolution.setTemporalResolution(temporalResolution);
			if (isNotNull(temporlResolutionRange))
				difResolution
						.setTemporalResolutionRange(temporlResolutionRange);
			this.dif.getDataResolution().add(difResolution);
		}
	}

	private void exportCitation(final int datasetId) {
		Dataset ds = QueryFactory.getInstance().createQuery().fetchDatasetById(datasetId, "citationSet");
		Set<DatasetCitation> citations = ds.getCitationSet();
		for (DatasetCitation c : citations) {
			DataSetCitation difCitation = new DataSetCitation();
			difCitation.setDatasetCreator(c.getCreator());
			difCitation.setDatasetTitle(c.getTitle());
			difCitation.setDatasetSeriesName(c.getSeriesName());
			difCitation.setDatasetReleaseDate(convertDate(c.getReleaseDate()));
			difCitation.setDatasetReleasePlace(c.getReleasePlace());
			difCitation.setDatasetPublisher(c.getPublisher());
			difCitation.setVersion(c.getVersion());
			difCitation.setOtherCitationDetails(c.getCitationDetail());
			difCitation.setOnlineResource(c.getOnlineResource());
			this.dif.getDataSetCitation().add(difCitation);
		}
	}

	private Personnel exportPersonnel(final Contact contact, final String role) {
		Personnel difContact = new Personnel();
		if (role != null) {
			difContact.getRole().add(role);
		} else {
			difContact.getRole().add(contact.getRole());
		}
		log.debug("fn: "+contact.getFirstName());
		difContact.setFirstName(contact.getFirstName());
		if (isNotNull(contact.getMiddleName()))
			difContact.setMiddleName(contact.getMiddleName());
		difContact.setLastName(contact.getLastName());
		difContact.getEmail().add(contact.getEmail());
		difContact.getPhone().add(contact.getPhone());
		difContact.getFax().add(contact.getFax());

		// TODO retrieve address from inventory
		ContactAddress address = new ContactAddress();
		address.getAddress().add(properties.getProperty("gcmd.contact.address.line.1"));
		address.getAddress().add(properties.getProperty("gcmd.contact.address.line.2"));
		address.setCity(properties.getProperty("gcmd.contact.city"));
		address.setProvinceOrState(properties.getProperty("gcmd.contact.province.or.state"));
		address.setPostalCode(properties.getProperty("gcmd.contact.postal.code"));
		address.setCountry(properties.getProperty("gcmd.contact.country"));
		difContact.setContactAddress(address);

		return difContact;
	}

	private void exportContactSet(final Set<DatasetContact> contacts) {
		for (DatasetContact c : contacts) {
			log.debug("Marshalling DatasetContact...");
			Contact contact = c.getDatasetContactPK().getContact();
			this.dif.getPersonnel().add(exportPersonnel(contact, null));
		}
	}

	private void exportParameters(final Set<DatasetParameter> parameters) {
		for (DatasetParameter p : parameters) {
			Parameters difParameter = new Parameters();
			difParameter.setCategory(p.getCategory());
			difParameter.setTopic(p.getTopic());
			difParameter.setTerm(p.getTerm());
			difParameter.setVariableLevel1(p.getVariable());
			if (isNotNull(p.getVariableDetail())) {
				difParameter.setDetailedVariable(p.getVariableDetail());
			}
			this.dif.getParameters().add(difParameter);
		}
	}

	private void exportDataCenter(final int providerId) {
		Provider provider = QueryFactory.getInstance().createQuery()
				.fetchProviderById(providerId);

		
		log.debug("number of provider contacts: "+provider.getContactSet().size());
		DataCenterName difDataCenterName = new DataCenterName();
		difDataCenterName.setShortName(provider.getShortName());
		difDataCenterName.setLongName(provider.getLongName());

		DataCenter difDataCenter = new DataCenter();
		difDataCenter.setDataCenterName(difDataCenterName);

		for (Contact contact : provider.getContactSet()) {
			log.debug("Contact info");
			difDataCenter.getPersonnel().add(
					exportPersonnel(contact,
							REQUIRED_DATA_CENTER_PERSONNEL_ROLE));
		}

		Set<ProviderResource> resource = provider.getProviderResourceSet();
		for (ProviderResource r : resource) {
			difDataCenter.setDataCenterURL(r.getResourcePath());
			break;
		}
		this.dif.getDataCenter().add(difDataCenter);
	}

	private void exportSensorSource(final Set<DatasetSource> sources) {
		Set<String> addedSensors = new HashSet<String>();
		Set<String> addedSources = new HashSet<String>();

		for (DatasetSource s : sources) {
			DatasetSourcePK pk = s.getDatasetSourcePK();

			// Ensure sensor is unique
			Sensor sensor = pk.getSensor();
			if (!addedSensors.contains(sensor.getSensorShortName())) {
				addedSensors.add(sensor.getSensorShortName());
				SensorName difSensor = new SensorName();
				difSensor.setShortName(sensor.getSensorShortName());
				difSensor.setLongName(sensor.getSensorLongName());

				this.dif.getSensorName().add(difSensor);
			}

			// Ensure source is unique
			Source source = pk.getSource();
			if (!addedSources.contains(source.getSourceShortName())) {
				addedSources.add(source.getSourceShortName());
				SourceName difSource = new SourceName();
				difSource.setShortName(source.getSourceShortName());
				difSource.setLongName(source.getSourceLongName());

				this.dif.getSourceName().add(difSource);
			}
		}
	}

	private void exportSpatialCoverage(final DatasetCoverage coverage) {
		/*
		 * Note: Don't separate bounding box if crosses date line. As specified
		 * by GCMD, "If data coverage extends over the International Date Line
		 * (180 Longitude), the <Easternmost_Longitude> of the data will
		 * likely be in the Western Hemisphere, and the <Westernmost_Longitude>
		 * will likely be in the Eastern Hemisphere."
		 */
		if (coverage.getEastLon() == null || coverage.getWestLon() == null || coverage.getNorthLat() == null || coverage.getSouthLat() == null) {
			log.info("Unable to retrieve spatial coverage. Latitude/Longitude is null.");
		} else {
			SpatialCoverage difSpatialCoverage = new SpatialCoverage();
			if (this.rangeIs360) {
				difSpatialCoverage.setWesternmostLongitude(convertLon(
						coverage.getWestLon()).toString());
				difSpatialCoverage.setEasternmostLongitude(convertLon(
						coverage.getEastLon()).toString());
			} else {
				difSpatialCoverage.setWesternmostLongitude(coverage
						.getWestLon().toString());
				difSpatialCoverage.setEasternmostLongitude(coverage
						.getEastLon().toString());
			}
			difSpatialCoverage.setNorthernmostLatitude(coverage.getNorthLat()
					.toString());
			difSpatialCoverage.setSouthernmostLatitude(coverage.getSouthLat()
					.toString());
			setAltitudeDepth(coverage, difSpatialCoverage);
			this.dif.getSpatialCoverage().add(difSpatialCoverage);
		}
	}

	private void setAltitudeDepth(final DatasetCoverage coverage,
			final SpatialCoverage difSpatialCoverage) {
		if (coverage.getMinAltitude() != null)
			difSpatialCoverage.setMinimumAltitude(coverage.getMinAltitude()
					.toString());
		if (coverage.getMaxAltitude() != null)
			difSpatialCoverage.setMaximumAltitude(coverage.getMaxAltitude()
					.toString());
		if (coverage.getMinDepth() != null)
			difSpatialCoverage.setMinimumDepth(coverage.getMinDepth()
					.toString());
		if (coverage.getMaxDepth() != null)
			difSpatialCoverage.setMaximumDepth(coverage.getMaxDepth()
					.toString());
	}

	private void exportMetadataHistory(final Set<DatasetMetaHistory> historySet) {
		for (DatasetMetaHistory history : historySet) {
			this.dif.setDIFRevisionHistory(history.getRevisionHistory());
			this.dif.setLastDIFRevisionDate(convertDate(history
					.getLastRevisionDate()));
			break;
		}
	}

	private Double convertLon(final Double val) {
		return val - 180;
	}

	private String convertDate(final Date date) {
		if (date == null) {
			return null;
		}
		String dateStr = TimeConversion.convertDate(date).toString();
		return dateStr.substring(0, dateStr.indexOf('T'));
	}

	private String currentDate() {
		String dateStr = TimeConversion.currentDate().toString();
		return dateStr.substring(0, dateStr.indexOf('T'));
	}

	private boolean isNotNull(final String value) {
		return value != null && !value.equalsIgnoreCase("null")
				&& !value.equalsIgnoreCase("none");
	}

	/**
	 * Create the XML representation of the GCMD DIF for this dataset.
	 * 
	 * @param directory
	 *            directory to save output XML file
	 * @return full path filename of the output XML file
	 */
	public String toXmlFile(final String directory) {
		String filename = directory + File.separator + this.name + ".xml";
		File file = new File(filename);
		try {
			file.createNewFile();
			if (!file.exists()) {
				filename = directory + File.separator + "dataset-"
						+ this.dataset.getDatasetId() + ".xml";
			}
		} catch (IOException e) {
			filename = directory + File.separator + "dataset-"
					+ this.dataset.getDatasetId() + ".xml";
		}
		try {
			JAXBContext jc = JAXBContext
					.newInstance("gov.nasa.podaac.distribute.gcmd.jaxb.granule");
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(this.dif, new FileOutputStream(filename));
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (JAXBException je) {
			je.printStackTrace();
		}
		return filename;
	}

	/**
	 * Validates specified file against GCMD DIF schema.
	 * 
	 * @param filename
	 *            filename of GCMD DIF XML file to validate
	 * @return true if file is valid GCMD DIF XML, false otherwise
	 */
	public boolean validateXML(final String filename) {
		JAXBContext context;
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema schema = schemaFactory.newSchema(new File(System
					.getProperty("distribute.schemas")
					+ File.separator + "dif_v9.8.2.xsd"));
			context = JAXBContext
					.newInstance("gov.nasa.podaac.distribute.gcmd.jaxb.granule");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setSchema(schema);
			unmarshaller
					.setEventHandler(new DistributeValidationEventHandler());
			unmarshaller.unmarshal(new File(filename));
			return true;
		} catch (SAXException e) {
			log.error("Unable to parse schema:\n" + e.getMessage());
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}

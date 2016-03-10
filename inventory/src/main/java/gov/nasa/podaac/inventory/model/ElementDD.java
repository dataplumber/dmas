//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author clwong
 * @version Jul 27, 2007
 * $Id: ElementDD.java 887 2008-04-02 21:42:54Z clwong $
 */

public class ElementDD implements Serializable {

	private Integer elementId;
	
	private Integer maxLength;
	private String scope;
	
	private String shortName;
	private String longName;
	private String type;
	private String dbTable;
	private String description;
	private Set<GranuleElement> granuleElementSet = new HashSet<GranuleElement>();
	private Set<DatasetElement> datasetElementSet = new HashSet<DatasetElement>();

    /**
	 * 
	 */
	public ElementDD() {}
	
	/**
	 * @param shortName
	 * @param longName
	 * @param type
	 * @param dbTable
	 * @param description
	 */
	public ElementDD(String shortName, String longName, String type, String dbTable, String description) {
		this.shortName = shortName;
		this.longName = longName;
		this.type = type;
		this.dbTable = dbTable;
		this.description = description;
	}

	public Integer getElementId() {
		return elementId;
	}

	public void setElementId(Integer elementId) {
		this.elementId = elementId;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDbTable() {
		return dbTable;
	}

	public void setDbTable(String dbTable) {
		this.dbTable = dbTable;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLenght) {
		this.maxLength = maxLenght;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
	
	public Set<GranuleElement> getGranuleElementSet() {
		return granuleElementSet;
	}

	public void setGranuleElementSet(Set<GranuleElement> granuleElementSet) {
		this.granuleElementSet = granuleElementSet;
	}
	
	public Set<DatasetElement> getDatasetElementSet() {
		return datasetElementSet;
	}

	public void setDatasetElementSet(Set<DatasetElement> datasetElementSet) {
		this.datasetElementSet = datasetElementSet;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((elementId == null) ? 0 : elementId.hashCode());
		result = prime * result
				+ ((shortName == null) ? 0 : shortName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ElementDD other = (ElementDD) obj;
		if (elementId == null) {
			if (other.elementId != null)
				return false;
		} else if (!elementId.equals(other.elementId))
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		return true;
	}

	@Override
	public String toString() {
        return  "ElementId ('" + getElementId() + "'), " +
                "ShortName: '" + getShortName() + "' " +
                "LongName: '" + getLongName()+ "'" +
                "Type: '" + getType()+ "'" +
                "Table: '" + getDbTable()+ "'"+
                "Description: '" + getDescription()+ "'";
    }
}

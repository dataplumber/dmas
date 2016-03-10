//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.inventory.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
* @author clwong
* @version Sep 7, 2007
* $Id: Project.java 2293 2008-11-15 07:11:52Z clwong $
*/
@SuppressWarnings("serial")
public class Project implements Serializable {

	private Integer projectId;
	private String projectShortName;
	private String projectLongName;
	private Set<DatasetProject> datasetProjectSet = new HashSet<DatasetProject>();
	public Integer getProjectId() {
		return projectId;
	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	public String getProjectShortName() {
		return projectShortName;
	}
	public void setProjectShortName(String projectShortName) {
		this.projectShortName = projectShortName;
	}
	public String getProjectLongName() {
		return projectLongName;
	}
	public void setProjectLongName(String projectLongName) {
		this.projectLongName = projectLongName;
	}
	public Set<DatasetProject> getDatasetProjectSet() {
		return datasetProjectSet;
	}
	public void setDatasetProjectSet(Set<DatasetProject> datasetProjectSet) {
		this.datasetProjectSet = datasetProjectSet;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((projectId == null) ? 0 : projectId.hashCode());
		result = prime
				* result
				+ ((projectShortName == null) ? 0 : projectShortName.hashCode());
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
		final Project other = (Project) obj;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		if (projectShortName == null) {
			if (other.projectShortName != null)
				return false;
		} else if (!projectShortName.equals(other.projectShortName))
			return false;
		return true;
	}

}


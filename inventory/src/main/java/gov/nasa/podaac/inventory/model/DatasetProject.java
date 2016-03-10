//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//Copyright 2007, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;
/**
 * @author clwong
 * $Id: DatasetProject.java 8045 2011-06-24 22:14:13Z gangl $
 */
@SuppressWarnings("serial")
public class DatasetProject implements Serializable {

	
	
	public static class DatasetProjectPK implements Serializable{
		private Dataset dataset;
		private Project project;
		public Dataset getDataset() {
			return dataset;
		}
		public void setDataset(Dataset dataset) {
			this.dataset = dataset;
		}
		public Project getProject() {
			return project;
		}
		public void setProject(Project project) {
			this.project = project;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((dataset == null) ? 0 : dataset.hashCode());
			result = prime * result
					+ ((project == null) ? 0 : project.hashCode());
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
			final DatasetProjectPK other = (DatasetProjectPK) obj;
			if (dataset == null) {
				if (other.dataset != null){
				
					return false;
				}
					
			} else if (!dataset.equals(other.dataset)){
				
				return false;
			}
			if (project == null) {
				if (other.project != null){
				
					return false;
				}
			} else if (!project.equals(other.project)){
				
				return false;
			}
			return true;
		}
	}
	
	private DatasetProjectPK datasetProjectPK = new DatasetProjectPK();

	public DatasetProjectPK getDatasetProjectPK() {
		return datasetProjectPK;
	}

	public void setDatasetProjectPK(DatasetProjectPK datasetProjectPK) {
		this.datasetProjectPK = datasetProjectPK;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datasetProjectPK == null) ? 0 : datasetProjectPK.hashCode());
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
		final DatasetProject other = (DatasetProject) obj;
		if (datasetProjectPK == null) {
			if (other.datasetProjectPK != null)
				return false;
		} else if (!datasetProjectPK.equals(other.datasetProjectPK))
			return false;
		return true;
	}

}


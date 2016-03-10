//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;

/**
 * @author clwong
 * @version Sep 8, 2007
 * $Id: GranuleElement.java 4652 2010-03-18 15:44:31Z gangl $
 */
@SuppressWarnings("serial")
public class GranuleElement implements Serializable {
	
//	public static class GranuleElementPK implements Serializable{
//		private Dataset dataset;
//		private ElementDD elementDD;
//		public Dataset getDataset() {
//			return dataset;
//		}
//		public void setDataset(Dataset dataset) {
//			this.dataset = dataset;
//		}
//		public ElementDD getElementDD() {
//			return elementDD;
//		}
//		public void setElementDD(ElementDD elementDD) {
//			this.elementDD = elementDD;
//		}
//		@Override
//		public int hashCode() {
//			final int prime = 31;
//			int result = 1;
//			result = prime * result
//					+ ((dataset == null) ? 0 : dataset.hashCode());
//			result = prime * result
//					+ ((elementDD == null) ? 0 : elementDD.hashCode());
//			return result;
//		}
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (getClass() != obj.getClass())
//				return false;
//			final GranuleElementPK other = (GranuleElementPK) obj;
//			if (dataset == null) {
//				if (other.dataset != null)
//					return false;
//			} else if (!dataset.equals(other.dataset))
//				return false;
//			if (elementDD == null) {
//				if (other.elementDD != null)
//					return false;
//			} else if (!elementDD.equals(other.elementDD))
//				return false;
//			return true;
//		}
//	}
	
	//private GranuleElementPK granuleElementPK;
	private Character obligationFlag;
	private Dataset dataset;
	private ElementDD elementDD;
	public Dataset getDataset() {
		return dataset;
	}
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	public ElementDD getElementDD() {
		return elementDD;
	}
	public void setElementDD(ElementDD elementDD) {
		this.elementDD = elementDD;
	}
	//private ElementDD granuleElementDD;
	//private Dataset dataset;
//	public GranuleElementPK getGranuleElementPK() {
//		return granuleElementPK;
//	}
//	public void setGranuleElementPK(GranuleElementPK granuleElementPK) {
//		this.granuleElementPK = granuleElementPK;
//	}
	public Character getObligationFlag() {
		return obligationFlag;
	}
	public void setObligationFlag(Character obligationFlag) {
		this.obligationFlag = obligationFlag;
	}
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime
//				* result
//				+ ((granuleElementPK == null) ? 0 : granuleElementPK.hashCode());
//		return result;
//	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
//		final GranuleElement other = (GranuleElement) obj;
//		if (granuleElementPK == null) {
//			if (other.granuleElementPK != null)
//				return false;
//		} else if (!granuleElementPK.equals(other.granuleElementPK))
//			return false;
		return true;
	}
}

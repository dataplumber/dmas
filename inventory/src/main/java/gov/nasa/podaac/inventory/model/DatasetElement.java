//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.model;

import java.io.Serializable;

/**
 * @author clwong
 * @version Sep 8, 2007
 * $Id: DatasetElement.java 1827 2008-09-15 16:24:16Z clwong $
 */
@SuppressWarnings("serial")
public class DatasetElement implements Serializable {
	
	private Integer	deId;	
	private Character obligationFlag;
	private String scope;
	private Dataset dataset;
	private ElementDD elementDD;
	
	public Integer getDeId()
	{
		return this.deId;
	}
	public void setDeId(Integer i){
		this.deId=i;
	}
	
	public Dataset getDataset() {
		return dataset;
	}
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	public ElementDD getElementDD() {
		if(elementDD == null)
			{
			elementDD = new ElementDD();
			}
		return elementDD;
	}
	public void setElementDD(ElementDD elementDD) {
		this.elementDD = elementDD;
	}
	public Character getObligationFlag() {
		return obligationFlag;
	}
	public void setObligationFlag(Character obligationFlag) {
		this.obligationFlag = obligationFlag;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.getDeId() == null) ? 0 : this.getDeId().hashCode());
		
		//System.out.println("hashcode: " + result);
		
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			{
				//System.out.println("==ob");
				return true;
			}
		if (obj == null)
			{
				//System.out.println("==null");
				return false;
			}
//		if (getClass() != obj.getClass())
//			{
//			System.out.println(getClass().toString()+":"+obj.getClass().toString());
//			return false;
//			}
		final DatasetElement other;
		try{
			other = (DatasetElement) obj;
		}catch(ClassCastException cce){
			return false;
		}
		if (this.getDeId() == null || other.getDeId() == null) {
			if (other.getDeId() != null){
				return false; 
			}
			else if(other.getElementDD().getElementId().equals(this.getElementDD().getElementId())){
					return true;
				}
			else
				{
					return false;
				}
		} else if (!this.getDeId().equals(other.getDeId())){
			return false;
		}
		return true;
	}
}

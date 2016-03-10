package gov.nasa.podaac.common.api.reader.api;

public class DataValue {

	Object m_val = null;
	Class m_class = null;
	public DataValue(Object o, Class c){
		m_val = o;
		m_class = c;
		
		//System.out.println(o);
	}
	
	public DataValue() {
		//null Data Value
	}
	
	public Long getValueLong(){
		if(m_val == null)
			return null;
		if(m_class == Integer.class)
			return new Long((Integer)m_val);
		if(m_class == Float.class)
			return ((Float)m_val).longValue();
		return (Long)m_val;
	}
	
	public Double getValueDouble(){
		if(m_val == null)
			return null;
		return (Double)m_val;
	}
	
	public Float getValueFloat(){
		
//		if(m_class != null)
//			System.out.println(m_class.getName());
		if(m_val == null)
			return null;
		
		if(m_class == Integer.class)
			return new Float((Integer)m_val);
		
		if(m_class == Short.class)
			return new Float((Short)m_val);
		
		if(m_class == Double.class)
			return new Float((Double)m_val);
		
		else if(m_class == Long.class)
			return new Float((Long)m_val);
		
		return (Float)m_val;
	}
	
	public Integer getValueInt(){
		if(m_val == null)
			return null;
		return (Integer)m_val;
	}

	public Object getValueByte() {
		if(m_val == null)
			return null;
		return (Byte)m_val;
	}
}

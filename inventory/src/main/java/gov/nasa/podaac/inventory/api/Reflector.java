package gov.nasa.podaac.inventory.api;

import java.lang.reflect.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Reflector {
	
	private static Log log = LogFactory.getLog(Reflector.class);

	public static Object getVal(Object o, String method){
		
		log.debug("Fecthing method " + method);
		if(o == null)
			return null;
		
		Method[] ms = o.getClass().getMethods();
		
		for(Method m: ms){
			Class retClazz = m.getReturnType();
			if(m.getName().equals(method)){
				log.debug("invoking method: " + m.getName() + ", return type: " + retClazz.getName());
				try {
					return m.invoke(o);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static String getify(String field){
		if(field == null)
			return null;
		if(field.length() < 2 )
			return "get" + field.toUpperCase();
		return "get"+ field.substring(0,1).toUpperCase() + field.substring(1);
	}
	
	
}

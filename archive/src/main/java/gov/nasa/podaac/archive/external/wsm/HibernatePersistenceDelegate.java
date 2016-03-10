package gov.nasa.podaac.archive.external.wsm;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.XMLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.hibernate.SessionException;
import org.hibernate.collection.PersistentBag;
import org.hibernate.collection.PersistentIdentifierBag;
import org.hibernate.collection.PersistentList;
import org.hibernate.collection.PersistentMap;
import org.hibernate.collection.PersistentSet;
import org.hibernate.collection.PersistentSortedMap;
import org.hibernate.collection.PersistentSortedSet;

public class HibernatePersistenceDelegate extends DefaultPersistenceDelegate
{
    private static HibernatePersistenceDelegate  self = null;
    static synchronized HibernatePersistenceDelegate getInstance()
    {
        if (HibernatePersistenceDelegate.self == null) HibernatePersistenceDelegate.self = new HibernatePersistenceDelegate();
        return HibernatePersistenceDelegate.self;
    }
    
    final private Map<Class<?>, Class<?>> CLASS_MAP;

    private HibernatePersistenceDelegate()
    {
        HashMap<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
        map.put(PersistentBag.class, ArrayList.class);
        map.put(PersistentIdentifierBag.class, ArrayList.class);
        map.put(PersistentList.class, ArrayList.class);
        map.put(PersistentMap.class, HashMap.class);
        map.put(PersistentSet.class, HashSet.class);
        map.put(PersistentSortedMap.class, TreeMap.class);
        map.put(PersistentSortedSet.class, TreeSet.class);
        this.CLASS_MAP = Collections.synchronizedMap(map);
    }

    void subscribe (XMLEncoder xmle)
    {
        for (Class<?> c : this.CLASS_MAP.keySet()) xmle.setPersistenceDelegate (c, HibernatePersistenceDelegate.self);
    }

    @Override
    protected Expression instantiate (Object oldInstance, Encoder out)
    {
        Class<?> replaceType = (Class<?>) CLASS_MAP.get(oldInstance.getClass());
        
        if (replaceType != null) { return new Expression(oldInstance, replaceType, "new", new Object[0]); }
        
        return super.instantiate (oldInstance, out);
    }

    @Override
    protected boolean mutatesTo (Object oldInstance, Object newInstance)
    {
        if (oldInstance != null && newInstance != null && CLASS_MAP.get (oldInstance.getClass()) == newInstance.getClass()) return true;
        
        return super.mutatesTo (oldInstance, newInstance);
    }

    @Override
    protected void initialize (Class<?> type, Object oldInstance, Object newInstance, Encoder out)
    {
        Class<?> replaceType = (Class<?>) CLASS_MAP.get (oldInstance.getClass());
        
        if (replaceType != null)
        {
            Object replaced = this.replace (oldInstance);
            
            if (type.equals (oldInstance.getClass())) type = replaced.getClass();
            super.initialize(type, replaced, newInstance, out);
        }
        else super.initialize(type, oldInstance, newInstance, out);
    }

    @SuppressWarnings("unchecked")
    private Object replace (Object obj)
    {
        Object result;
        
        if (obj instanceof PersistentSet)
        {
            try { result = new HashSet((Set)obj); }
            catch (SessionException se) { result = new HashSet(); }
        }
        else if (obj instanceof PersistentMap)
        {
            try { result = new HashMap((Map)obj); }
            catch (SessionException se) { result = new HashMap(); }
        }
        else if (obj instanceof PersistentSortedSet)
        {
            try { result = new TreeSet((SortedSet) obj); }
            catch (SessionException se) { result = new TreeSet(); }
        }
        else if (obj instanceof PersistentSortedMap)
        {
            try { result = new TreeMap((SortedMap) obj); }
            catch (SessionException se) { result = new TreeMap(); }
        }
        else if (obj instanceof PersistentList || obj instanceof PersistentBag || obj instanceof PersistentIdentifierBag)
        {
            try {  result = new ArrayList((Collection) obj); }
            catch (SessionException se) { result = new ArrayList(); }
        }
        else throw new RuntimeException("Unknown hibernate type: " + obj.getClass());
        
        return result;
    }
}

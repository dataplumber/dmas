/*
 * Created on Aug 26, 2010
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gov.nasa.podaac.archive.external.wsm;

import gov.nasa.podaac.archive.xml.Packet;
import gov.nasa.podaac.archive.xml.Packet.DatasetList;
import gov.nasa.podaac.archive.xml.Packet.GranuleList;
import gov.nasa.podaac.archive.xml.Packet.GranuleReferencetList;
import gov.nasa.podaac.archive.xml.Packet.IdList;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleReference;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

public class Utilities
{
    private static Utilities self = null;
    static synchronized Utilities getInstance () throws JAXBException, SAXException, URISyntaxException
    {
        if (Utilities.self == null) Utilities.self = new Utilities();

        return Utilities.self;
    }

    private final JAXBContext   jc;
    private final Marshaller    m;
    private final SchemaFactory sf;
    private final Unmarshaller  u;

    private Utilities() throws JAXBException, SAXException, URISyntaxException
    {
        this.jc = JAXBContext.newInstance("gov.nasa.podaac.archive.xml");
        this.m = jc.createMarshaller();
        this.sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        this.u = jc.createUnmarshaller();

        this.u.setSchema(this.sf.newSchema(new StreamSource(Utilities.class.getResource("/wsm.xsd").toURI().toString())));
        this.m.setSchema(this.sf.newSchema(new StreamSource(Utilities.class.getResource("/wsm.xsd").toURI().toString())));
    }

    public void add (Packet p, Dataset ds)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLEncoder xmle = new XMLEncoder(baos);

        HibernatePersistenceDelegate.getInstance().subscribe(xmle);
        xmle.writeObject(ds);
        xmle.close();
        synchronized (p)
        {
            if (p.getDatasetList() == null) p.setDatasetList(new DatasetList());
            p.getDatasetList().getEncodedDataset().add(new String(baos.toByteArray()));
        }
    }

    public void add (Packet p, Granule g)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLEncoder xmle = new XMLEncoder(baos);

        HibernatePersistenceDelegate.getInstance().subscribe(xmle);
        xmle.writeObject(g);
        xmle.close();
        synchronized (p)
        {
            if (p.getGranuleList() == null) p.setGranuleList(new GranuleList());
            p.getGranuleList().getEncodedGranule().add(new String(baos.toByteArray()));
        }
    }

    public void add (Packet p, GranuleReference gr)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLEncoder xmle = new XMLEncoder(baos);

        xmle.setPersistenceDelegate(gov.nasa.podaac.archive.external.wsm.HibernatePersistenceDelegate.class, HibernatePersistenceDelegate.getInstance());
        //HibernatePersistenceDelegate.getInstance().subscribe(xmle);
        xmle.writeObject(gr);
        xmle.close();
        synchronized (p)
        {
            if (p.getGranuleReferencetList() == null) p.setGranuleReferencetList(new GranuleReferencetList());
            p.getGranuleReferencetList().getEncodedGranuleRef().add(new String(baos.toByteArray()));
        }

    }

    public void add (Packet p, Integer id)
    {
        List<Integer> ids = new ArrayList<Integer>(1);

        ids.add(id);
        this.add(p, ids);
    }

    public void add (Packet p, List<Integer> ids)
    {
        synchronized (p)
        {
            if (p.getIdList() == null) p.setIdList(new IdList());
            p.getIdList().getIds().addAll(ids);
        }
    }

    public void addDatasets (Packet p, Collection<Dataset> dss)
    {
        for (Dataset ds : dss)
            this.add(p, ds);
    }

    public void addGranuleRefs (Packet p, Collection<GranuleReference> grs)
    {
        for (GranuleReference gr : grs)
            this.add(p, gr);
    }

    public void addGranules (Packet p, Collection<Granule> gs)
    {
        for (Granule g : gs)
            this.add(p, g);
    }

    public List<Dataset> extractDataset (Packet p)
    {
        ByteArrayInputStream bais;
        List<Dataset> result = new ArrayList<Dataset>(p.getDatasetList().getEncodedDataset().size());
        XMLDecoder xmld;

        for (String eds : p.getDatasetList().getEncodedDataset())
        {
            bais = new ByteArrayInputStream(eds.getBytes());
            xmld = new XMLDecoder(bais);
            result.add((Dataset) xmld.readObject());
        }
        return result;
    }

    public List<Granule> extractGranule (Packet p)
    {
        ByteArrayInputStream bais;
        List<Granule> result = new ArrayList<Granule>(p.getGranuleList().getEncodedGranule().size());
        XMLDecoder xmld;

        for (String eds : p.getGranuleList().getEncodedGranule())
        {
            bais = new ByteArrayInputStream(eds.getBytes());
            xmld = new XMLDecoder(bais);
            result.add((Granule) xmld.readObject());
        }
        return result;
    }

    public List<GranuleReference> extractGranuleRef (Packet p)
    {
        ByteArrayInputStream bais;
        List<GranuleReference> result = new ArrayList<GranuleReference>(p.getGranuleReferencetList().getEncodedGranuleRef().size());
        XMLDecoder xmld;

        for (String eds : p.getGranuleList().getEncodedGranule())
        {
            bais = new ByteArrayInputStream(eds.getBytes());
            xmld = new XMLDecoder(bais);
            result.add((GranuleReference) xmld.readObject());
        }
        return result;
    }

    public Packet read (InputStream is) throws IOException, JAXBException
    {
        Packet result = (Packet) u.unmarshal(is);
        return result;
    }

    public void send (Packet p, OutputStream os) throws IOException, JAXBException
    {
        m.marshal(p, os);
        os.flush();
    }
}

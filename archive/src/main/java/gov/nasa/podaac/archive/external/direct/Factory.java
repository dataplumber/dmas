package gov.nasa.podaac.archive.external.direct;

import gov.nasa.podaac.archive.external.InventoryAccess;
import gov.nasa.podaac.archive.external.InventoryQuery;
import gov.nasa.podaac.archive.external.InventoryFactory;

public class Factory extends InventoryFactory
{
    final private InventoryAccess access = new Access();
    final private InventoryQuery query = new Query();
    
    @Override
    public InventoryAccess getAccess ()
    {
        return this.access;
    }

    @Override
    public InventoryQuery getQuery ()
    {
        return this.query;
    }
}

/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: Ellipse.java 2379 2008-12-10 00:19:57Z thuang $
 */
public interface Ellipse extends Accessor {

   Point getCenterPoint();

   String getUnit();

   Double getValue();

   void setCenterPoint(Point centerPoint);

   void setUnit(String unit);

   void setValue(double value);
}

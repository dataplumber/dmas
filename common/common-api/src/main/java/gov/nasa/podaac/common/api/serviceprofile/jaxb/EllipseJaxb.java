/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.EllipseType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.PointType;
import gov.nasa.podaac.common.api.serviceprofile.Ellipse;
import gov.nasa.podaac.common.api.serviceprofile.Point;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.RadiusType;

/**
 * Implementation of Ellipse object using JAXB for XML marshalling and
 * unmarshalling.
 * 
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: EllipseJaxb.java 4425 2009-12-24 00:38:36Z thuang $
 */
public class EllipseJaxb extends AccessorBase implements Ellipse {

   private EllipseType _jaxbObj = null;

   public EllipseJaxb() {
      this._jaxbObj = new EllipseType();
   }

   public EllipseJaxb(double centerLatitude, double centerLongitude,
         double radius, String radiusUnit) {
      this._jaxbObj = new EllipseType();
      this.setCenterPoint(new PointJaxb(centerLatitude, centerLongitude));
      this.setUnit(radiusUnit);
      this.setValue(radius);
   }

   public EllipseJaxb(Ellipse ellipse) {
      if (ellipse.getImplObj() instanceof EllipseType) {
         this._jaxbObj = (EllipseType) ellipse.getImplObj();
      } else {
         this._jaxbObj = new EllipseType();
         this.setCenterPoint(ellipse.getCenterPoint());
         this.setUnit(ellipse.getUnit());
         this.setValue(ellipse.getValue());
      }
   }

   public EllipseJaxb(EllipseType jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final EllipseJaxb other = (EllipseJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public synchronized Point getCenterPoint() {
      return new PointJaxb(this._jaxbObj.getCenter());
   }

   public synchronized Object getImplObj() {
      return this._jaxbObj;
   }

   public synchronized String getUnit() {
		if (this._jaxbObj.getRadius() == null) {
			this._jaxbObj.setRadius(new RadiusType());
		}
      return this._jaxbObj.getRadius().getUnit();
   }

   public synchronized Double getValue() {
		if (this._jaxbObj.getRadius() == null) {
			this._jaxbObj.setRadius(new RadiusType());
		}
      return this._jaxbObj.getRadius().getValue();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   public synchronized void setCenterPoint(Point centerPoint) {
      PointType pt = new PointType();
      pt.setLatitude(centerPoint.getLatitude());
      pt.setLongitude(centerPoint.getLongitude());
      this._jaxbObj.setCenter(pt);
   }

   public synchronized void setUnit(String unit) {
		if (this._jaxbObj.getRadius() == null) {
			this._jaxbObj.setRadius(new RadiusType());
		}
      this._jaxbObj.getRadius().setUnit(unit);
   }

   public synchronized void setValue(double value) {
		if (this._jaxbObj.getRadius() == null) {
			this._jaxbObj.setRadius(new RadiusType());
		}
      this._jaxbObj.getRadius().setValue(value);
   }
}

/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.BoundingPolygonType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.PointOrderType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.PointType;
import gov.nasa.podaac.common.api.serviceprofile.BoundingPolygon;
import gov.nasa.podaac.common.api.serviceprofile.Point;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileException;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of BoundingPolygon object using JAXB for XML marshalling and
 * unmarshalling.
 * 
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: BoundingPolygonJaxb.java 1234 2008-05-30 04:45:50Z thuang $
 */
public class BoundingPolygonJaxb extends AccessorBase implements
      BoundingPolygon {

   private BoundingPolygonType _jaxbObj;

   public BoundingPolygonJaxb() {
      this._jaxbObj = new BoundingPolygonType();
   }

   public BoundingPolygonJaxb(BoundingPolygon boundingPolygon) {
      this._jaxbObj = new BoundingPolygonType();
      this._setValues(boundingPolygon.getRegionName(), boundingPolygon
            .getPoints(), boundingPolygon.getPointOrder());
   }

   public BoundingPolygonJaxb(BoundingPolygonType jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public BoundingPolygonJaxb(String region, List<Point> points) {
      this._setValues(region, points, PointOrder.CLOCKWISE);
   }

   public BoundingPolygonJaxb(String region, List<Point> points,
         PointOrder order) throws ServiceProfileException {
      this._jaxbObj = new BoundingPolygonType();
      this._setValues(region, points, order);
   }

   private synchronized void _setValues(String region, List<Point> points,
         PointOrder order) {
      this.setRegionName(region);
      this.setPoints(points);
      this.setPointOrder(order);
   }

   public void addPoint(Point point) {
      Point newPoint = point;
      if (!(newPoint.getImplObj() instanceof PointType)) {
         newPoint = new PointJaxb(point);
      }
      List<PointType> points = this._jaxbObj.getPoint();
      points.add((PointType) newPoint.getImplObj());
      point = newPoint;
   }

   public Point createPoint(double latitude, double longitude) {
      return new PointJaxb(latitude, longitude);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final BoundingPolygonJaxb other = (BoundingPolygonJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public synchronized Object getImplObj() {
      return this._jaxbObj;
   }

   public synchronized PointOrder getPointOrder() {
      return PointOrder.valueOf(this._jaxbObj.getOrder().toString());
   }

   public synchronized List<Point> getPoints() {
      List<Point> result = new LinkedList<Point>();

      List<PointType> points = this._jaxbObj.getPoint();
      if (points != null) {
         for (PointType point : points) {
            result.add(new PointJaxb(point));
         }
      }
      return result;
   }

   public synchronized String getRegionName() {
      return this._jaxbObj.getRegion();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   public synchronized void setPointOrder(PointOrder pointOrder) {
      this._jaxbObj.setOrder(PointOrderType.valueOf(pointOrder.toString()));
   }

   public synchronized void setPoints(List<Point> points) {
      List<PointType> pointList = this._jaxbObj.getPoint();
      pointList.clear();
      for (Point point : points) {
         PointType pt = new PointType();
         pt.setLatitude(point.getLatitude());
         pt.setLongitude(point.getLongitude());
         pointList.add(pt);
      }
   }

   public synchronized void setRegionName(String name) {
      this._jaxbObj.setRegion(name);

   }

}

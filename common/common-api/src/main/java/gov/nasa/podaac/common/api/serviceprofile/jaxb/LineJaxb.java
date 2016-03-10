package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import java.util.List;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.LineType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.PointType;
import gov.nasa.podaac.common.api.serviceprofile.Line;
import gov.nasa.podaac.common.api.serviceprofile.Point;

public class LineJaxb extends AccessorBase implements Line {

   private LineType _jaxbObj;

   public LineJaxb() {
      this._jaxbObj = new LineType();
   }

   public LineJaxb(Line line) {
      this._jaxbObj = new LineType();
      this.setEndPoints(line.getStartPoint(), line.getEndPoint());
   }

   public LineJaxb(LineType jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public Point createPoint(double latitude, double longitude) {
      return new PointJaxb(latitude, longitude);
   }

   public Point getEndPoint() {
      Point result = null;
      List<PointType> points = this._jaxbObj.getPoint();
      if (points.size() == 2) {
         result = new PointJaxb(points.get(1));
      }
      return result;
   }

   @Override
   public Object getImplObj() {
      return this._jaxbObj;
   }

   public Point getStartPoint() {
      Point result = null;
      List<PointType> points = this._jaxbObj.getPoint();
      if (points.size() == 2) {
         result = new PointJaxb(points.get(0));
      }
      return result;

   }

   public void setEndPoints(Point startPoint, Point endPoint) {
      List<PointType> points = this._jaxbObj.getPoint();
      points.clear();

      Point temp = new PointJaxb(startPoint);
      temp.setOwner(this);
      points.add((PointType) temp.getImplObj());
      startPoint = temp;

      temp = new PointJaxb(endPoint);
      temp.setOwner(this);
      points.add((PointType) temp.getImplObj());
      endPoint = temp;
   }
}

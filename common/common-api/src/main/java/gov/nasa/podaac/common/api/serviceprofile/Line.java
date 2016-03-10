package gov.nasa.podaac.common.api.serviceprofile;

public interface Line extends Accessor {

   /**
    * Method to create an endpoint
    * 
    * @param latitude the latitude value
    * @param longitude the longitude value
    * @return
    */
   Point createPoint(double latitude, double longitude);

   /**
    * Method to get the ending point of a line
    * 
    * @return the ending point
    */
   Point getEndPoint();

   /**
    * Method to get the starting point of a line
    * 
    * @return the starting point
    */
   Point getStartPoint();

   /**
    * Method to set the end points of a line
    * 
    * @param startPoint the starting point
    * @param endPoint the end point
    */
   void setEndPoints(Point startPoint, Point endPoint);
   
}

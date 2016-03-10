/***************************************************************************
*
* Copyright 2010, by the California Institute of Technology. ALL
* RIGHTS RESERVED. United States Government Sponsorship acknowledged.
* Any commercial use must be negotiated with the Office of Technology
* Transfer at the California Institute of Technology.
*
* @version $Id: BoundingBoxExtractor.java 12783 2014-02-13 23:12:43Z qchau $
*
****************************************************************************/

package gov.nasa.podaac.common.api.transformer;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import ucar.ma2.Array;
import ucar.ma2.IndexIterator;

import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Group;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import gov.nasa.podaac.common.api.alg.PointPair;
import gov.nasa.podaac.common.api.util.FileUtility;
import gov.nasa.podaac.common.api.serviceprofile.BoundingRectangle;
import gov.nasa.podaac.common.api.serviceprofile.jaxb.BoundingRectangleJaxb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BoundingBoxExtractor {
   // Encapsulate the reading of the latitude and longitude variables and return the 4 corners of the bounding box.

   private static Log _logger = LogFactory.getLog(BoundingBoxExtractor.class);

   private static String m_LATITUDE_VARIABLE_LABEL  = "lat";  // This is the default label for latitude variable in the NetCDF file.
   private static String m_LONGITUDE_VARIABLE_LABEL = "lon";  // This is the default label for longitude variable in the NetCDF file.

   private static ArrayList<Double> m_latitudesList = new ArrayList<Double>(0); // List of first latitude for every row.
   private static ArrayList<Double> m_longitudesList = new ArrayList<Double>(0); // List of first longitude for every row.

   private static BoundingRectangle m_boundingRectangle = new BoundingRectangleJaxb(0.0,0.0,0.0,0.0); // This is the resultant bounding box extracted by this class.

   private static BoundingBoxExtractor m_instance = new BoundingBoxExtractor();

   public BoundingBoxExtractor() {
   }

   public static BoundingBoxExtractor getInstance() {
       // There should be only one instance of this object.
       return m_instance;
   }

   public static void setLatitudeLongitudeLabels(String i_latitudeVariableLabel,
                                                 String i_longitudeVariableLabel) {
       m_LATITUDE_VARIABLE_LABEL  = i_latitudeVariableLabel;   // This will now be the label for the latitude variable in the NetCDF file.
       m_LONGITUDE_VARIABLE_LABEL = i_longitudeVariableLabel;  // This will now be the label for the longitude variable in the NetCDF file.
   }

   private static class VariableTraverseAssistant {
      // A way to encapsulate an iterator and all its attributes extracted from a NetCDF or HDF file.
      // This allow the variable to be traversed through each entry and apply the error checking and applying scaling to the variable itself.

      private String m_assistantName    = null;
      private long   m_variableListSize = 0;
      private long   m_validMin         = 0;
      private long   m_validMax         = 0;
      private long   m_fillValue        = 0;
      private double m_scaleFactor      = 0.0;
      private IndexIterator m_iterator  = null;

      public VariableTraverseAssistant() {
      }

      public VariableTraverseAssistant(String        i_assistantName,
                                       long          i_variableListSize,
                                       long          i_validMin,
                                       long          i_validMax,
                                       long          i_fillValue,
                                       double        i_scaleFactor,
                                       IndexIterator i_iterator)
      {
          // No error checking at the moment.
          m_assistantName    = i_assistantName; 
          m_variableListSize = i_variableListSize; 
          m_validMin         = i_validMin; 
          m_validMax         = i_validMax; 
          m_fillValue        = i_fillValue; 
          m_scaleFactor      = i_scaleFactor;
          m_iterator         = i_iterator; 
      }

      public String getAssistantName() {
          return m_assistantName;
      }

      public long getVariablelistSize() {
          return m_variableListSize;
      }

      public long getValidMin() {
          return m_validMin;
      }

      public long getValidMax() {
          return m_validMax;
      }

      public long getFillValue() {
          return m_fillValue;
      }

      public double getScaleFactor() {
          return m_scaleFactor;
      }

      public IndexIterator getIterator() {
          return m_iterator;
      }
   } // end VariableTraverseAssistant class

   private static VariableTraverseAssistant _extractVariable(NetcdfFile      i_dataFilePointer,
                                                             String          i_variableName,
                                                             List<Dimension> i_sharedDimensions,
                                                             String          i_tempNetCDFFile) throws IOException
   {
      // Function extract the variable and the array associated with that variable and return an IndexIterator so the user can iterate through.
//if (2 == 2) {
//throw new IOException("This is a test of throwing IOException from BoundingBoxExtractor::_extractVariable() function.");
//}

     long a_variableListSize = 0;
     long a_validMin  = 0;
     long a_validMax  = 0;
     long a_fillValue = 0;
     double a_scaleFactor = 0;
     IndexIterator a_iterator = null;

     long sharedNumRows  = i_sharedDimensions.get(0).getLength();  // The NUMROWS is the first element.
     long sharedNumCells = i_sharedDimensions.get(1).getLength();  // The NUMCELLS is the 2nd element.

      Variable variableObject = i_dataFilePointer.findVariable(i_variableName);

      // Make sure variable is not null or empty.

      if (variableObject == null) {
          _logger.error("Function NetcdfFile.findVariable("+i_variableName+") returns null in file [" + i_tempNetCDFFile+ "]");
//      System.out.println("Function NetcdfFile.getVariables() returns null list of zero size list [" + i_tempNetCDFFile+ "]");
          throw new IOException("Function NetcdfFile.getVariables("+i_variableName+") returns null in file [" + i_tempNetCDFFile+ "]");
      }

      // Get the 2 dimensions of this variableObject.

      List<Dimension> variableDimensions = variableObject.getDimensions();

      // Make sure it is 2 dimensions.
      if ((variableDimensions.size() < 2) || (variableDimensions.size() != i_sharedDimensions.size())) {
          _logger.error("Expecting " + i_variableName + " array to be 2 dimensions, got " + variableDimensions.size() + " from file [" + i_tempNetCDFFile+ "]");
//      System.out.println("Function NetcdfFile.getVariables() returns null list of zero size list [" + i_tempNetCDFFile+ "]");
          throw new IOException("Expecting " + i_variableName + " array to be 2 dimensions, got " + variableDimensions.size() + " from file [" + i_tempNetCDFFile+ "]");
      }

      // A typical header attribute of a file:
      //
      // dimensions:
      //         NUMROWS = 3259 ;
      //         NUMCELLS = 82 ;
      // variables:
      //        int lat(NUMROWS, NUMCELLS) ;
      //                 lat:_FillValue = -2147483647 ;
      //                 lat:valid_min = -9000000 ;
      //                 lat:valid_max = 9000000 ;
      //                 lat:long_name = "latitude" ;
      //                 lat:units = "degrees_north" ;
      //                 lat:scale_factor = 1.e-05 ;
      //         int lon(NUMROWS, NUMCELLS) ;
      //                 lon:_FillValue = -2147483647 ;
      //                 lon:valid_min = 0 ;
      //                 lon:valid_max = 36000000 ;
      //                 lon:long_name = "longitude" ;
      //                 lon:units = "degrees_east" ;
      //                 lon:scale_factor = 1.e-05 ;
      //

      // Read the array out of the variable object.

      Array a_variableArray = variableObject.read();
      a_variableListSize = a_variableArray.getSize();

      // Get the attribute of this variableObject.  We'll need these inorder to re-calculate the original value.

      a_validMin  = (variableObject.findAttribute("valid_min").getNumericValue()).intValue();
      a_validMax  = (variableObject.findAttribute("valid_max").getNumericValue()).intValue();
      // Some granule does not have _Fillvalue.  We have to be careful how we read it otherwise will get Null Pointer Exception.
      if (variableObject.findAttribute("_FillValue") != null)  {
          a_fillValue = (variableObject.findAttribute("_FillValue").getNumericValue()).intValue();
      }
      a_scaleFactor = (variableObject.findAttribute("scale_factor").getNumericValue()).doubleValue();

if (2 == 3) { // Easy way to comment out debug printing.
      System.out.println("------------------------------------------------------------");
      System.out.println(i_variableName + " variable by element:");
      System.out.println("------------------------------------------------------------");
      System.out.println("");
      System.out.println("sharedNumRows  [" + sharedNumRows + "]");
      System.out.println("sharedNumCells [" + sharedNumCells + "]");
      System.out.println("a_validMin [" + a_validMin + "]");
      System.out.println("a_validMax [" + a_validMax + "]");
      System.out.println("a_fillValue [" + a_fillValue + "]");
      System.out.println("a_scaleFactor [" + a_scaleFactor + "]");
      System.out.println("");
      System.out.println("variableObject.findAttribute(valid_min) [" + variableObject.findAttribute("valid_min") + "]");
      System.out.println("variableObject.findAttribute(valid_max) [" + variableObject.findAttribute("valid_max") + "]");
      System.out.println("variableObject.findAttribute(_FillValue) [" + variableObject.findAttribute("_FillValue") + "]");
      System.out.println("variableObject.findAttribute(scale_factor) [" + variableObject.findAttribute("scale_factor") + "]");
      System.out.println("");
      System.out.println("a_variableListSize = " + a_variableListSize);
      System.out.println("a_variableArray.getRank() = " + a_variableArray.getRank());
      System.out.println("a_variableArray type [" + a_variableArray.getElementType() + "] ");
}

      // Get an index iterator for traversing the array in canonical order.

      a_iterator = a_variableArray.getIndexIterator();

      // Build a VariableTraverseAssistant to return to callee.

      VariableTraverseAssistant o_assistant = new VariableTraverseAssistant(i_variableName,
                                                                            a_variableListSize,
                                                                            a_validMin,
                                                                            a_validMax,
                                                                            a_fillValue,
                                                                            a_scaleFactor,
                                                                            a_iterator);

      return o_assistant;
   }

   public static boolean extractBoundingBox(NetcdfFile i_dataFilePointer,
                                               String i_tempNetCDFFile,
                                               String i_latitudeVariableShortName,
                                               String i_longitudeVariableShortName) throws IOException
   {
      // Extract the bounding box values so we can translate it to a rectangle box.

//if (2 == 2) {
//throw new IOException("This is a test of throwing IOException from BoundingBoxExtractor::extractBoundingBox() function.");
//}
      boolean o_transformedSuccessFlag = true;  // Start out assuming that we will be successful in transforming the rectangle box.

      // Get the shared dimension in this file and the rest of the code can use it.

      List<Dimension> sharedDimensions = i_dataFilePointer.getDimensions();

      // Make sure it is 2 dimensions.
      if (sharedDimensions.size() < 2) {
          _logger.error("Expecting shared dimensions to be 2, got " + sharedDimensions.size() + " from file [" + i_tempNetCDFFile+ "]");
//      System.out.println("Function NetcdfFile.getVariables() returns null list of zero size list [" + i_tempNetCDFFile+ "]");
          throw new IOException("Expecting shared dimensions to be 2, got " + sharedDimensions.size() + " from file [" + i_tempNetCDFFile+ "]");
      }

      long sharedNumRows  = sharedDimensions.get(0).getLength();  // The NUMROWS is the first element.
      long sharedNumCells = sharedDimensions.get(1).getLength();  // The NUMCELLS is the 2nd element.

      VariableTraverseAssistant a_latAssistant = _extractVariable(i_dataFilePointer,i_latitudeVariableShortName,sharedDimensions,i_tempNetCDFFile);
      _convertToOriginalLatitudeLongitudeValues(a_latAssistant,sharedNumRows,sharedNumCells,i_tempNetCDFFile);

//System.out.println("m_latitudesList.size() = " + m_latitudesList.size());

      VariableTraverseAssistant a_lonAssistant = _extractVariable(i_dataFilePointer,i_longitudeVariableShortName,sharedDimensions,i_tempNetCDFFile);
      _convertToOriginalLatitudeLongitudeValues(a_lonAssistant,sharedNumRows,sharedNumCells,i_tempNetCDFFile);

//System.out.println("m_longitudesList.size() = " + m_longitudesList.size());

      // We now have the latitude and longitude values of every row and column,
      // we can now calculate the 4 corners.

      _calculateFourCorners(sharedNumRows);

      return o_transformedSuccessFlag;
   }

   public BoundingRectangle getBoundingRectangle() {
       return m_boundingRectangle;
   }

   private static void _calculateFourCorners(long i_sharedNumRows) {

      // We now have the latitude and longitude values of all the rows, we can extract the 4 corners.

      Object obj = null;  // Use this to get min and max values of latitude and longitudes.

      obj = Collections.min(m_longitudesList);
      m_boundingRectangle.setWestLongitude(((Double) obj).doubleValue());
      obj = Collections.max(m_longitudesList);
      m_boundingRectangle.setEastLongitude(((Double) obj).doubleValue());

      obj = Collections.min(m_latitudesList);
      m_boundingRectangle.setSouthLatitude(((Double) obj).doubleValue());
      obj = Collections.max(m_latitudesList);
      m_boundingRectangle.setNorthLatitude(((Double) obj).doubleValue());

// Print longitudes for debugging.

if (2 == 3) {
System.out.println("_calculateFourCorners: m_longitudesList.get(0) = " +  m_longitudesList.get(0));
System.out.println("_calculateFourCorners: m_longitudesList.get(1) = " +  m_longitudesList.get(1));
System.out.println("_calculateFourCorners: m_longitudesList.get(2) = " +  m_longitudesList.get(2));
System.out.println("_calculateFourCorners: m_longitudesList.get(3) = " +  m_longitudesList.get(3));
System.out.println("_calculateFourCorners: m_longitudesList.get(4) = " +  m_longitudesList.get(4));

System.out.println("");

// Print latitudes for debugging.

System.out.println("_calculateFourCorners: m_latitudesList.get(0) = " +  m_latitudesList.get(0));
System.out.println("_calculateFourCorners: m_latitudesList.get(1) = " +  m_latitudesList.get(1));
System.out.println("_calculateFourCorners: m_latitudesList.get(2) = " +  m_latitudesList.get(2));
System.out.println("_calculateFourCorners: m_latitudesList.get(3) = " +  m_latitudesList.get(3));
System.out.println("_calculateFourCorners: m_latitudesList.get(4) = " +  m_latitudesList.get(4));

System.out.println("");
}
   }

   private static void _convertToOriginalLatitudeLongitudeValues(VariableTraverseAssistant i_assistant,
                                                          long                      i_sharedNumRows,
                                                          long                      i_sharedNumCells,
                                                          String                    i_tempNetCDFFile)
   {
      // Get all the attributes associated with a VariableTraverseAssistant object.

      IndexIterator a_iterator = i_assistant.getIterator();
      String a_dimensionName   = i_assistant.getAssistantName();
      long   a_fillValue       = i_assistant.getFillValue();
      double a_scaleFactor     = i_assistant.getScaleFactor();

//      System.out.println("_convertToOriginalLatitudeLongitudeValues:i_sharedNumRows  " + i_sharedNumRows); 
//      System.out.println("_convertToOriginalLatitudeLongitudeValues:i_sharedNumCells " + i_sharedNumCells); 

      // Create lists big enough to hold every latitude or longitude value.
      if (a_dimensionName.equals(m_LATITUDE_VARIABLE_LABEL)) {
          if (m_latitudesList != null) m_latitudesList.clear(); // Start out with a fresh list.

          m_latitudesList = new ArrayList<Double>((int)(i_sharedNumRows*i_sharedNumCells)); // List of latitudes.
      } else if (a_dimensionName.equals(m_LONGITUDE_VARIABLE_LABEL)) {
          if (m_longitudesList != null) m_longitudesList.clear(); // Start out with a fresh list.

          m_longitudesList = new ArrayList<Double>((int)(i_sharedNumRows*i_sharedNumCells)); // List of longitudes.
      } else {
          _logger.error("Do not recognize a_dimensionName " + a_dimensionName + ".  Valid values are lat and lon only.  NetCDF file [" + i_tempNetCDFFile + "]");
      }

      // For each value, we convert the intValue to the original value by multiplying by the scale factor.

      double originalValue    = 0.0;
      int    entireArrayIndex = 0;

      while (a_iterator.hasNext() && entireArrayIndex < (i_sharedNumRows*i_sharedNumCells)) {
          int intValue = a_iterator.getIntNext();  // Get one integer value via the iterator.

              // TODO: For now, we exit to give the user a chance to fix the fill value.
              // Do a sanity check to make sure we don't have bad values.
              if (intValue == a_fillValue) {
                  System.out.println("_convertToOriginalLatitudeLongitudeValues:ERROR, intValue " + intValue + " is the same as a_fillValue " + a_fillValue);
                  System.out.println("_convertToOriginalLatitudeLongitudeValues:Cannot calculate originalValue variable.");
                  System.out.println("_convertToOriginalLatitudeLongitudeValues:entireArrayIndex = " + entireArrayIndex + " out of " + (i_sharedNumRows*i_sharedNumCells));
                  System.exit(1);
              }

              originalValue = intValue * a_scaleFactor;  // Get the original value.

              if (a_dimensionName.equals(m_LATITUDE_VARIABLE_LABEL)) {
//System.out.println("Adding originalValue [" + originalValue + "] to m_latitudesList");

                  m_latitudesList.add(new Double(originalValue)); // Save the first latitude value in this specific array.

              } else if (a_dimensionName.equals(m_LONGITUDE_VARIABLE_LABEL)) {

//System.out.println("Adding originalValue [" + originalValue + "] to m_longitudesList ");

                  m_longitudesList.add(new Double(originalValue)); // Save the first longitude value in this specific array.

              }
//System.out.println("entireArrayIndex = " + entireArrayIndex);

          entireArrayIndex++;
      }
   }
}

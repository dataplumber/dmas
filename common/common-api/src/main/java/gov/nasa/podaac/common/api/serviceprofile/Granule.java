/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

import gov.nasa.podaac.common.api.serviceprofile.Common.AccessRole;
import gov.nasa.podaac.common.api.serviceprofile.Common.DataPass;
import gov.nasa.podaac.common.api.serviceprofile.Common.DayNight;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public interface Granule extends Accessor {

   void addExtra(String name, String value);

   void addFile(GranuleFile file);

   void addLine(Line line);

   void addLink(GranuleFileLink link);

   void addPoint(Point point);

   void addPolygon(BoundingPolygon polygon);

   void clearExtras();

   void clearFiles();

   void clearLines();

   void clearLinks();

   void clearPoints();

   void clearPolygons();

   BoundingPolygon createBoundingPolygon();

   BoundingRectangle createBoundingRectangle();

   Ellipse createEllipse();

   GranuleFile createFile();

   GranuleHistory createGranuleHistory();

   Line createLine();

   GranuleFileLink createLink(String GranuleName, String FileName);

   Point createPoint();

   Swath createSwath();

   AccessRole getAccessConstraint();

   String getAncillaryName();

   BoundingRectangle getBoundingRectangle();

   Long getCollectionId();

   String getCollectionName();

   String getComment();

   Date getCreateTime();

   Integer getCycle();

   String getDatasetName();

   DayNight getDayNightMode();

   Integer getDayOfYearStart();

   Ellipse getEllipse();

   Properties getExtras();

   int getFileCount();

   Set<GranuleFile> getFiles();

   GranuleHistory getGranuleHistory();

   String getGroupName();

   Long getId();

   String getInputParameters();

   Double getIntercept();

   Double getLatitudeStep();

   String getLatitudeUnits();

   List<Line> getLines();

   Set<GranuleFileLink> getLinks();

   Double getLongitudeStep();

   String getLongitudeUnits();

   String getName();

   Long getNumberOfColumns();

   Long getNumberOfLines();

   String getObservationMode();

	String getOfficialName();

   Integer getPass();

   DataPass getPassType();

   List<Point> getPoints();

   List<BoundingPolygon> getPolygons();

   String getProductType();

   String getProjection();

   String getReplace();

   Integer getRevolution();

   String getScaling();

   String getScalingEquation();

   Double getSlope();

   String getSurfaceType();

   Swath getSwath();

   Date getTemporalCoverageStartTime();

   Date getTemporalCoverageStopTime();

   String getVersion();

   boolean isStoreAsGroup();

   void setAccessConstraint(AccessRole accessRole);

   void setAncillaryName(String name);

   void setBoundingRectangle(BoundingRectangle rectangle);

   void setCollectionId(Long collectionId);

   void setCollectionName(String name);

   void setComment(String comment);

   void setCreateTime(Date createTime);

   void setCreateTime(long createTime);

   void setCycle(int cycle);

   void setDatasetName(String datasetName);

   void setDayNightMode(DayNight dayNight);

   void setDayOfYearStart(int dayOfYear);

   void setEllipse(Ellipse ellipse);

   void setGranuleHistory(GranuleHistory history);

   void setGroupName(String name);

   void setId(Long id);

   void setInputParameters(String inputParameters);

   void setIntercept(Double intercept);

   void setLatitudeStep(Double step);

   void setLatitudeUnits(String units);

   void setLongitudeStep(Double step);

   void setLongitudeUnits(String units);

   void setName(String name);

   void setNumberOfColumns(Long numberOfColumns);

   void setNumberOfLines(Long numberOfLines);

   void setObservationMode(String observationMode);

	void setOfficialName(String name);

   void setPass(int pass);

   void setPassType(DataPass passType);

   void setProductType(String productType);

   void setProjection(String projection);

   void setReplace(String name);

   void setRevolution(int revolution);

   void setScaling(String scaling);

   void setScalingEquation(String scalingEquation);

   void setSlope(Double slop);

   void setStoreAsGroup(boolean flag);

   void setSurfaceType(String surfaceType);

   void setSwath(Swath swath);

   void setTemporalCoverageStartTime(Date startTime);

   void setTemporalCoverageStartTime(long startTime);

   void setTemporalCoverageStopTime(Date stopTime);

   void setTemporalCoverageStopTime(long stopTime);

   void setVersion(String version);
}

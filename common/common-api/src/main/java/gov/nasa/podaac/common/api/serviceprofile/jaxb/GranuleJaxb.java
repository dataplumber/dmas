/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.*;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.DomainAttributeType.Extra;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.GranuleType.Files;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.SpatialDomainType.Polygons;
import gov.nasa.podaac.common.api.serviceprofile.*;
import gov.nasa.podaac.common.api.serviceprofile.Common.AccessRole;
import gov.nasa.podaac.common.api.serviceprofile.Common.DataPass;
import gov.nasa.podaac.common.api.serviceprofile.Common.DayNight;

import java.math.BigInteger;
import java.util.*;

public class GranuleJaxb extends AccessorBase implements Granule {

   private GranuleType _jaxbObj;

   public GranuleJaxb() {
      this._jaxbObj = new GranuleType();
   }

   public GranuleJaxb(Granule granule) {
      this._jaxbObj = new GranuleType();
      this.setAccessConstraint(granule.getAccessConstraint());
      this.setAncillaryName(granule.getAncillaryName());
      List<BoundingPolygon> polygons = granule.getPolygons();
      for (BoundingPolygon polygon : polygons) {
         this.addPolygon(polygon);
      }
      this.setBoundingRectangle(granule.getBoundingRectangle());
      this.setCollectionId(granule.getCollectionId());
      this.setCollectionName(granule.getCollectionName());
      this.setComment(granule.getComment());
      this.setCreateTime(granule.getCreateTime());
      this.setCycle(granule.getCycle());
      this.setDatasetName(granule.getDatasetName());
      this.setDayNightMode(granule.getDayNightMode());
      this.setDayOfYearStart(granule.getDayOfYearStart());
      this.setEllipse(granule.getEllipse());
      this.setId(granule.getId());
      this.setName(granule.getName());
      this.setReplace(granule.getReplace());
      this.setPass(granule.getPass());
      this.setPassType(granule.getPassType());
      List<Point> points = granule.getPoints();
      for (Point point : points) {
         this.addPoint(point);
      }
      List<Line> lines = granule.getLines();
      for (Line line : lines) {
         this.addLine(line);
      }
      this.setRevolution(granule.getRevolution());
      this.setSwath(granule.getSwath());
      this.setTemporalCoverageStartTime(granule.getTemporalCoverageStartTime());
      this.setTemporalCoverageStopTime(granule.getTemporalCoverageStopTime());
      this.setVersion(granule.getVersion());
      this.setGranuleHistory(granule.getGranuleHistory());
   }

   public GranuleJaxb(GranuleType jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   protected List<Extra> _getExtras() {
      if (this._jaxbObj.getExtras() == null) {
         this._jaxbObj.setExtras(new DomainAttributeType());
      }
      return this._jaxbObj.getExtras().getExtra();
   }

   protected List<GranuleFileType> _getFiles() {
      if (this._jaxbObj.getFiles() == null) {
         this._jaxbObj.setFiles(new Files());
      }
      return this._jaxbObj.getFiles().getFile();
   }

   protected List<GranuleFileLinkType> _getLinks() {
      if (this._jaxbObj.getFiles() == null) {
         this._jaxbObj.setFiles(new Files());
      }
      return this._jaxbObj.getFiles().getLink();
   }

   private Polygons _getPolygons() {
      SpatialDomainType sdt = this._jaxbObj.getSpatialCoverage();
      if (sdt == null) {
         sdt = new SpatialDomainType();
         this._jaxbObj.setSpatialCoverage(sdt);
      }

      if (sdt.getPolygons() == null) {
         sdt.setPolygons(new Polygons());
      }

      return sdt.getPolygons();
   }

   protected SpatialDomainType _getSpatialCoverage() {
      return this._jaxbObj.getSpatialCoverage();
   }

   public synchronized void addExtra(String name, String value) {
      Extra extra = new Extra();
      extra.setName(name);
      extra.setValue(value);
      this._getExtras().add(extra);
   }

   public synchronized void addFile(GranuleFile file) {
      GranuleFile temp = file;
      if (!(temp.getImplObj() instanceof GranuleFileType)) {
         temp = new GranuleFileJaxb(file);
      }
      this._getFiles().add((GranuleFileType) temp.getImplObj());
      temp.setOwner(this);
      file = temp;
   }

   public void addLine(Line line) {
      SpatialDomainType sdt = this._getSpatialCoverage();
      if (sdt == null) {
         sdt = new SpatialDomainType();
         this._jaxbObj.setSpatialCoverage(sdt);
      }
      List<LineType> lines = sdt.getLines().getLine();
      if (!(line.getImplObj() instanceof LineType)) {
         line = new LineJaxb(line);
         line.setOwner(this);
      }
      lines.add((LineType) line.getImplObj());
   }

   public void addLink(GranuleFileLink link) {
      GranuleFileLink temp = link;
      if (!(temp.getImplObj() instanceof GranuleFileLinkType)) {
         temp = new GranuleFileLinkJaxb(link);
      }
      this._getLinks().add((GranuleFileLinkType) temp.getImplObj());
      temp.setOwner(this);
      link = temp;
   }

   public void addPoint(Point point) {
      SpatialDomainType sdt = this._getSpatialCoverage();
      if (sdt == null) {
         sdt = new SpatialDomainType();
         this._jaxbObj.setSpatialCoverage(sdt);
      }
      List<PointType> points = sdt.getPoints().getPoint();
      if (!(point.getImplObj() instanceof PointType)) {
         point = new PointJaxb(point);
         point.setOwner(this);
      }
      points.add((PointType) point.getImplObj());
   }

   public void addPolygon(BoundingPolygon polygon) {
      if (polygon == null)
         return;

      Polygons polygons = this._getPolygons();
      BoundingPolygon temp = polygon;
      if (!(temp.getImplObj() instanceof BoundingPolygonType)) {
         temp = new BoundingPolygonJaxb(polygon);
      }
      polygons.getPolygon().add((BoundingPolygonType) temp.getImplObj());
      temp.setOwner(this);
      polygon = temp;
   }

   public void clearExtras() {
      this._getExtras().clear();
   }

   public void clearFiles() {
      this._getFiles().clear();
   }

   public void clearLines() {
      SpatialDomainType sdt = this._getSpatialCoverage();
      sdt.getLines().getLine().clear();
   }

   public void clearPoints() {
      SpatialDomainType sdt = this._getSpatialCoverage();
      sdt.getPoints().getPoint().clear();
   }

   public void clearPolygons() {
      SpatialDomainType sdt = this._getSpatialCoverage();
      sdt.getPolygons().getPolygon().clear();
   }

   public void clearLinks() {
      this._getLinks().clear();
   }

   public BoundingPolygon createBoundingPolygon() {
      return new BoundingPolygonJaxb();
   }

   public BoundingRectangle createBoundingRectangle() {
      return new BoundingRectangleJaxb();
   }

   public Ellipse createEllipse() {
      return new EllipseJaxb();
   }

   public GranuleFile createFile() {
      return new GranuleFileJaxb();
   }

   public GranuleHistory createGranuleHistory() {
      return new GranuleHistoryJaxb();
   }

   public Line createLine() {
      return new LineJaxb();
   }

   public GranuleFileLink createLink(String GranuleName, String FileName) {
      return new GranuleFileLinkJaxb(GranuleName, FileName);
   }

   public Point createPoint() {
      return new PointJaxb();
   }

   public Swath createSwath() {
      return new SwathJaxb();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final GranuleJaxb other = (GranuleJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public AccessRole getAccessConstraint() {
      if (this._jaxbObj.getAccessConstraint() != null)
         return AccessRole.valueOf(this._jaxbObj.getAccessConstraint()
               .toString());
      return null;
   }

   public String getAncillaryName() {
      return this._jaxbObj.getAncillaryName();
   }

   public synchronized BoundingRectangle getBoundingRectangle() {
      SpatialDomainType coverage = this._getSpatialCoverage();
      if (coverage != null && coverage.getRectangle() != null) {
         BoundingRectangle rectangle =
               new BoundingRectangleJaxb(coverage.getRectangle());
         rectangle.setOwner(this);
         return rectangle;
      }
      return null;
   }

   public Long getCollectionId() {
      return this._jaxbObj.getCollectionId();
   }

   public String getCollectionName() {
      return this._jaxbObj.getCollectionName();
   }

   public String getComment() {
      return this._jaxbObj.getComment();
   }

   public Date getCreateTime() {
      return new Date(this._jaxbObj.getCreateTime().longValue());
   }

   public Integer getCycle() {
      if (this._jaxbObj.getCycle() != null)
         return this._jaxbObj.getCycle().intValue();
      return null;
   }

   public String getDatasetName() {
      return this._jaxbObj.getDatasetName();
   }

   public DayNight getDayNightMode() {
      if (this._jaxbObj.getDayNightMode() != null) {
         return DayNight.valueOf(this._jaxbObj.getDayNightMode().value());
      }
      return null;
   }

   public Integer getDayOfYearStart() {
      if (this._jaxbObj.getDayOfYearStart() != null) {
         return this._jaxbObj.getDayOfYearStart().intValue();
      }
      return null;
   }

   public synchronized Ellipse getEllipse() {
      SpatialDomainType coverage = this._getSpatialCoverage();
      if (coverage != null && coverage.getEllipse() != null) {
         Ellipse ellipse = new EllipseJaxb(coverage.getEllipse());
         ellipse.setOwner(this);
         return ellipse;
      }
      return null;
   }

   public Properties getExtras() {
      Properties result = new Properties();
      if (this._jaxbObj.getExtras() != null) {
         List<Extra> extras = this._jaxbObj.getExtras().getExtra();
         for (Extra extra : extras) {
            result.setProperty(extra.getName(), extra.getValue());
         }

      }
      return result;
   }

   public int getFileCount() {
      if (this._jaxbObj.getFiles() != null)
         return this._jaxbObj.getFiles().getFile().size();
      return 0;
   }

   public synchronized Set<GranuleFile> getFiles() {
      Set<GranuleFile> result = new HashSet<GranuleFile>();
      if (this._jaxbObj.getFiles() != null) {
         List<GranuleFileType> files = this._jaxbObj.getFiles().getFile();
         for (GranuleFileType file : files) {
            GranuleFile temp = new GranuleFileJaxb(file);
            temp.setOwner(this);
            result.add(temp);
         }
      }
      return result;
   }

   public GranuleHistory getGranuleHistory() {
      GranuleHistory result = null;
      if (this._jaxbObj.getHistory() != null) {
         result = new GranuleHistoryJaxb(this._jaxbObj.getHistory());
      }
      return result;
   }

   public String getGroupName() {
      String result = null;
      if (this._jaxbObj.getGroup() != null) {
         result = this._jaxbObj.getGroup().getName();
      }
      return result;
   }

   public Long getId() {
      return this._jaxbObj.getId();
   }

   public String getInputParameters() {
      return this._jaxbObj.getInputParameters();
   }

   public Double getIntercept() {
      return this._jaxbObj.getIntercept();
   }

   public Double getLatitudeStep() {
      return this._jaxbObj.getLatitudeStep();
   }

   public String getLatitudeUnits() {
      return this._jaxbObj.getLatitudeUnits();
   }

   @Override
   public Object getImplObj() {
      return this._jaxbObj;
   }

   public List<Line> getLines() {
      List<Line> result = new LinkedList<Line>();
      SpatialDomainType sdt = this._getSpatialCoverage();
      List<LineType> lines = sdt.getLines().getLine();
      for (LineType line : lines) {
         LineJaxb l = new LineJaxb(line);
         l.setOwner(this);
         result.add(l);
      }
      return result;
   }

   public synchronized Set<GranuleFileLink> getLinks() {
      Set<GranuleFileLink> result = new HashSet<GranuleFileLink>();
      if (this._jaxbObj.getFiles() != null) {
         List<GranuleFileLinkType> links = this._jaxbObj.getFiles().getLink();
         for (GranuleFileLinkType link : links) {
            GranuleFileLink temp = new GranuleFileLinkJaxb(link);
            temp.setOwner(this);
            result.add(temp);
         }
      }
      return result;
   }

   public Double getLongitudeStep() {
      return this._jaxbObj.getLongitudeStep();
   }

   public String getLongitudeUnits() {
      return this._jaxbObj.getLongitudeUnits();
   }

   public String getName() {
      return this._jaxbObj.getName();
   }

   public Long getNumberOfColumns() {
      if (this._jaxbObj.getNumberOfColumns() != null)
         return this._jaxbObj.getNumberOfColumns().longValue();
      return null;
   }

   public Long getNumberOfLines() {
      if (this._jaxbObj.getNumberOfLines() != null)
         return this._jaxbObj.getNumberOfLines().longValue();
      return null;
   }

   public String getObservationMode() {
      return this._jaxbObj.getObservationMode();
   }

   public String getOfficialName() {
      return this._jaxbObj.getOfficialName();
   }

   public Integer getPass() {
      if (this._jaxbObj.getPass() != null)
         return this._jaxbObj.getPass().intValue();
      return null;
   }

   public DataPass getPassType() {
      if (this._jaxbObj.getPassType() != null) {
         return DataPass.valueOf(this._jaxbObj.getPassType().value());
      }
      return null;
   }

   public synchronized List<Point> getPoints() {
      List<Point> result = new LinkedList<Point>();

      SpatialDomainType coverage = this._getSpatialCoverage();
      if (coverage != null && coverage.getPoints() != null) {
         List<PointType> points = coverage.getPoints().getPoint();
         for (PointType point : points) {
            Point p = new PointJaxb(point);
            p.setOwner(this);
            result.add(p);
         }
      }
      return result;
   }

   public synchronized List<BoundingPolygon> getPolygons() {
      List<BoundingPolygon> result = new LinkedList<BoundingPolygon>();
      SpatialDomainType coverage = this._getSpatialCoverage();
      if (coverage != null && coverage.getPolygons() != null) {
         List<BoundingPolygonType> polygons =
               coverage.getPolygons().getPolygon();
         for (BoundingPolygonType polygon : polygons) {
            BoundingPolygon p = new BoundingPolygonJaxb(polygon);
            p.setOwner(this);
            result.add(p);
         }
      }
      return result;
   }

   public String getProductType() {
      return this._jaxbObj.getProductType();
   }

   public String getProjection() {
      return this._jaxbObj.getProjection();
   }

   public String getReplace() {
      return this._jaxbObj.getReplace();
   }

   public Integer getRevolution() {
      if (this._jaxbObj.getRevolution() != null)
         return this._jaxbObj.getRevolution().intValue();
      return null;
   }

   public String getScaling() {
      return this._jaxbObj.getScaling();
   }

   public String getScalingEquation() {
      return this._jaxbObj.getScalingEquation();
   }

   public Double getSlope() {
      return this._jaxbObj.getSlope();
   }

   public String getSurfaceType() {
      return this._jaxbObj.getSurfaceType();
   }

   public synchronized Swath getSwath() {
      SpatialDomainType coverage = this._getSpatialCoverage();
      if (coverage != null && coverage.getSwath() != null) {
         Swath swath = new SwathJaxb(coverage.getSwath());
         swath.setOwner(this);
         return swath;
      }
      return null;
   }

   public Date getTemporalCoverageStartTime() {
      TimeStampType coverage = this._jaxbObj.getTemporalCoverage();
      if (coverage != null && coverage.getStart() != null) {
         return new Date(coverage.getStart().longValue());
      }
      return null;
   }

   public Date getTemporalCoverageStopTime() {
      TimeStampType coverage = this._jaxbObj.getTemporalCoverage();
      if (coverage != null && coverage.getStop() != null) {
         return new Date(coverage.getStop().longValue());
      }
      return null;
   }

   public String getVersion() {
      if (this._jaxbObj.getVersion() != null) {
         return this._jaxbObj.getVersion();
      }
      return null;
   }

   public boolean isStoreAsGroup() {
      boolean result = false;
      if (this._jaxbObj.getGroup() != null && this._jaxbObj.getGroup().isStoreAsGroup() != null) {
         result = this._jaxbObj.getGroup().isStoreAsGroup();
      }

      return result;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   public void setAccessConstraint(AccessRole accessRole) {
      this._jaxbObj.setAccessConstraint(AccessRoleType.valueOf(accessRole
            .toString()));
   }

   public void setAncillaryName(String name) {
      this._jaxbObj.setAncillaryName(name);
   }

   public void setBoundingRectangle(BoundingRectangle rectangle) {
      if (rectangle == null)
         return;
      BoundingRectangle temp = rectangle;
      if (!(temp.getImplObj() instanceof BoundingRectangleType)) {
         temp = new BoundingRectangleJaxb(rectangle);
      }
      SpatialDomainType sdt = this._jaxbObj.getSpatialCoverage();
      if (sdt == null) {
         sdt = new SpatialDomainType();
         this._jaxbObj.setSpatialCoverage(sdt);
      }
      sdt.setRectangle((BoundingRectangleType) temp.getImplObj());
      temp.setOwner(this);
      rectangle = temp;
   }

   public void setCollectionId(Long collectionId) {
      this._jaxbObj.setCollectionId(collectionId);
   }

   public void setCollectionName(String name) {
      this._jaxbObj.setCollectionName(name);
   }

   public void setComment(String comment) {
      this._jaxbObj.setComment(comment);
   }

   public void setCreateTime(Date createTime) {
      this._jaxbObj.setCreateTime(BigInteger.valueOf(createTime.getTime()));
   }

   public void setCreateTime(long createTime) {
      this._jaxbObj.setCreateTime(BigInteger.valueOf(createTime));
   }

   public void setCycle(int cycle) {
      this._jaxbObj.setCycle(BigInteger.valueOf(cycle));
   }

   public void setDatasetName(String datasetName) {
      this._jaxbObj.setDatasetName(datasetName);
   }

   public void setDayNightMode(DayNight dayNight) {
      this._jaxbObj
            .setDayNightMode(DayNightType.fromValue(dayNight.toString()));
   }

   public void setDayOfYearStart(int dayOfYear) {
      this._jaxbObj.setDayOfYearStart(BigInteger.valueOf(dayOfYear));
   }

   public void setEllipse(Ellipse ellipse) {
      if (ellipse == null)
         return;
      Ellipse temp = ellipse;
      if (!(temp.getImplObj() instanceof EllipseType)) {
         temp = new EllipseJaxb(ellipse);
      }
      SpatialDomainType sdt = this._jaxbObj.getSpatialCoverage();
      if (sdt == null) {
         sdt = new SpatialDomainType();
         this._jaxbObj.setSpatialCoverage(sdt);
      }
      sdt.setEllipse((EllipseType) temp.getImplObj());
      temp.setOwner(this);
      ellipse = temp;
   }

   public void setGranuleHistory(GranuleHistory history) {
      GranuleHistory temp = history;
      if (!(temp.getImplObj() instanceof GranuleHistoryType)) {
         temp = new GranuleHistoryJaxb(history);
      }
      this._jaxbObj.setHistory((GranuleHistoryType) temp.getImplObj());
      temp.setOwner(this);
      history = temp;
   }

   public void setGroupName(String name) {
      if (this._jaxbObj.getGroup() == null) {
         this._jaxbObj.setGroup(new GranuleType.Group());
      }

      this._jaxbObj.getGroup().setName(name);
   }

   public void setId(Long id) {
      this._jaxbObj.setId(id);
   }

   public void setInputParameters(String inputParameters) {
      this._jaxbObj.setInputParameters(inputParameters);
   }

   public void setIntercept(Double intercept) {
      this._jaxbObj.setIntercept(intercept);
   }

   public void setLatitudeStep(Double step) {
      this._jaxbObj.setLatitudeStep(step);
   }

   public void setLatitudeUnits(String units) {
      this._jaxbObj.setLatitudeUnits(units);
   }

   public void setLongitudeStep(Double step) {
      this._jaxbObj.setLongitudeStep(step);
   }

   public void setLongitudeUnits(String units) {
      this._jaxbObj.setLongitudeUnits(units);
   }

   public void setName(String name) {
      this._jaxbObj.setName(name);
   }

   public void setNumberOfColumns(Long numberOfColumns) {
      if (numberOfColumns != null) {
         this._jaxbObj.setNumberOfColumns(BigInteger.valueOf(numberOfColumns));
      } else {
         this._jaxbObj.setNumberOfColumns(null);
      }
   }

   public void setNumberOfLines(Long numberOfLines) {
      if (numberOfLines != null) {
         this._jaxbObj.setNumberOfLines(BigInteger.valueOf(numberOfLines));
      } else {
         this._jaxbObj.setNumberOfLines(null);
      }
   }

   public void setObservationMode(String observationMode) {
      this._jaxbObj.setObservationMode(observationMode);
   }

   public void setOfficialName(String name) {
      this._jaxbObj.setOfficialName(name);
   }

   public void setPass(int pass) {
      this._jaxbObj.setPass(BigInteger.valueOf(pass));
   }

   public void setPassType(DataPass passType) {
      this._jaxbObj.setPassType(DataPassType.fromValue(passType.toString()));
   }

   public void setProductType(String productType) {
      this._jaxbObj.setProductType(productType);
   }

   public void setProjection(String projection) {
      this._jaxbObj.setProjection(projection);
   }

   public void setReplace(String name) {
      this._jaxbObj.setReplace(name);
   }

   public void setRevolution(int revolution) {
      this._jaxbObj.setRevolution(BigInteger.valueOf(revolution));
   }

   public void setScaling(String scaling) {
      this._jaxbObj.setScaling(scaling);
   }

   public void setScalingEquation(String scalingEquation) {
      this._jaxbObj.setScalingEquation(scalingEquation);
   }

   public void setSlope(Double slope) {
      this._jaxbObj.setSlope(slope);
   }

   public void setStoreAsGroup(boolean flag) {
      if (this._jaxbObj.getGroup() == null) {
         this._jaxbObj.setGroup(new GranuleType.Group());
      }
      this._jaxbObj.getGroup().setStoreAsGroup(flag);
   }

   public void setSurfaceType(String surfaceType) {
      this._jaxbObj.setSurfaceType(surfaceType);
   }

   public void setSwath(Swath swath) {
      if (swath == null)
         return;
      Swath temp = swath;
      if (!(temp.getImplObj() instanceof SwathType)) {
         temp = new SwathJaxb(swath);
      }
      SpatialDomainType sdt = this._jaxbObj.getSpatialCoverage();
      if (sdt == null) {
         sdt = new SpatialDomainType();
         this._jaxbObj.setSpatialCoverage(sdt);
      }
      sdt.setSwath((SwathType) temp.getImplObj());
      temp.setOwner(this);
      swath = temp;
   }

   public void setTemporalCoverageStartTime(Date startTime) {
      if (startTime == null)
         return;
      if (this._jaxbObj.getTemporalCoverage() == null) {
         this._jaxbObj.setTemporalCoverage(new TimeStampType());
      }
      this._jaxbObj.getTemporalCoverage().setStart(
            BigInteger.valueOf(startTime.getTime()));
   }

   public void setTemporalCoverageStartTime(long startTime) {
      if (this._jaxbObj.getTemporalCoverage() == null) {
         this._jaxbObj.setTemporalCoverage(new TimeStampType());
      }
      this._jaxbObj.getTemporalCoverage().setStart(
            BigInteger.valueOf(startTime));
   }

   public void setTemporalCoverageStopTime(Date stopTime) {
      if (stopTime == null)
         return;
      if (this._jaxbObj.getTemporalCoverage() == null) {
         this._jaxbObj.setTemporalCoverage(new TimeStampType());
      }
      this._jaxbObj.getTemporalCoverage().setStop(
            BigInteger.valueOf(stopTime.getTime()));
   }

   public void setTemporalCoverageStopTime(long stopTime) {
      if (this._jaxbObj.getTemporalCoverage() == null) {
         this._jaxbObj.setTemporalCoverage(new TimeStampType());
      }
      this._jaxbObj.getTemporalCoverage().setStop(BigInteger.valueOf(stopTime));
   }

   public void setVersion(String version) {
      if (version != null)
         this._jaxbObj.setVersion(version);
   }

}

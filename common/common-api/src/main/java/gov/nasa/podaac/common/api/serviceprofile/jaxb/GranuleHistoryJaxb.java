/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.GranuleHistoryType;
import gov.nasa.podaac.common.api.serviceprofile.GranuleHistory;

import java.math.BigInteger;
import java.util.Date;

/**
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: GranuleHistoryJaxb.java 1239 2008-05-30 07:04:36Z thuang $
 */
public class GranuleHistoryJaxb extends AccessorBase implements GranuleHistory {

   private GranuleHistoryType _jaxbObj;

   public GranuleHistoryJaxb() {
      this._jaxbObj = new GranuleHistoryType();
   }

   public GranuleHistoryJaxb(GranuleHistory history) {
      this._jaxbObj = new GranuleHistoryType();
      this.setVersion(history.getVersion());
      this.setCreateDate(history.getCreateDate());
      this.setLastRevisionDate(history.getLastRevisionDate());
      this.setRevisionHistory(history.getRevisionHistory());
   }

   public GranuleHistoryJaxb(GranuleHistoryType jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public String getVersion() {
      return this._jaxbObj.getVersion();
   }

   public void setVersion(String version) {
      this._jaxbObj.setVersion(version);
   }

   public Date getCreateDate() {
      return new Date(this._jaxbObj.getCreateDate().longValue());
   }

   public void setCreateDate(Date date) {
      if (date == null) return;
      this._jaxbObj.setCreateDate(BigInteger.valueOf(date.getTime()));
   }

   public void setCreateDate(long date) {
      this._jaxbObj.setCreateDate(BigInteger.valueOf(date));
   }

   public Date getLastRevisionDate() {
      return new Date(this._jaxbObj.getLastRevisionDate().longValue());
   }

   public void setLastRevisionDate(Date date) {
      if (date == null) return;
      this._jaxbObj.setLastRevisionDate(BigInteger.valueOf(date.getTime()));
   }

   public void setLastRevisionDate(long date) {
      this._jaxbObj.setLastRevisionDate(BigInteger.valueOf(date));
   }

   public String getRevisionHistory() {
      return this._jaxbObj.getRevisionHistory();
   }

   public void setRevisionHistory(String history) {
      this._jaxbObj.setRevisionHistory(history);
   }

   public Object getImplObj() {
      return this._jaxbObj;
   }
}

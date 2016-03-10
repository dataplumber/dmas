/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.podaac.common.api.transformer.lod;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: CsvParser.java 1234 2008-05-30 04:45:50Z thuang $
 */
public class CsvParser {

   private static Log _logger = LogFactory.getLog(CsvParser.class);

   private BufferedReader _bufferedReader;
   private String _nextLine;

   public CsvParser(Reader reader) throws Exception {
      if (reader == null) {
         throw new Exception("Reader cannot be null.");
      }

      _bufferedReader = new BufferedReader(reader);
      _nextLine = null;
   }

   public boolean hasNext() {
      boolean result = (_nextLine != null);
      if (!result) {
         result = _readNextLine();
      }

      return result;
   }

   public List<String> next() {
      List<String> values = null;

      if (this.hasNext()) {
         values = _parseLine(_nextLine);
         _nextLine = null;
      }

      return values;
   }

   private boolean _readNextLine() {
      boolean result = false;
      try {
         _nextLine = _bufferedReader.readLine();
         if (_nextLine != null) {
            result = true;
         }
      } catch (Exception exception) {
      }

      return result;
   }

   private List<String> _parseLine(String line) {
      String[] values = line.split(",");

      LinkedList<String> formattedValues = new LinkedList<String>();
      StringBuilder concatenatedValue = new StringBuilder();
      boolean quoteStarted = false;
      for (String value : values) {
         if (!quoteStarted) {
            if (value.startsWith("\"")) {
               quoteStarted = true;

               concatenatedValue.append(value.substring(1) + ",");
            } else {
               formattedValues.add(value);
            }
         } else {
            if (value.endsWith("\"")) {
               quoteStarted = false;

               concatenatedValue.append(value
                     .substring(0, (value.length() - 1)));
               formattedValues.add(concatenatedValue.toString());
               concatenatedValue.delete(0, concatenatedValue.length());
            } else {
               concatenatedValue.append(value + ",");
            }
         }
      }

      if (CsvParser._logger.isTraceEnabled()) {
         StringBuffer msg = new StringBuffer();
         for (String value : formattedValues) {
            msg.append(value + ", ");
         }
         CsvParser._logger.trace(msg);
      }

      return formattedValues;
   }
}

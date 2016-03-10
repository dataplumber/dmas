/***************************************************************************
*
* Copyright 2012, by the California Institute of Technology. ALL
* RIGHTS RESERVED. United States Government Sponsorship acknowledged.
* Any commercial use must be negotiated with the Office of Technology
* Transfer at the California Institute of Technology.
*
* @version $Id: ZipUtility.java 10011 2012-05-21 18:33:07Z qchau $
*
****************************************************************************/

package gov.nasa.podaac.common.api.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Enumeration;
import com.googlecode.compress_j2me.lzc.LZCInputStream;

// This class is a utility class to handle compress/decompressing of content using ZIP format.
// This initial version only handle decompressing function.
 
// This class is copied from GzipUtility class and modified to fit.

public class UnixZipUtility {
   private static final int m_BUFFER_SIZE = 1024;

   protected UnixZipUtility() {
   }

   private static void copyInputStream(InputStream in, OutputStream out) throws IOException
   {
//System.out.println("ZipUtility::copyInputStream():Entering");
    byte[] buffer = new byte[m_BUFFER_SIZE];
    int len;

    while((len = in.read(buffer)) >= 0) {
//System.out.println("ZipUtility::copyInputStream():len " + len);
      out.write(buffer, 0, len);
    }

    in.close();
    out.close();
//System.out.println("ZipUtility::copyInputStream():Leaving");
  }
   
   public static void decompress(FileInputStream i_inputFilename, FileOutputStream i_outputFilename) throws IOException {
		// Note:
		//
		//    This function assumes the input file only contains one file.  If the input file
		//    contains more than one entries, only the last entry is uncompressed to file named in variable i_outputFilename.
		
		//System.out.println("ZipUtility::decompress():Entering");
		LZCInputStream.uncompress(i_inputFilename, i_outputFilename);


   }
   
   public static void decompress(String i_inputFilename,
                                 String i_outputFilename) throws IOException {
	   decompress(new FileInputStream(i_inputFilename), new FileOutputStream(i_outputFilename));
   }
}

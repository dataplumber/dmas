/*
 * Created on Jun 11, 2010
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gov.nasa.podaac.common.crawler.spider.provider;

import gov.nasa.podaac.common.api.file.FileProduct;

public interface ProcessFileProduct
{
   final public static ProcessFileProduct deflt = new ProductSet();
   
   public void process (FileProduct fp);
}

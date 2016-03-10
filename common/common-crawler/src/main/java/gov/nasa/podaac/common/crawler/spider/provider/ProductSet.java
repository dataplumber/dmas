/*
 * Created on Jun 11, 2010
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gov.nasa.podaac.common.crawler.spider.provider;

import gov.nasa.podaac.common.api.file.FileProduct;

import java.util.HashSet;
import java.util.Set;

final public class ProductSet implements ProcessFileProduct
{
   final private Set<FileProduct> theList;
   
   ProductSet ()
   {
      this.theList = new HashSet<FileProduct>();
   }
   
   ProductSet (int expected)
   {
      this.theList = new HashSet<FileProduct>(expected);
   }
   
   public void clear() { this.theList.clear(); }
   
   public void process(FileProduct fp)
   {
      synchronized (this.theList) { this.theList.add (fp); }
   }

   public Set<FileProduct> getShallowCopy()
   {
      Set<FileProduct> result = new HashSet<FileProduct>();
      
      synchronized (this.theList) { result.addAll (this.theList); }
      return result;
   }
}

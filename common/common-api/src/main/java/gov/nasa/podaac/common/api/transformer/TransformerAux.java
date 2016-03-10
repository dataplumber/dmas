package gov.nasa.podaac.common.api.transformer;

import gov.nasa.podaac.common.api.serviceprofile.Common.MessageLevel;

public interface TransformerAux {
   
   String getContributorEmailAddress();
   
   MessageLevel getContributorMessageLevel();
    
   String getRootURI();

   void setContributorEmailAddress(String emailAddress);

   void setContributorMessageLevel(MessageLevel messageLevel);
   
   void setRootURI(String rootURI);
}

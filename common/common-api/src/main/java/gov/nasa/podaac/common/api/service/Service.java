package gov.nasa.podaac.common.api.service;

public interface Service {

   void init();
   
   void setName(String name);
   
   String getName();
   
   void start();
   
   void stop();
   
}

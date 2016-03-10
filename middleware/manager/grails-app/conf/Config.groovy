// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]
// The default codec used to encode data with ${}
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
grails.converters.encoding="UTF-8"

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.serverURL = "http://www.changeme.com"
    }
    development {
        grails.serverURL = "http://localhost:8080/${appName}"
    }
    test {
        grails.serverURL = "http://localhost:8080/${appName}"
    }

}

// log4j configuration
log4j = {
    appenders {
       console name:'stdoutLogger', 
          layout: pattern(
             conversionPattern: '%d{ABSOLUTE} %-5p [%c{1}:%L] {%t} %m%n')

       appender new org.apache.log4j.DailyRollingFileAppender(
          name:'fileLogger',
          fileName: 'manager.log',
          layout: pattern(
             conversionPattern: '%d %-5p [%c{1}:%L] {%t} %m%n'),
          datePattern: "'.'yyyy-MM-dd"
       )
    
       appender new org.apache.log4j.DailyRollingFileAppender(
          name:'stackTraceLogger',
          fileName: 'manager.stacktrace',
          layout: pattern(
             conversionPattern: '%d %-5p [%c{1}:%L] {%t} %m%n'),
          datePattern: "'.'yyyy-MM-dd"
       )
    }
   
   root {
      error 'stdoutLogger', 'fileLogger'
      additivity = true
   }
    
   error stackTraceLogger: "StackTrace"
   
   debug 'grails.app',
         'org.quartz',
         'gov.nasa.podaac.inventory.core',
         'gov.nasa.podaac.common.api.zookeeper',
         'gov.nasa.podaac.security.client'
         
   error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
         'org.codehaus.groovy.grails.web.pages', //  GSP
         'org.codehaus.groovy.grails.web.sitemesh', //  layouts
         'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
         'org.codehaus.groovy.grails.web.mapping', // URL mapping
         'org.codehaus.groovy.grails.commons', // core / classloading
         'org.codehaus.groovy.grails.plugins', // plugins
         'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
         'org.springframework',
         'org.hibernate'
         
   warn 'org.mortbay.log'
}

/*
// configuration for JMS settings
horizon_naming_context = "org.jnp.interfaces.NamingContextFactory"
horizon_naming_factory = "org.jboss.naming:org.jnp.interfaces"
*/

// configuration for Grails Spring Security plugin
grails.plugins.springsecurity.portMapper.httpPort = (System.getProperty('server.port') ?: 8080).toInteger()
grails.plugins.springsecurity.portMapper.httpsPort = (System.getProperty('server.port.https') ?: 8443).toInteger()
grails.plugins.springsecurity.secureChannel.definition = [
   '/**': 'REQUIRES_SECURE_CHANNEL'
]

environments {
   local {
      //horizon_provider_url = "jnp://localhost:1099"
      horizon_sigevent_url = "http://lanina.jpl.nasa.gov:8100/sigevent"
      horizon_zookeeper_url = "lanina.jpl.nasa.gov:2181"
      horizon_zookeeper_ws_url = "lanina.jpl.nasa.gov:9998"
      horizon_discovery_url = "http://lanina.jpl.nasa.gov:8983/solr.war"
      horizon_dataset_update_federation = "podaacDev"
      horizon_dataset_update_purge_rate = 1
      horizon_dataset_update_sigevent = [[type: "INFO", purgeRate: 1], [type: "WARN", purgeRate: 1], [type: "ERROR", purgeRate: 1]]
      //SecurityServiceInfo
      gov.nasa.podaac.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.security.port = 9197
      gov.nasa.podaac.security.realm = "PODAAC-MANAGER"
      
      //Host and port of inventory service
      gov.nasa.podaac.manager.inventory.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.manager.inventory.port = 9192
      gov.nasa.podaac.manager.inventory.user = "thuang"
      gov.nasa.podaac.manager.inventory.pass = "password"
   }

   oracle {
      //horizon_provider_url = "jnp://localhost:1099"
      horizon_sigevent_url = "http://lanina.jpl.nasa.gov:8100/sigevent"
      horizon_zookeeper_url = "lanina.jpl.nasa.gov:2181"
      horizon_zookeeper_ws_url = "lanina.jpl.nasa.gov:9998"
      horizon_discovery_url = "http://lanina.jpl.nasa.gov:8983/solr.war"
      horizon_dataset_update_federation = "podaacDev"
      horizon_dataset_update_purge_rate = 1
      horizon_dataset_update_sigevent = [[type: "INFO", purgeRate: 1], [type: "WARN", purgeRate: 1], [type: "ERROR", purgeRate: 1]]
      //SecurityServiceInfo
      gov.nasa.podaac.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.security.port = 9197
      gov.nasa.podaac.security.realm = "PODAAC-MANAGER"
      
      //Host and port of inventory service
      gov.nasa.podaac.manager.inventory.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.manager.inventory.port = 9192
      gov.nasa.podaac.manager.inventory.user = "thuang"
      gov.nasa.podaac.manager.inventory.pass = "password"
   }

   development {
      //horizon_provider_url = "jnp://localhost:1099"
      horizon_sigevent_url = "http://localhost:8100/sigevent"
      horizon_zookeeper_url = "localhost:2181"
      horizon_zookeeper_ws_url = "localhost:9998"
      horizon_discovery_url = "http://localhost:8983/solr.war"
      horizon_dataset_update_federation = "podaacDev"
      horizon_dataset_update_purge_rate = 1
      horizon_dataset_update_sigevent = [[type: "INFO", purgeRate: 1], [type: "WARN", purgeRate: 1], [type: "ERROR", purgeRate: 1]]
      //SecurityServiceInfo
      gov.nasa.podaac.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.security.port = 9197
      gov.nasa.podaac.security.realm = "PODAAC-MANAGER"
      
      //Host and port of inventory service
      gov.nasa.podaac.manager.inventory.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.manager.inventory.port = 9192
      gov.nasa.podaac.manager.inventory.user = "thuang"
      gov.nasa.podaac.manager.inventory.pass = "password"
   }

   test {
      //horizon_provider_url = "jnp://lanina.jpl.nasa.gov:1099"
      horizon_sigevent_url = "http://lanina.jpl.nasa.gov:8100/sigevent"
      horizon_inventory_url = "http://lanina.jpl.nasa.gov:9191"
      horizon_zookeeper_url = "lanina.jpl.nasa.gov:2181"
      horizon_zookeeper_ws_url = "lanina.jpl.nasa.gov:9998"
      horizon_discovery_url = "http://lanina.jpl.nasa.gov:8983/solr.war"
      horizon_dataset_update_federation = "podaacTest"
      horizon_dataset_update_purge_rate = 1
      horizon_dataset_update_sigevent = [[type: "INFO", purgeRate: 1], [type: "WARN", purgeRate: 1], [type: "ERROR", purgeRate: 1]]
      //SecurityServiceInfo
      gov.nasa.podaac.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.security.port = 9197
      gov.nasa.podaac.security.realm = "PODAAC-MANAGER"
      
      //Host and port of inventory service
      gov.nasa.podaac.manager.inventory.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.manager.inventory.port = 9192
      gov.nasa.podaac.manager.inventory.user = "thuang"
      gov.nasa.podaac.manager.inventory.pass = "password"
   }
   
   testing {
      //horizon_provider_url = "jnp://lanina.jpl.nasa.gov:1099"
      horizon_sigevent_url = "http://lanina.jpl.nasa.gov:8100/sigevent"
      horizon_zookeeper_url = "lanina.jpl.nasa.gov:2181"
      horizon_zookeeper_ws_url = "lanina.jpl.nasa.gov:9998"
      horizon_discovery_url = "http://lanina.jpl.nasa.gov:8983/solr.war"
      horizon_dataset_update_federation = "podaacTest"
      horizon_dataset_update_purge_rate = 1
      horizon_dataset_update_sigevent = [[type: "INFO", purgeRate: 1], [type: "WARN", purgeRate: 1], [type: "ERROR", purgeRate: 1]]
      //SecurityServiceInfo
      gov.nasa.podaac.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.security.port = 9197
      gov.nasa.podaac.security.realm = "PODAAC-MANAGER"
      
      //Host and port of inventory service
      gov.nasa.podaac.manager.inventory.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.manager.inventory.port = 9192
      gov.nasa.podaac.manager.inventory.user = "thuang"
      gov.nasa.podaac.manager.inventory.pass = "password"
   }

   production {
      //horizon_provider_url = "jnp://prawn.jpl.nasa.gov:1099"
      horizon_sigevent_url = "http://lanina.jpl.nasa.gov:8100/sigevent"
      horizon_zookeeper_url = "lanina.jpl.nasa.gov:2181"
      horizon_zookeeper_ws_url = "lanina.jpl.nasa.gov:9998"
      horizon_discovery_url = "http://lanina.jpl.nasa.gov:8983/solr.war"
      horizon_dataset_update_federation = "podaacOps"
      horizon_dataset_update_purge_rate = 4320
      horizon_dataset_update_sigevent = [[type: "INFO", purgeRate: 2], [type: "WARN", purgeRate: 2], [type: "ERROR", purgeRate: 30]]
      //SecurityServiceInfo
      gov.nasa.podaac.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.security.port = 9197
      gov.nasa.podaac.security.realm = "PODAAC-MANAGER"
      
      //Host and port of inventory service
      gov.nasa.podaac.manager.inventory.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.manager.inventory.port = 9192
      gov.nasa.podaac.manager.inventory.user = "thuang"
      gov.nasa.podaac.manager.inventory.pass = "password"
   }
   
   operation {
      //horizon_provider_url = "jnp://prawn.jpl.nasa.gov:1099"
      horizon_sigevent_url = "http://lanina.jpl.nasa.gov:8100/sigevent"
      horizon_zookeeper_url = "lanina.jpl.nasa.gov:2181"
      horizon_zookeeper_ws_url = "lanina.jpl.nasa.gov:9998"
      horizon_discovery_url = "http://lanina.jpl.nasa.gov:8983/solr.war"
      horizon_dataset_update_federation = "podaacOps"
      horizon_dataset_update_purge_rate = 4320
      horizon_dataset_update_sigevent = [[type: "INFO", purgeRate: 2], [type: "WARN", purgeRate: 2], [type: "ERROR", purgeRate: 30]]
      //SecurityServiceInfo
      gov.nasa.podaac.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.security.port = 9197
      gov.nasa.podaac.security.realm = "PODAAC-MANAGER"
      
      //Host and port of inventory service
      gov.nasa.podaac.manager.inventory.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.manager.inventory.port = 9192
      gov.nasa.podaac.manager.inventory.user = "thuang"
      gov.nasa.podaac.manager.inventory.pass = "password"
   }
}

//System.setProperty("com.mchange.v2.c3p0.management.ManagementCoordinator", "com.mchange.v2.c3p0.management.NullManagementCoordinator")



     

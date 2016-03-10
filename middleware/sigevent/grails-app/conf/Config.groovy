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
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
      xml: ['text/xml', 'application/xml'],
      text: 'text-plain',
      js: 'text/javascript',
      rss: 'application/rss+xml',
      atom: 'application/atom+xml',
      css: 'text/css',
      csv: 'text/csv',
      all: '*/*',
      json: ['application/json', 'text/json'],
      form: 'application/x-www-form-urlencoded',
      multipartForm: 'multipart/form-data'
]
// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

// set per-environment serverURL stem for creating absolute links
// environments {
//    production {
//        grails.serverURL = "http://www.changeme.com"
//    }
//}

// log4j configuration
log4j = {
      appenders {
         console name:'stdoutLogger', 
            layout: pattern(
               conversionPattern: '%d{ABSOLUTE} %-5p [%c{1}:%L] {%t} %m%n')

         appender new org.apache.log4j.DailyRollingFileAppender(
            name:'fileLogger',
            fileName: 'sigevent.log',
            layout: pattern(
               conversionPattern: '%d %-5p [%c{1}:%L] {%t} %m%n'),
            datePattern: "'.'yyyy-MM-dd"
         )
      
         appender new org.apache.log4j.DailyRollingFileAppender(
            name:'stackTraceLogger',
            fileName: 'sigevent.stacktrace',
            layout: pattern(
               conversionPattern: '%d %-5p [%c{1}:%L] {%t} %m%n'),
            datePattern: "'.'yyyy-MM-dd"
         )
      }
     
     root {
        info 'stdoutLogger', 'fileLogger'
        additivity = true
     }
     
     error stackTraceLogger: "StackTrace"
     
     info 'grails.app',
           'org.quartz'
           
     error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'org.apache.tomcat'
           
     warn 'org.mortbay.log'
}

// configuration for JMS settings
// horizon_naming_context = "org.jnp.interfaces.NamingContextFactory"
// horizon_naming_factory = "org.jboss.naming:org.jnp.interfaces"

environments {
   local {
      horizon_email_host = 'smtp.jpl.nasa.gov'
      horizon_provider_url = "jnp://localhost:1099"
      horizon_jms_username = "thuang"
      horizon_jms_password = "password"
      sigevent_api_data_uri = "http://localhost:8100/sigevent/events/data"
      sigevent_twitter_username = "username"
      sigevent_twitter_password = 'password'
      sigevent_twitter_characters_limit = 140
      //SecurityServiceInfo
      gov.nasa.podaac.security.service.enable = true
      gov.nasa.podaac.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.security.port = 9197
      gov.nasa.podaac.security.realm = "PODAAC-SIGEVENT"
      gov.nasa.podaac.security.role="ADMIN"
   }

   oracle {
      horizon_email_host = 'localhost'
      horizon_provider_url = "jnp://localhost:1099"
      horizon_jms_username = "thuang"
      horizon_jms_password = "password"
      sigevent_api_data_uri = "http://localhost:8100/sigevent/events/data"
      sigevent_twitter_username = "username"
      sigevent_twitter_password = 'password'
      sigevent_twitter_characters_limit = 140
      //SecurityServiceInfo
      gov.nasa.podaac.security.service.enable = true
      gov.nasa.podaac.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.security.port = 9197
      gov.nasa.podaac.security.realm = "PODAAC-SIGEVENT"
      gov.nasa.podaac.security.role="ADMIN"
   }

   development {
      horizon_email_host = 'smtp.jpl.nasa.gov'
      horizon_provider_url = "jnp://localhost:1099"
      horizon_jms_username = "thuang"
      horizon_jms_password = "password"
      sigevent_api_data_uri = "http://localhost:8100/sigevent/events/data"
      sigevent_twitter_username = "sigeventdev"
      sigevent_twitter_password = 'password'
      sigevent_twitter_characters_limit = 140
      //SecurityServiceInfo
      gov.nasa.podaac.security.service.enable = true
      gov.nasa.podaac.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.security.port = 9197
      gov.nasa.podaac.security.realm = "PODAAC-SIGEVENT"
      gov.nasa.podaac.security.role="ADMIN"
   }

   test {
      horizon_email_host = 'smtp.jpl.nasa.gov'
      horizon_provider_url = "jnp://lanina.jpl.nasa.gov:1099"
      horizon_jms_username = "thuang"
      horizon_jms_password = "password"
      sigevent_api_data_uri = "http://lanina.jpl.nasa.gov:8100/sigevent/events/data"
      sigevent_twitter_username = "username"
      sigevent_twitter_password = 'password'
      sigevent_twitter_characters_limit = 140
      //SecurityServiceInfo
      gov.nasa.podaac.security.service.enable = true
      gov.nasa.podaac.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.security.port = 9197
      gov.nasa.podaac.security.realm = "PODAAC-SIGEVENT"
      gov.nasa.podaac.security.role="ADMIN"
   }
   
   testing {
      horizon_email_host = 'smtp.jpl.nasa.gov'
      horizon_provider_url = "jnp://lanina.jpl.nasa.gov:1099"
      horizon_jms_username = "thuang"
      horizon_jms_password = "password"
      sigevent_api_data_uri = "http://lanina.jpl.nasa.gov:8100/sigevent/events/data"
      sigevent_twitter_username = "username"
      sigevent_twitter_password = 'password'
      sigevent_twitter_characters_limit = 140
      //SecurityServiceInfo
      gov.nasa.podaac.security.service.enable = true
      gov.nasa.podaac.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.security.port = 9197
      gov.nasa.podaac.security.realm = "PODAAC-SIGEVENT"
      gov.nasa.podaac.security.role="ADMIN"
   }

   production {
      horizon_email_host = 'smtp.jpl.nasa.gov'
      horizon_provider_url = "jnp://prawn.jpl.nasa.gov:1099"
      horizon_jms_username = "thuang"
      horizon_jms_password = "password"
      sigevent_api_data_uri = "http://prawn.jpl.nasa.gov:8100/sigevent/events/data"
      sigevent_twitter_username = "username"
      sigevent_twitter_password = 'password'
      sigevent_twitter_characters_limit = 140
      //SecurityServiceInfo
      gov.nasa.podaac.security.service.enable = true
      gov.nasa.podaac.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.security.port = 9197
      gov.nasa.podaac.security.realm = "PODAAC-SIGEVENT"
      gov.nasa.podaac.security.role="ADMIN"
   }
   
   operation {
      horizon_email_host = 'smtp.jpl.nasa.gov'
      horizon_provider_url = "jnp://prawn.jpl.nasa.gov:1099"
      horizon_jms_username = "thuang"
      horizon_jms_password = "password"
      sigevent_api_data_uri = "http://prawn.jpl.nasa.gov:8100/sigevent/events/data"
      sigevent_twitter_username = "username"
      sigevent_twitter_password = 'password'
      sigevent_twitter_characters_limit = 140
      //SecurityServiceInfo
      gov.nasa.podaac.security.service.enable = true
      gov.nasa.podaac.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.podaac.security.port = 9197
      gov.nasa.podaac.security.realm = "PODAAC-SIGEVENT"
      gov.nasa.podaac.security.role="ADMIN"
   }
}

//System.setProperty("com.mchange.v2.c3p0.management.ManagementCoordinator", "com.mchange.v2.c3p0.management.NullManagementCoordinator")

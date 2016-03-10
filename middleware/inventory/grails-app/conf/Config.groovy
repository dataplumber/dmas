// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = false // enables the parsing of file extensions from URLs into the request format
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
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
grails.converters.xml.pretty.print = false 
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
grails.exceptionresolver.params.exclude = ['password']


//Security Service
gov.nasa.podaac.security.host="https://localhost"
gov.nasa.podaac.security.port=9197
gov.nasa.podaac.security.realm="PODAAC-INVENTORY"
//no role information needed. Authentication is enough.

//If you want to enable roles management, uncomment the line 
//below and fill in the ROLES that are authorized to update the inventory system 
//gov.nasa.podaac.security.roles=['DE','ADMIN','WRITE_ALL']


//cache user logins for faster access. Recomended to keep this as true, 
//other wise every request needing auth will query the security service.
gov.nasa.podaac.auth.cache.useCache = true;
//hours to cache a user 
//(once successfully cached, requests won't ping the security server until the time limit is up). 0 = infinite cache.
gov.nasa.podaac.auth.cache.timeLimit = 2

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
    // Example of changing the log pattern for the default console
    // appender:
    //
    appenders {
       console name:'stdoutLogger', 
          layout: pattern(
             conversionPattern: '%d{ABSOLUTE} %-5p [%c{1}:%L] {%t} %m%n')

       appender new org.apache.log4j.DailyRollingFileAppender(
          name:'fileLogger',
          fileName: 'inventory.log',
          layout: pattern(
             conversionPattern: '%d %-5p [%c{1}:%L] {%t} %m%n'),
          datePattern: "'.'yyyy-MM-dd"
       )
    
       appender new org.apache.log4j.DailyRollingFileAppender(
          name:'stackTraceLogger',
          fileName: 'inventory.stacktrace',
          layout: pattern(
             conversionPattern: '%d %-5p [%c{1}:%L] {%t} %m%n'),
          datePattern: "'.'yyyy-MM-dd"
       )
    }
    root {
        error 'stdoutLogger', 'fileLogger'
        additivity = true
    }
//    appenders {
//        console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
//    }

    all    'org.codehaus.groovy.grails.web.mapping'
    debug  'grails.app.service.InventoryService',
           'org.codehaus.groovy.grails.web.mapping',
	   'grails.converters.XML',
	   'grails.app.service.AuthenticationService',
	   'grails.app.controller.DatasetController',
	   'grails.app.controller.SipController',
           'grails.app.controller.CollectionController',
	   'grails.app.controller.GranuleController',	
	   'grails.app.controller.ManifestController',
	   'gov.nasa.podaac.inventory.core.InventoryImpl',
           'gov.nasa.podaac.inventory.core.QueryImpl',
	   'gov.nasa.podaac.inventory.core.DatasetMetadataImpl'
    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
}

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

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
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

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password','pass']

//sigeven info
gov.nasa.podaac.security.sig.category="TOOL_SECURITY"
gov.nasa.podaac.security.sig.url="http://lanina:8100/sigevent/"


//The ports below must be defined in both this config file and in "BuildConfig.groovy" in this same directory.
//You'll see an corresponding http/https port description in BuildConfig.groovy
//This lets the scurity plugin know SSL must be used, and which ports to map https -> https
grails.plugins.springsecurity.portMapper.httpPort=9196
grails.plugins.springsecurity.portMapper.httpsPort=9197
grails.plugins.springsecurity.secureChannel.definition = [
	'/**':         'REQUIRES_SECURE_CHANNEL'
	//<port-mapping http="9080" https="9443" />
  ]


security{
	token{
		/*
		 * A job exists to expire tokens after a certain amount of time, which is set in the "Realm".
		 */
		jobStartDelay =5 //seconds before the jobs kickoff
		jobInterval = 900 // second between job. 900 = 15 minutes.
	}
	caching{
		/*
		 * a job exists to purge the cache every so often. This keeps the in memory cache from growing too large.
		 * 
		 */
		jobStartDelay =10 //seconds before the jobs kickoff
		jobInterval = 60 //seconds between the job
		
		/*
		 * Cache specific configuration
		 * The cache is used so we don't keep hammering LDAP/Oracle for consecutive requests for authentication.
		 * It's also used to track requests from ipaddress/realm to lock accounts/prevent denial of service requests.
		 * The parameters are as follows. 
		 */
		cacheTimeLimit = 120 // Time, in minutes, that a cached entry is usable for. 
		misses = 10 // Number of consecutive misses before a user is locked from authenticating
		lockTimeLimit = 15 // Amount of time, in minutes, that a entry is locked from authentication once consecutive misses are hit(Can't authenticate via the service)
	}
	plugins {
		LDAP{
			host = 'ldap://ldap.jpl.nasa.gov:636'
			searchDn = 'ou=personnel,dc=dir,dc=jpl,dc=nasa,dc=gov'
			//group = 'podaac.j1slx.dev'
		}
		DATABASE{
			//no database specific config
		}
//		OTHER{
//		//future plugins/A&A mechanisms (user reg?))	
//		}
	}
}

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.serverURL = "http://www.changeme.com"
    }
    development {
		logFile="sapi.log"
        //grails.serverURL = "http://localhost:8080/${appName}"
    }
	local {
		logFile="sapi.log"
		//grails.serverURL = "http://localhost:8080/${appName}"
	}
	podaacdev{
		logFile="sapi.log"
		//grails.serverURL = "http://localhost:8080/${appName}"
	}
    test {
		logFile="sapi.log"
        grails.serverURL = "http://localhost:8080/${appName}"
    }
	local {
		logFile="sapi.log"
		grails.serverURL = "http://localhost:8080/${appName}"
	}
	mac{
		logFile="sapi.log"
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
          fileName: logFile,
          layout: pattern(
             conversionPattern: '%d %-5p [%c{1}:%L] {%t} %m%n'),
          datePattern: "'.'yyyy-MM-dd"
       )
    
       appender new org.apache.log4j.DailyRollingFileAppender(
          name:'stackTraceLogger',
          fileName: "${logFile}.st",
          layout: pattern(
             conversionPattern: '%d %-5p [%c{1}:%L] {%t} %m%n'),
          datePattern: "'.'yyyy-MM-dd"
       )
    }
    root {
        error 'stdoutLogger', 'fileLogger'
        additivity = true
    }
	debug  'gov.nasa.podaac.security.server',
		   'grails.app.controller.gov.nasa.podaac.security.server',
		   'grails.app.controllers.gov.nasa.podaac.security.server',
		   'gov.nasa.podaac.security.server.*',
		   'grails.app.service',
		   'grails.app.services',
		   'grails.app.conf',
		   'grails.app.utils',
		   'gov.nasa.tpds.torchws',
		   'grails.app.bootstrap',
		   'grails.app.job',
		   'grails.app.jobs',
		   'grails.app.task',
		   'grails.app.controller',
		   'grails.app.controllers',
		   'grails.app.filter',
		   'grails.app.filters'
    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           //'org.hibernate',
		   'org.hibernate.tool.hbm2ddl',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
}

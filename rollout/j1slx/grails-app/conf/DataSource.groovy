dataSource {
	pooled = true
	driverClassName = "oracle.jdbc.driver.OracleDriver"
	url = "jdbc:oracle:thin:@seadb.jpl.nasa.gov:1526:DAACDEV"
	dialect = "org.hibernate.dialect.Oracle10gDialect"
 }
 
 hibernate {
	hibernate.dialect = "org.hibernate.dialect.Oracle10gDialect"
	cache.use_second_level_cache = false
	cache.use_query_cache = false
	cache.use_minimal_puts = false
	cache.provider_class = 'org.hibernate.cache.NoCacheProvider'
        naming_strategy = gov.nasa.podaac.j1slx.CustomNamingStrategy

 }
 

//dataSource {
//    pooled = true
//    driverClassName = "org.hsqldb.jdbcDriver"
//    username = "sa"
//    password = ""
//}
//hibernate {
//    cache.use_second_level_cache = true
//    cache.use_query_cache = true
//    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
//}

 // environment specific settings
environments {
//    development {
//        dataSource {
//            dbCreate = "create-drop" // one of 'create', 'create-drop','update'
//            url = "jdbc:hsqldb:mem:devDB"
//        }
//    }
	development {
		dataSource {
		   pooled = true
		   dbCreate = "create-drop"
		   driverClassName = "oracle.jdbc.driver.OracleDriver"
		   url = "jdbc:oracle:thin:@seadb.jpl.nasa.gov:1526:DAACDEV"
		   dialect = "org.hibernate.dialect.Oracle10gDialect"
		   username = "gangl"
		   password = 'password'
  
		}
	 }

    podaacdev {
                dataSource {
                   pooled = true
                   dbCreate = "update"
                   driverClassName = "oracle.jdbc.driver.OracleDriver"
                   url = "jdbc:oracle:thin:@seadb.jpl.nasa.gov:1526:DAACDEV"
                   dialect = "org.hibernate.dialect.Oracle10gDialect"
                   username = "podaac_dev"
                   password = 'password'

                }
         }
  
    testing {
        dataSource {
        	pooled = true
                   dbCreate = "update"
                   driverClassName = "oracle.jdbc.driver.OracleDriver"
                   url = "jdbc:oracle:thin:@seasql.jpl.nasa.gov:1526:DAACTEST"
                   dialect = "org.hibernate.dialect.Oracle10gDialect"
                   username = "jason"
                   password = 'password'
	}
    }
    production {
        dataSource {
            dbCreate = "update"
            url = "jdbc:hsqldb:file:prodDb;shutdown=true"
        }
    }
}


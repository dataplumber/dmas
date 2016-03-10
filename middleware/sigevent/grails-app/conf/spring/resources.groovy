import org.codehaus.groovy.grails.commons.ConfigurationHolder
//import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter
//import org.springframework.jndi.JndiObjectFactoryBean
import com.mchange.v2.c3p0.ComboPooledDataSource

beans = {

   def config = ConfigurationHolder.config

   mailSender(org.springframework.mail.javamail.JavaMailSenderImpl) {
      host = config.horizon_email_host
   }

   mailMessage(org.springframework.mail.SimpleMailMessage) {
      from = 'do_not_reply@podaac.jpl.nasa.gov'
   }
   /*
   globalConnectionFactory(JndiObjectFactoryBean) {
      jndiName = "/ConnectionFactory"
      jndiEnvironment = [
            "java.naming.provider.url": config.horizon_provider_url,
            "java.naming.factory.initial": config.horizon_naming_context,
            "java.naming.factory.url.pkgs": config.horizon_naming_factory
      ]
   }

   jmsConnectionFactory(UserCredentialsConnectionFactoryAdapter) {
      targetConnectionFactory = ref(globalConnectionFactory)
      username = config.horizon_jms_username
      password = config.horizon_jms_password
   }
   */
   if(config.dataSource.url =~ 'oracle') {
      System.setProperty(
         "com.mchange.v2.c3p0.management.ManagementCoordinator",
         "com.mchange.v2.c3p0.management.NullManagementCoordinator")
      dataSource(ComboPooledDataSource) {
         driverClass = 'oracle.jdbc.driver.OracleDriver'
         user = config.dataSource.username
         password = config.dataSource.password
         jdbcUrl = config.dataSource.url
         initialPoolSize = 5
         minPoolSize = 3
         maxPoolSize = 20
         acquireIncrement = 1
         maxIdleTime = 600
         propertyCycle = 60
      }
   }
}

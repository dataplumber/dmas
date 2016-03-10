class SysEmailServiceTests extends GroovyTestCase {

   def sysEmailService

   void testEmail() {
      def email = [
            to: ['thomas.huang@jpl.nasa.gov'],
            subject: 'test message',
            text: 'this message was sent by grails email service.'
      ]
      sysEmailService.notify([email])
   }

}

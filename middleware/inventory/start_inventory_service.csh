#mvn dependency:copy-dependencies
grails -Dinventory.config.file=./inventory.config -Djava.security.egd=file:/dev/urandom  -Dgrails.env=dev -Dserver.port=9191 -Dserver.port.https=9192 -Dserver.host=localhost run-app -https -Dpodaac.service.name=IWS 

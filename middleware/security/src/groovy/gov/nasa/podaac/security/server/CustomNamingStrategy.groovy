package gov.nasa.podaac.security.server

import org.hibernate.cfg.ImprovedNamingStrategy
import org.hibernate.util.StringHelper

class CustomNamingStrategy extends ImprovedNamingStrategy {

    String classToTableName(String className) {
        "SEC_" + StringHelper.unqualify(className)
    }

}

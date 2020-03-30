package org.acme.iam.manager.business;

import org.jboss.logging.Logger;

/**
 * Aggregator
 */
public class Aggregator {


    private static final Logger LOG = Logger.getLogger(Aggregator.class);

    public boolean checkUserExistence() {
        boolean result = false;
        LOG.debug("Sending request to keycloak...");
        //      1 Send get_token
        //      2 Extract token
        //      3 Send check_existence 
        //      4 Extract timestamp
        //      5 If timestamp --> true else false
        //      6 If cannot connect --> Throw exception
        LOG.debug("Got response from keycloak...");
        return result;
    }
}
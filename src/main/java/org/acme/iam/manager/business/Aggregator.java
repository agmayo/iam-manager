package org.acme.iam.manager.business;

import org.jboss.logging.Logger;

/**
 * Aggregator
 */
public class Aggregator {


    private static final Logger LOG = Logger.getLogger(Aggregator.class);

    public boolean checkUserExistence() {
        boolean result = false;
        // 1. Send Request to keycloak
        LOG.debug("Sending request to keycloak...");
        // 2. Parse response
        LOG.debug("Got response from keycloak...");
        // 3. throw Exception or result = true or result = false
        return result;
    }
}
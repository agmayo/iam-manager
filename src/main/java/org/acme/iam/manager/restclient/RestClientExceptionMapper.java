package org.acme.iam.manager.restclient;

import java.io.ByteArrayInputStream;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.jboss.logging.Logger;

/**
 * TokenExceptionMapper
 * More info: https://itnext.io/how-to-deal-with-4xx-5xx-responses-in-microprofile-rest-client-2e16559f542
 */
@Priority(4000)
public class RestClientExceptionMapper implements 
ResponseExceptionMapper<RuntimeException>{
    // Logs to be sent to Logstash
    private static final Logger LOG = Logger.getLogger(RestClientExceptionMapper.class);
	@Override
	public RuntimeException toThrowable(Response response) {
		int status = response.getStatus();

        String msg = getBody(response);

        RuntimeException re ;
        switch (status) {
        case 401: 
            re = new WebApplicationException(msg, status);
            //re = new ForbiddenException(msg);
        break;
        default:
            re = new WebApplicationException(msg, status);
        }
    LOG.warn("IAM sent a "+ status + " HTTP status code and the following message: " + msg);
    return re;
    }
    private String getBody(Response response) {
        ByteArrayInputStream is = (ByteArrayInputStream) response.getEntity();
        byte[] bytes = new byte[is.available()];
        is.read(bytes,0,is.available());
        String body = new String(bytes);
        return body;
      }

    
}
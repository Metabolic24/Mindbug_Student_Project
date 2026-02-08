package org.metacorp.mindbug.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper for REST APIs
 */
@Provider
public class WebSocketExceptionMapper implements ExceptionMapper<WebSocketException> {

    @Override
    public Response toResponse(WebSocketException ex) {
        return Response.status(400).
                entity(ex.getMessage()).
                type("text/plain").
                build();
    }
}

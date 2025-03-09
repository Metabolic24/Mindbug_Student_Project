package org.metacorp.mindbug.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
* Exception mapper for REST APIs
 */
@Provider
public class GameStateExceptionMapper implements ExceptionMapper<GameStateException> {

    @Override
    public Response toResponse(GameStateException ex) {
        return Response.status(400).
                entity(ex.getMessage()).
                type("text/plain").
                build();
    }
}

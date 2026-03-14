package org.metacorp.mindbug.exception;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameStateExceptionMapperTest {

    @Test
    public void toResponse_nominal() {
        GameStateExceptionMapper mapper = new GameStateExceptionMapper();
        GameStateException exception = new GameStateException("test");

        Response response = mapper.toResponse(exception);
        assertNotNull(response);
        assertEquals(400, response.getStatus());
        assertEquals(exception.getMessage(), response.getEntity().toString());
        assertEquals("text/plain", response.getMediaType().toString());
    }
}

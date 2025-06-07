package org.metacorp.mindbug.controller;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.metacorp.mindbug.dto.player.RegisterDTO;
import org.metacorp.mindbug.service.PlayerService;

/**
 * Controller for player REST API
 */
@Path("/player")
public class PlayerController {


    /**
     * Endpoint triggered when a new player wants to register
     *
     * @param body the request body
     * @return the response to the REST request
     */
    @POST
    public Response register(RegisterDTO body) {
        if (body == null || body.getName() == null || body.getName().isBlank()) {
            return Response.status(400).entity("Invalid request body").build();
        }

        //TODO Ajouter éventuellement des contraintes/vérifications sur le nom du joueur

        return Response.ok(PlayerService.createPlayer(body.getName())).build();
    }
}

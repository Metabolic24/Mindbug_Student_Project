package org.metacorp.mindbug.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.metacorp.mindbug.dto.player.RegisterDTO;
import org.metacorp.mindbug.service.PlayerService;
import java.util.List;

/**
 * Controller for player REST API
 */
@Path("/player")
public class PlayerController {

    @Inject
    private PlayerService playerService;

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

        if (body.getName().length() < 3 || body.getName().length() > 20) { //TODO: choose max length
            return Response.status(400).entity("Name must be between 3 and 20 characters").build();
        }
        if (!body.getName().matches("^[a-zA-Z0-9_]+$")) {
            return Response.status(400).entity("Name contains invalid characters").build();
        }
        
        //TODO: choose forbidden names
        List<String> FORBIDDEN_NAMES = List.of(
                // administration
                "admin", "administrator", "root", "system", "server", "moderator", "mod", "staff", "support",
                "owner", "superuser", "sysadmin", "gameadmin",

                // official
                "official", "officiel", "dev", "developer", "game", "gamemaster", "gm", "cm", "communitymanager",
                "team", "equipes", "equipe", "serviceclient",
            

                // Variantes
                "adm1n", "4dmin", "r00t", "m0d",

                // Variantes FR
                "administrateur", "moderateur", "systeme", "serveur"
             );
        if (FORBIDDEN_NAMES.contains(body.getName().toLowerCase())) {
            return Response.status(400).entity("Forbidden name").build();
        }

        return Response.ok(playerService.createPlayer(body.getName())).build();
    }
}

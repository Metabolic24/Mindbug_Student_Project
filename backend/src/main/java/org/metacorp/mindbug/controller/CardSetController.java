package org.metacorp.mindbug.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.metacorp.mindbug.dto.card.CardSetDTO;
import org.metacorp.mindbug.exception.CardSetException;
import org.metacorp.mindbug.service.CardSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller for card set REST API
 */
@Path("/sets")
public class CardSetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardSetController.class);

    @Inject
    private CardSetService cardSetService;

    /**
     * @return the names of all the available card sets
     */
    @GET
    public List<String> getAvailableSets() {
        return cardSetService.getAvailableSets();
    }

    /**
     * Get all the cards related to the given set
     *
     * @param set the card set to filter on
     * @return the cards related to the set as a list of Integer
     */
    @GET
    @Path("/{set}")
    public List<Integer> getCardsBySet(@PathParam(value = "set") String set) {
        return cardSetService.getCardSetContent(set);
    }

    /**
     * Create a new custom card set
     *
     * @param cardSetDTO the card set DTO
     * @return HTTP OK if card set has been created, another HTTP code in other cases
     */
    @POST
    public Response createCustomCardSet(CardSetDTO cardSetDTO) {
        if (cardSetDTO == null
                || cardSetDTO.getName() == null || cardSetDTO.getName().isBlank()
                || cardSetDTO.getCards() == null || cardSetDTO.getCards().size() < 20) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request body").build();
        }

        try {
            cardSetService.create(cardSetDTO.getName(), cardSetDTO.getCards());
            return Response.ok().build();
        } catch (CardSetException e) {
            LOGGER.error(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{set}")
    public Response exportCardSet(@PathParam(value = "set") String set) {
        try {
            cardSetService.export(set);
            return Response.ok().build();
        } catch (CardSetException e) {
            LOGGER.error(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}

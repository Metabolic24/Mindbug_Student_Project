package org.metacorp.mindbug.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.metacorp.mindbug.model.card.Card;
import org.metacorp.mindbug.utils.SetUtils;

import java.util.List;

/**
 * Controller for card set REST API
 */
@Path("/sets")
public class CardSetController {

    /**
     * Retrieve the list of all available sets
     *
     * @return the list of all available sets as a list of String
     */
    @GET
    public List<String> getAvailableSets() {
        return SetUtils.getAvailableCardSets();
    }

    /**
     * Get all the cards related to the given set
     *
     * @param set the card set to filter on
     * @return the cards related to the set as a list of String
     */
    @GET
    @Path("/{set}")
    public List<Integer> getCardsBySet(@PathParam(value = "set") String set) {
        return SetUtils.getCardSetContent(set);
    }

    /**
     * Get full card data for a given set
     *
     * @param set the card set
     * @return the cards of the set
     */
    @GET
    @Path("/{set}/cards")
    public List<Card> getCardDetailsBySet(@PathParam(value = "set") String set) {
        return SetUtils.getCardSetCards(set);
    }

    /**
     * Get a single card by id within a set
     *
     * @param set the card set
     * @param id the card id
     * @return the card data
     */
    @GET
    @Path("/{set}/cards/{id}")
    public Card getCardById(@PathParam(value = "set") String set, @PathParam(value = "id") int id) {
        Card card = SetUtils.getCardById(set, id);
        if (card == null) {
            throw new WebApplicationException("Card not found", Response.Status.NOT_FOUND);
        }
        return card;
    }
}

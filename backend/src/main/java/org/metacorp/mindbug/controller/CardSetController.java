package org.metacorp.mindbug.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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
}

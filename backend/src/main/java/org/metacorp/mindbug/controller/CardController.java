package org.metacorp.mindbug.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.metacorp.mindbug.dto.card.CardDTO;
import org.metacorp.mindbug.dto.card.LightCardDTO;
import org.metacorp.mindbug.service.CardSetService;

import java.util.List;
import java.util.Set;

/**
 * Controller for card REST API
 */
@Path("/cards")
public class CardController {

    @Inject
    private CardSetService cardSetService;

    /**
     * @return the names of all the available card sets
     */
    @GET
    public List<LightCardDTO> getAllAvailableCards() {
        return cardSetService.getAllCards();
    }
}

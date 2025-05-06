package org.metacorp.mindbug.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.metacorp.mindbug.dto.GameStateDTO;
import org.metacorp.mindbug.dto.rest.DeclareAttackDTO;
import org.metacorp.mindbug.dto.rest.PickDTO;
import org.metacorp.mindbug.dto.rest.PlayDTO;
import org.metacorp.mindbug.dto.rest.ResolveAttackDTO;
import org.metacorp.mindbug.dto.rest.choice.BooleanAnswerDTO;
import org.metacorp.mindbug.dto.rest.choice.SimultaneousAnswerDTO;
import org.metacorp.mindbug.dto.rest.choice.TargetAnswerDTO;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.mapper.GameStateMapper;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.AttackService;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.service.PlayCardService;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Controller for game REST API
 */
@Path("/game")
public class GameController {

    @Inject
    private GameService gameService;

    //TODO To be removed as game should not be started that way
    @GET
    public Response start() {
        Game game = gameService.createGame();
        return Response.ok(GameStateMapper.fromGame(game)).build();
    }

    //TODO To be removed (for debug purpose only)
    @GET
    @Path("/{gameId}")
    public Response status(@PathParam(value = "gameId") UUID gameId) {
        Game game = gameService.findById(gameId);

        GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);

        return Response.ok(gameStateDTO).build();
    }

    /**
     * Endpoint triggered when the current player picks a card to be played
     *
     * @param body the request body
     * @return the response to the REST request
     * @throws GameStateException if an error occurs in game state
     */
    @POST
    @Path("/pick")
    public Response pick(PickDTO body) throws GameStateException {
        if (body == null || body.getGameId() == null || body.getCardId() == null) {
            return Response.status(400).entity("Invalid request body").build();
        }

        Game game = gameService.findById(body.getGameId());
        if (game == null) {
            return Response.status(400).entity("Requested game not found").build();
        }

        try {
            CardInstance pickedCard = game.getCurrentPlayer().getHand().stream()
                    .filter(cardInstance -> cardInstance.getUuid().equals(body.getCardId()))
                    .findFirst().orElseThrow();
            PlayCardService.pickCard(pickedCard, game);
        } catch (NoSuchElementException e) {
            return Response.status(400).entity("Card not found").build();
        }

        return Response.ok().build();
    }

    /**
     * Endpoint triggered after the opponent choice to mindbug or not the previously picked card
     *
     * @param body the request body
     * @return the response to the REST request
     * @throws GameStateException if an error occurs in game state
     */
    @POST
    @Path("/play")
    public Response play(PlayDTO body) throws GameStateException {
        if (body == null || body.getGameId() == null) {
            return Response.status(400).entity("Invalid request body").build();
        }

        Game game = gameService.findById(body.getGameId());
        if (game == null) {
            return Response.status(400).entity("Requested game not found").build();
        }

        Player mindbugger = null;
        if (body.getMindbuggerId() != null) {
            try {
                mindbugger = game.getPlayers().stream()
                        .filter(player -> player.getUuid().equals(body.getMindbuggerId()))
                        .findFirst().orElseThrow();
            } catch (NoSuchElementException e) {
                return Response.status(400).entity("Player not found").build();
            }
        }

        PlayCardService.playCard(mindbugger, game);

        return Response.ok().build();
    }

    /**
     * Endpoint triggered when the current player declares an attack with a card on his/her board
     *
     * @param body the request body
     * @return the response to the REST request
     * @throws GameStateException if an error occurs in game state
     */
    @POST
    @Path("/attack")
    public Response declareAttack(DeclareAttackDTO body) throws GameStateException {
        if (body == null || body.getGameId() == null || body.getAttackingCardId() == null) {
            return Response.status(400).entity("Invalid request body").build();
        }

        Game game = gameService.findById(body.getGameId());
        if (game == null) {
            return Response.status(404).entity("Requested game not found").build();
        }

        try {
            CardInstance attackingCard = game.getCurrentPlayer().getBoard().stream()
                    .filter(cardInstance -> cardInstance.getUuid().equals(body.getAttackingCardId()))
                    .findFirst().orElseThrow();
            AttackService.declareAttack(attackingCard, game);
        } catch (NoSuchElementException e) {
            return Response.status(400).entity("Card not found").build();
        }

        return Response.ok().build();
    }

    /**
     * Endpoint triggered when the opponent chooses the target of the current player attack
     *
     * @param body the request body
     * @return the response to the REST request
     * @throws GameStateException if an error occurs in game state
     */
    @PUT
    @Path("/attack")
    public Response resolveAttack(ResolveAttackDTO body) throws GameStateException {
        if (body == null || body.getGameId() == null ||
                (body.getDefendingPlayerId() != null && body.getDefenseCardId() == null) ||
                (body.getDefendingPlayerId() == null && body.getDefenseCardId() != null)) {
            return Response.status(400).entity("Invalid request body").build();
        }

        Game game = gameService.findById(body.getGameId());
        if (game == null) {
            return Response.status(404).entity("Requested game not found").build();
        }

        CardInstance defendingCard = null;
        if (body.getDefendingPlayerId() != null && body.getDefenseCardId() != null) {
            try {
                Player opponentPlayer = game.getPlayers().stream()
                        .filter(player -> player.getUuid().equals(body.getDefendingPlayerId()))
                        .findFirst().orElseThrow();

                defendingCard = opponentPlayer.getBoard().stream()
                        .filter(cardInstance -> cardInstance.getUuid().equals(body.getDefenseCardId()))
                        .findFirst().orElseThrow();
            } catch (NoSuchElementException e) {
                return Response.status(400).entity("Card not found").build();
            }
        }

        AttackService.resolveAttack(defendingCard, game);

        return Response.ok().build();
    }

    /**
     * Endpoint triggered when a boolean choice is resolved
     *
     * @param body the request body
     * @return the response to the REST request
     * @throws GameStateException if an error occurs in game state
     */
    @POST
    @Path("/choice/boolean")
    public Response resolveBoolean(BooleanAnswerDTO body) throws GameStateException {
        if (body == null || body.getGameId() == null || body.getOk() == null) {
            return Response.status(400).entity("Invalid request body").build();
        }

        Game game = gameService.findById(body.getGameId());
        if (game == null) {
            return Response.status(404).entity("Requested game not found").build();
        } else if (game.getChoice() == null || (game.getChoice().getType() != ChoiceType.BOOLEAN && game.getChoice().getType() != ChoiceType.FRENZY)) {
            return Response.status(404).entity("No boolean choice to resolve").build();
        }

        GameService.resolveChoice(body.getOk(), game);

        return Response.ok().build();
    }

    /**
     * Endpoint triggered when a simultaneous choice is resolved
     *
     * @param body the request body
     * @return the response to the REST request
     * @throws GameStateException if an error occurs in game state
     */
    @POST
    @Path("/choice/simultaneous")
    public Response resolveSimultaneousChoice(SimultaneousAnswerDTO body) throws GameStateException {
        if (body == null || body.getGameId() == null || body.getFirstCardId() == null) {
            return Response.status(400).entity("Invalid request body").build();
        }

        Game game = gameService.findById(body.getGameId());
        if (game == null) {
            return Response.status(404).entity("Requested game not found").build();
        } else if (game.getChoice() == null || (game.getChoice().getType() != ChoiceType.SIMULTANEOUS)) {
            return Response.status(404).entity("No simultaneous choice to resolve").build();
        }

        GameService.resolveChoice(body.getFirstCardId(), game);

        return Response.ok().build();
    }

    /**
     * Endpoint triggered when a target choice is resolved
     *
     * @param body the request body
     * @return the response to the REST request
     * @throws GameStateException if an error occurs in game state
     */
    @POST
    @Path("/choice/target")
    public Response resolveTargetChoice(TargetAnswerDTO body) throws GameStateException {
        if (body == null || body.getGameId() == null || body.getTargets() == null) {
            return Response.status(400).entity("Invalid request body").build();
        }

        Game game = gameService.findById(body.getGameId());
        if (game == null) {
            return Response.status(404).entity("Requested game not found").build();
        } else if (game.getChoice() == null || (game.getChoice().getType() != ChoiceType.TARGET)) {
            return Response.status(404).entity("No simultaneous choice to resolve").build();
        }

        GameService.resolveChoice(body.getTargets(), game);

        return Response.ok().build();
    }
}

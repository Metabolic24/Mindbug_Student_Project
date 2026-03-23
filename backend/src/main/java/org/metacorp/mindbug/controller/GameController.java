package org.metacorp.mindbug.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.metacorp.mindbug.dto.rest.ActionDTO;
import org.metacorp.mindbug.dto.rest.DeclareAttackDTO;
import org.metacorp.mindbug.dto.rest.PickDTO;
import org.metacorp.mindbug.dto.rest.ResolveAttackDTO;
import org.metacorp.mindbug.dto.rest.StartOfflineDTO;
import org.metacorp.mindbug.dto.rest.SurrenderDTO;
import org.metacorp.mindbug.dto.rest.choice.BooleanAnswerDTO;
import org.metacorp.mindbug.dto.rest.choice.MultipleTargetAnswerDTO;
import org.metacorp.mindbug.dto.rest.choice.SingleTargetAnswerDTO;
import org.metacorp.mindbug.exception.CardSetException;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.UnknownPlayerException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.mapper.GameStateMapper;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.service.game.ActionService;
import org.metacorp.mindbug.service.game.AttackService;
import org.metacorp.mindbug.service.game.ChoiceService;
import org.metacorp.mindbug.service.game.GameStateService;
import org.metacorp.mindbug.service.game.PlayCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggablePlayer;

/**
 * Controller for game REST API
 */
@Path("/game")
public class GameController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    @Inject
    private GameService gameService;

    @POST
    @Path("/startOffline")
    public Response startOffline(StartOfflineDTO startDTO) {
        LOGGER.debug("Starting an offline game for player {}", startDTO.getPlayerId());

        try {
            Game game = gameService.createGame(Arrays.asList(startDTO.getPlayerId(), null), startDTO.getCardSetName());
            return Response.ok(game.getUuid()).build();
        } catch (UnknownPlayerException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid player ID").build();
        } catch (CardSetException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    //TODO To be removed (for debug purpose only)
    @GET
    @Path("/{gameId}")
    public Response status(@PathParam(value = "gameId") UUID gameId) {
        Game game = gameService.findById(gameId);
        return Response.ok(GameStateMapper.fromGame(game)).build();
    }

    @POST
    @Path("/surrender")
    public Response surrender(SurrenderDTO body) {
        if (body.getGameId() == null || body.getPlayerId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing request data").build();
        }

        Game game = gameService.findById(body.getGameId());
        if (game == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Requested game not found").build();
        }

        Optional<Player> playerOpt = game.getPlayers().stream()
                .filter(player -> player.getUuid().equals(body.getPlayerId()))
                .findFirst();
        if (playerOpt.isPresent()) {
            try {
                GameStateService.endGame(playerOpt.get(), game);
            } catch (WebSocketException e) {
                game.getLogger().warn("Failed to send WebSocket message after surrendering", e);
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Unknown player ID").build();
        }

        return Response.ok().build();
    }

    /**
     * Endpoint triggered when the current player picks a card to be played
     *
     * @param body the request body
     * @return the response to the REST request
     * @throws GameStateException if an error occurs in game state
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    @POST
    @Path("/pick")
    public Response pick(PickDTO body) throws GameStateException, WebSocketException {
        if (body == null || body.getGameId() == null || body.getCardId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request body").build();
        }

        Game game = gameService.findById(body.getGameId());
        if (game == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Requested game not found").build();
        }

        try {
            CardInstance pickedCard = game.getCurrentPlayer().getHand().stream()
                    .filter(cardInstance -> cardInstance.getUuid().equals(body.getCardId()))
                    .findFirst().orElseThrow();

            game.getLogger().debug("Player {} picked card {}", getLoggablePlayer(game.getCurrentPlayer()), getLoggableCard(pickedCard));
            PlayCardService.pickCard(pickedCard, game);
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Card not found").build();
        }

        return Response.ok().build();
    }

    /**
     * Endpoint triggered when a player chooses an action on his/her cards
     *
     * @param body the request body
     * @return the response to the REST request
     * @throws GameStateException if an error occurs in game state
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    @POST
    @Path("/action")
    public Response action(ActionDTO body) throws GameStateException, WebSocketException {
        if (body == null || body.getGameId() == null || body.getCardId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request body").build();
        }

        Game game = gameService.findById(body.getGameId());
        if (game == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Requested game not found").build();
        }

        try {
            CardInstance actionCard = game.getCurrentPlayer().getBoard().stream()
                    .filter(cardInstance -> cardInstance.getUuid().equals(body.getCardId()))
                    .findFirst().orElseThrow();

            game.getLogger().debug("Player {} used {} action", getLoggablePlayer(game.getCurrentPlayer()), getLoggableCard(actionCard));

            ActionService.resolveAction(actionCard, game);
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Card not found").build();
        }

        return Response.ok().build();
    }

    /**
     * Endpoint triggered when the current player declares an attack with a card on his/her board
     *
     * @param body the request body
     * @return the response to the REST request
     * @throws GameStateException if an error occurs in game state
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    @POST
    @Path("/attack")
    public Response declareAttack(DeclareAttackDTO body) throws GameStateException, WebSocketException {
        if (body == null || body.getGameId() == null || body.getAttackingCardId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request body").build();
        }

        Game game = gameService.findById(body.getGameId());
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Requested game not found").build();
        }

        try {
            CardInstance attackingCard = game.getCurrentPlayer().getBoard().stream()
                    .filter(cardInstance -> cardInstance.getUuid().equals(body.getAttackingCardId()))
                    .findFirst().orElseThrow();

            game.getLogger().debug("Player {} attacks with {}", getLoggablePlayer(game.getCurrentPlayer()), getLoggableCard(attackingCard));

            AttackService.declareAttack(attackingCard, game);
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Card not found").build();
        }

        return Response.ok().build();
    }

    /**
     * Endpoint triggered when the opponent chooses the target of the current player attack
     *
     * @param body the request body
     * @return the response to the REST request
     * @throws GameStateException if an error occurs in game state
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    @PUT
    @Path("/attack")
    public Response resolveAttack(ResolveAttackDTO body) throws GameStateException, WebSocketException {
        if (body == null || body.getGameId() == null
                || (body.getDefendingPlayerId() != null && body.getDefenseCardId() == null)
                || (body.getDefendingPlayerId() == null && body.getDefenseCardId() != null)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request body").build();
        }

        Game game = gameService.findById(body.getGameId());
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Requested game not found").build();
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

                game.getLogger().debug("Player {} blocks {} with {}", getLoggablePlayer(opponentPlayer),
                        getLoggableCard(game.getAttackingCard()), getLoggableCard(defendingCard));
            } catch (NoSuchElementException e) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Card not found").build();
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
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    @POST
    @Path("/choice/boolean")
    public Response resolveBoolean(BooleanAnswerDTO body) throws GameStateException, WebSocketException {
        if (body == null || body.getGameId() == null || body.getOk() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request body").build();
        }

        Game game = gameService.findById(body.getGameId());
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Requested game not found").build();
        } else if (game.getChoice() == null || (game.getChoice().getType() != ChoiceType.BOOLEAN
                && game.getChoice().getType() != ChoiceType.FRENZY && game.getChoice().getType() != ChoiceType.MINDBUG)) {
            return Response.status(Response.Status.NOT_FOUND).entity("No boolean choice to resolve").build();
        }

        game.getLogger().debug("Resolving boolean choice with value {}", body.getOk());

        ChoiceService.resolveChoice(body.getOk(), game);

        return Response.ok().build();
    }

    /**
     * Endpoint triggered when a single target choice is resolved (SIMULTANEOUS or HUNTER)
     *
     * @param body the request body
     * @return the response to the REST request
     * @throws GameStateException if an error occurs in game state
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    @POST
    @Path("/choice/single")
    public Response resolveSingleTargetChoice(SingleTargetAnswerDTO body) throws GameStateException, WebSocketException {
        if (body == null || body.getGameId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request body").build();
        }

        Game game = gameService.findById(body.getGameId());
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Requested game not found").build();
        } else if (game.getChoice() == null || (game.getChoice().getType() != ChoiceType.SIMULTANEOUS
                && game.getChoice().getType() != ChoiceType.HUNTER)) {
            return Response.status(Response.Status.NOT_FOUND).entity("No single target choice (SIMULTANEOUS or HUNTER) to resolve").build();
        } else if (game.getChoice().getType() == ChoiceType.SIMULTANEOUS && body.getCardId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request body : missing cardId").build();
        }

        game.getLogger().debug("Resolving single target choice with card {}", body.getCardId());

        ChoiceService.resolveChoice(body.getCardId(), game);

        return Response.ok().build();
    }

    /**
     * Endpoint triggered when a target choice is resolved
     *
     * @param body the request body
     * @return the response to the REST request
     * @throws GameStateException if an error occurs in game state
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    @POST
    @Path("/choice/target")
    public Response resolveTargetChoice(MultipleTargetAnswerDTO body) throws GameStateException, WebSocketException {
        if (body == null || body.getGameId() == null || body.getTargets() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request body").build();
        }

        Game game = gameService.findById(body.getGameId());
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Requested game not found").build();
        } else if (game.getChoice() == null || game.getChoice().getType() != ChoiceType.TARGET) {
            return Response.status(Response.Status.NOT_FOUND).entity("No target choice to resolve").build();
        } else {
            game.getLogger().debug("Resolving target choice with cards {}", body.getTargets());

            ChoiceService.resolveChoice(body.getTargets(), game);
        }

        return Response.ok().build();
    }
}

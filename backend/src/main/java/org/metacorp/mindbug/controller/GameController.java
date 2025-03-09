package org.metacorp.mindbug.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.metacorp.mindbug.dto.rest.DeclareAttackDTO;
import org.metacorp.mindbug.dto.rest.PickDTO;
import org.metacorp.mindbug.dto.rest.PlayDTO;
import org.metacorp.mindbug.dto.rest.ResolveAttackDTO;
import org.metacorp.mindbug.dto.rest.choice.BooleanChoiceDTO;
import org.metacorp.mindbug.dto.rest.choice.SimultaneousChoiceDTO;
import org.metacorp.mindbug.dto.rest.choice.TargetChoiceDTO;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.AttackService;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.service.PlayCardService;

import java.util.NoSuchElementException;

@Path("/game")
public class GameController {

    @Inject
    private GameService gameService;

    @GET
    public Response start() {
        Game game = gameService.createGame();
        System.out.println("==========================================");
        System.out.println(game.getUuid());

        for (Player player : game.getPlayers()) {
            System.out.println("==========================================");
            System.out.println(player.getName() + " : " + player.getUuid());
            for (CardInstance card : player.getHand()) {
                System.out.println("\t-" + card.getCard().getName() + " : " + card.getUuid());
            }
        }
        System.out.println("==========================================");

        return Response.ok().build();
    }

    @POST
    @Path("/pick")
    public Response pick(PickDTO dto) throws GameStateException {
        if (dto == null || dto.getGameId() == null || dto.getCardId() == null) {
            return Response.status(400).entity("Invalid request body").build();
        }

        Game game = gameService.findById(dto.getGameId());
        if (game == null) {
            return Response.status(400).entity("Requested game not found").build();
        }

        try {
            CardInstance pickedCard = game.getCurrentPlayer().getHand().stream()
                    .filter(cardInstance -> cardInstance.getUuid().equals(dto.getCardId()))
                    .findFirst().orElseThrow();
            PlayCardService.pickCard(pickedCard, game);
        } catch (NoSuchElementException e) {
            return Response.status(400).entity("Card not found").build();
        }

        return Response.ok().build();
    }

    @POST
    @Path("/play")
    public Response play(PlayDTO dto) throws GameStateException {
        if (dto == null || dto.getGameId() == null) {
            return Response.status(400).entity("Invalid request body").build();
        }

        Game game = gameService.findById(dto.getGameId());
        if (game == null) {
            return Response.status(400).entity("Requested game not found").build();
        }

        Player mindbugger = null;
        if (dto.getMindbuggerId() != null) {
            try {
                mindbugger = game.getPlayers().stream()
                        .filter(player -> player.getUuid().equals(dto.getMindbuggerId()))
                        .findFirst().orElseThrow();
            } catch (NoSuchElementException e) {
                return Response.status(400).entity("Player not found").build();
            }
        }

        PlayCardService.playCard(mindbugger, game);

        return Response.ok().build();
    }

    @POST
    @Path("/attack")
    public Response declareAttack(DeclareAttackDTO dto) throws GameStateException {
        if (dto == null || dto.getGameId() == null || dto.getAttackingCardId() == null) {
            return Response.status(400).entity("Invalid request body").build();
        }

        Game game = gameService.findById(dto.getGameId());
        if (game == null) {
            return Response.status(404).entity("Requested game not found").build();
        }

        try {
            CardInstance attackingCard = game.getCurrentPlayer().getBoard().stream()
                    .filter(cardInstance -> cardInstance.getUuid().equals(dto.getAttackingCardId()))
                    .findFirst().orElseThrow();
            AttackService.declareAttack(attackingCard, game);
        } catch (NoSuchElementException e) {
            return Response.status(400).entity("Card not found").build();
        }

        return Response.ok().build();
    }

    @PUT
    @Path("/attack")
    public Response resolveAttack(ResolveAttackDTO dto) throws GameStateException {
        if (dto == null || dto.getGameId() == null ||
                (dto.getDefendingPlayerId() != null && dto.getDefenseCardId() == null) ||
                (dto.getDefendingPlayerId() == null && dto.getDefenseCardId() != null)) {
            return Response.status(400).entity("Invalid request body").build();
        }

        Game game = gameService.findById(dto.getGameId());
        if (game == null) {
            return Response.status(404).entity("Requested game not found").build();
        }

        CardInstance defendingCard = null;
        if (dto.getDefendingPlayerId() != null && dto.getDefenseCardId() != null) {
            try {
                Player opponentPlayer = game.getPlayers().stream()
                        .filter(player -> player.getUuid().equals(dto.getDefendingPlayerId()))
                        .findFirst().orElseThrow();

                defendingCard = opponentPlayer.getBoard().stream()
                        .filter(cardInstance -> cardInstance.getUuid().equals(dto.getDefenseCardId()))
                        .findFirst().orElseThrow();
            } catch (NoSuchElementException e) {
                return Response.status(400).entity("Card not found").build();
            }
        }

        AttackService.resolveAttack(defendingCard, game);

        return Response.ok().build();
    }

    @POST
    @Path("/choice/boolean")
    public Response resolveBoolean(BooleanChoiceDTO dto) throws GameStateException {
        if (dto == null || dto.getGameId() == null || dto.getOk() == null) {
            return Response.status(400).entity("Invalid request body").build();
        }

        Game game = gameService.findById(dto.getGameId());
        if (game == null) {
            return Response.status(404).entity("Requested game not found").build();
        } else if (game.getChoice() == null || (game.getChoice().getType() != ChoiceType.BOOLEAN && game.getChoice().getType() != ChoiceType.FRENZY)) {
            return Response.status(404).entity("No boolean choice to resolve").build();
        }

        GameService.resolveChoice(dto.getOk(), game);

        return Response.ok().build();
    }

    @POST
    @Path("/choice/simultaneous")
    public Response resolveSimultaneousChoice(SimultaneousChoiceDTO dto) throws GameStateException {
        if (dto == null || dto.getGameId() == null || dto.getFirstCardId() == null) {
            return Response.status(400).entity("Invalid request body").build();
        }

        Game game = gameService.findById(dto.getGameId());
        if (game == null) {
            return Response.status(404).entity("Requested game not found").build();
        } else if (game.getChoice() == null || (game.getChoice().getType() != ChoiceType.SIMULTANEOUS)) {
            return Response.status(404).entity("No simultaneous choice to resolve").build();
        }

        GameService.resolveChoice(dto.getFirstCardId(), game);

        return Response.ok().build();
    }

    @POST
    @Path("/choice/target")
    public Response resolveTargetChoice(TargetChoiceDTO dto) throws GameStateException {
        if (dto == null || dto.getGameId() == null || dto.getTargets() == null) {
            return Response.status(400).entity("Invalid request body").build();
        }

        Game game = gameService.findById(dto.getGameId());
        if (game == null) {
            return Response.status(404).entity("Requested game not found").build();
        } else if (game.getChoice() == null || (game.getChoice().getType() != ChoiceType.TARGET)) {
            return Response.status(404).entity("No simultaneous choice to resolve").build();
        }

        GameService.resolveChoice(dto.getTargets(), game);

        return Response.ok().build();
    }
}

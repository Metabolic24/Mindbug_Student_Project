package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.InflictEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.model.player.Team;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.game.GameStateService;

import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggablePlayer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Effect resolver for InflictEffect
 */
public class InflictEffectResolver extends EffectResolver<InflictEffect> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public InflictEffectResolver(InflictEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) throws WebSocketException{
        this.effectSource = card;

        boolean self = effect.isSelf();
        boolean allButOne = effect.isAllButOne();
        

        List<Player> affectedPlayers = self
                ? List.of(card.getOwner())
                : card.getOwner().getOpponents(game.getPlayers());

        // In 2v2, opponents can share the same Team: apply the effect once per Team.
        Set<Team> affectedTeams = new HashSet<>();
        for (Player affectedPlayer : affectedPlayers) {
            Team affectedTeam = affectedPlayer.getTeam();
            if (!affectedTeams.add(affectedTeam)) {
                continue;
            }

            if (allButOne) {
                if (affectedTeam.getLifePoints() > 1) {
                    affectedTeam.setLifePoints(1);
                    GameStateService.lifePointLost(affectedPlayer, game);
                }
            } else {
                int value = effect.isMindbugCount() ? affectedPlayer.getMindBugs() : effect.getValue();
                affectedTeam.loseLifePoints(value);
                GameStateService.lifePointLost(affectedPlayer, game);
            }
        }
        game.getLogger().debug("{} LP changed ({} -> {}) due to {} effect",
                getLoggablePlayer(affectedPlayer), oldLifePoints, affectedTeam.getLifePoints(), getLoggableCard(effectSource));
                
        HistoryService.logEffect(game, effect.getType(), effectSource, null);
    }
}

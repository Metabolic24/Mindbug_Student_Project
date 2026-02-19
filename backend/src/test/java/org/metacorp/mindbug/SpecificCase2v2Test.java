package org.metacorp.mindbug;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.game.AttackService;
import org.metacorp.mindbug.utils.TestGameUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.metacorp.mindbug.utils.TestGameUtils.prepareCustomGame2v2;
import static org.metacorp.mindbug.utils.TestUtils.cleanHistoryDirectory;

public class SpecificCase2v2Test {

    private Game game;

    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;


    @BeforeEach
    public void setup() {
        game = prepareCustomGame2v2();
        player1 = TestGameUtils.getPlayer1();
        player2 = TestGameUtils.getPlayer2();
        player3 = TestGameUtils.getPlayer3();
        player4 = TestGameUtils.getPlayer4();
    }
}

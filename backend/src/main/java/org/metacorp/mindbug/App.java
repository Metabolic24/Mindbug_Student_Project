package org.metacorp.mindbug;

public class App {

    public static void main(String[] args) {
        Game game = new Game("Player1", "Player2");
        System.out.println(game.getCurrentPlayer());
    }
}

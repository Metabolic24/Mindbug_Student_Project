package org.metacorp.mindbug;

import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.app.AutoApp;

public class AutoAppTest {

    @Test
    public void testNominal() {
        for (int i = 0; i < 100; i++) {
            System.out.println("\n===========================");
            System.out.println("Début de la partie n°" + i + "\n");
            AutoApp.main(null);
            System.out.println("===========================\n");
        }
    }
}

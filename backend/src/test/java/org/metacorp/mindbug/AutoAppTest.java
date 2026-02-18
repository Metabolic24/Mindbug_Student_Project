package org.metacorp.mindbug;

import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.app.AutoApp;
import org.metacorp.mindbug.utils.MindbugGameTest;

public class AutoAppTest extends MindbugGameTest {

    @Test
    public void testNominal() {
        for (int i = 0; i < 100; i++) {
            System.out.println("\n========================");
            System.out.println(" Début de la partie n°" + i);
            System.out.println("========================\n");

            AutoApp.start(getAppUtilsGame());
        }
    }
}

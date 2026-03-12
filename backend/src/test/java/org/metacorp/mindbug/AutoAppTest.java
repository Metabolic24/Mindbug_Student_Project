package org.metacorp.mindbug;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.metacorp.mindbug.app.AutoApp;

import java.io.IOException;

import static org.metacorp.mindbug.utils.TestUtils.cleanHistoryDirectory;

public class AutoAppTest {

    @AfterAll
    public static void tearDown() throws IOException {
        cleanHistoryDirectory();
    }

    @Test
    public void testNominal() {
        for (int i = 0; i < 100; i++) {
            System.out.println("\n========================");
            System.out.println(" Début de la partie n°" + i);
            System.out.println("========================\n");

            AutoApp.main();
        }
    }
}

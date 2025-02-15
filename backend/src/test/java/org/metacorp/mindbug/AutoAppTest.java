package org.metacorp.mindbug;

import org.junit.jupiter.api.Test;

public class AutoAppTest {

    @Test
    public void testNominal() {
        for (int i = 0; i < 100; i++) {
            AutoApp.main(null);
        }
    }
}

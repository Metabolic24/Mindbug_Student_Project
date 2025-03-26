package com.mindbug;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mindbug.controller.CardController;
import com.mindbug.services.CardService;

@SpringBootTest
class MindbugApplicationTests {

	@Autowired
    private CardController cardController;

    @Autowired
    private CardService cardService;

	@Test
	void contextLoads() {
		assertNotNull(cardController, "The controller should not be null");
        assertNotNull(cardService, "The Service should not be null");
	}

}

package org.metacorp.mindbug.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.stream.Stream;

/**
 * Utility class for tests
 */
public class TestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

    /**
     * Clean the directory containing log files
     *
     * @throws IOException if an error occurs while listing or deleting files/directories
     */
    public static void cleanHistoryDirectory() throws IOException {
        Path logDir = Path.of("./log");

        try (Stream<Path> stream = Files.list(logDir)) {
            stream.forEach(path -> {
                if (!path.getFileName().toString().equals("mindbug.log")) {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        String errorMessage = MessageFormat.format("Failed to clean history file {0}", path);
                        LOGGER.warn(errorMessage, e);
                    }
                }
            });
        }
    }
}

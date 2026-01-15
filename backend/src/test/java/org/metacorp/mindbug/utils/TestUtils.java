package org.metacorp.mindbug.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Utility class for tests
 */
public class TestUtils {

    /**
     * Clean the directory containing log files
     * @throws IOException if an error occurs while listing or deleting files/directories
     */
    public static void cleanHistoryDirectory() throws IOException {
        Path logDir = Path.of("./log");

        try (Stream<Path> stream = Files.list(logDir)) {
            stream.forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        Files.delete(logDir);
    }
}

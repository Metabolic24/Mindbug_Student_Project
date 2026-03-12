package org.metacorp.mindbug.app;

/**
 * Backward-compatible entry point for auto 2v2 mode.
 * Prefer using AutoApp.main with args (\"2v2\").
 */
public class AutoApp2v2 {
    public static void main() {
        AutoApp.main(new String[] {"2v2"});
    }
}

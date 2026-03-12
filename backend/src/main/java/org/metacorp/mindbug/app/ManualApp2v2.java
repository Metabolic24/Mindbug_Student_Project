package org.metacorp.mindbug.app;

/**
 * Backward-compatible entry point for manual 2v2 mode.
 * Prefer using ManualApp.main with args ("2v2").
 */
public class ManualApp2v2 {
    public static void main(String[] args) {
        ManualApp.main(new String[] {"2v2"});
    }
}

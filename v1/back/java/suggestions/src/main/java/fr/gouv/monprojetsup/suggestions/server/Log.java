package fr.gouv.monprojetsup.suggestions.server;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Log {

    /* todo: do better, keep track uf usage statistics */

    public static void logTrace(String handleException, String message) {
        log.info(handleException + " " + message);

    }

    public static void logBackError(Throwable e) {
        log.warn("Error", e);
    }

    public static void logBackError(String s) {
        log.warn(s);
    }
}

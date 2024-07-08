package fr.gouv.monprojetsup.suggestions.server;

import com.sun.net.httpserver.HttpServer;

public interface ServerInterface {
    void start();

    void registerHandlers(HttpServer server);

    void stop();

}

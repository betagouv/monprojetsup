package fr.gouv.monprojetsup.app.server;

import com.sun.net.httpserver.HttpServer;

public interface ServerInterface {
    void start();

    void registerHandlers(HttpServer server);

    void stop();

}

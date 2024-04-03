package fr.gouv.monprojetsup.common.server;

import java.util.logging.Logger;

public abstract class Server {

    public static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    protected abstract void init() throws Exception;


}

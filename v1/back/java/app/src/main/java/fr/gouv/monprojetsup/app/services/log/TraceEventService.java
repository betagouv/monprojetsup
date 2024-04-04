package fr.gouv.monprojetsup.app.services.log;

import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.app.log.Log;
import fr.gouv.monprojetsup.common.server.Server;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class TraceEventService extends MyService<TraceEventService.Request, Server.BasicResponse> {

    public TraceEventService() {
        super(Request.class);
    }

    public record Request(
            @NotNull String login,
            @NotNull String token,
            @NotNull String action)
    {
    }


    @Override
    public @NotNull Server.BasicResponse handleRequest(@NotNull Request req) throws Exception {
        Log.logTrace(req.login, req.action);
        return new Server.BasicResponse();
    }


}

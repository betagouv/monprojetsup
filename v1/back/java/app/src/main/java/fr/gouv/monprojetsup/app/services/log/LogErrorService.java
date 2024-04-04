package fr.gouv.monprojetsup.app.services.log;

import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.app.log.Log;
import fr.gouv.monprojetsup.common.server.Server;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class LogErrorService extends MyService<LogErrorService.Request, Server.BasicResponse> {

    public LogErrorService() {
        super(Request.class);
    }

    public  record Request(
            @NotNull String error
    ) {
    }


    @Override
    public @NotNull Server.BasicResponse handleRequest(@NotNull Request req) throws Exception {
        //todo prevents DOS
        Log.logFrontError(req.error());
        return new Server.BasicResponse();
    }

}

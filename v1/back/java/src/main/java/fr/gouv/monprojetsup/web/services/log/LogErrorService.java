package fr.gouv.monprojetsup.web.services.log;

import fr.gouv.monprojetsup.tools.server.MyService;
import fr.gouv.monprojetsup.web.log.Log;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class LogErrorService extends MyService<LogErrorService.Request, MyService.BasicResponse> {

    public LogErrorService() {
        super(Request.class);
    }

    public  record Request(
            @NotNull String error
    ) {
    }


    @Override
    public @NotNull MyService.BasicResponse handleRequest(@NotNull Request req) throws Exception {
        //todo prevents DOS
        Log.logFrontError(req.error());
        return new MyService.BasicResponse();
    }

}

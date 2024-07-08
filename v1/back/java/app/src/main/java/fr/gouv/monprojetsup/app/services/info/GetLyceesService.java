package fr.gouv.monprojetsup.app.services.info;

import fr.gouv.monprojetsup.app.db.model.Lycee;
import fr.gouv.monprojetsup.app.server.MyAppService;
import fr.gouv.monprojetsup.data.dto.ResponseHeader;
import fr.gouv.monprojetsup.app.server.Server;
import fr.gouv.monprojetsup.app.server.WebServer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetLyceesService extends MyAppService<Server.EmptyRequest, GetLyceesService.Response> {

    public GetLyceesService() {
        super(Server.EmptyRequest.class);
    }

    public record Response(
            ResponseHeader header,
            List<Lycee> lycees
    ) {
        public Response(List<Lycee> lycees) { this(new ResponseHeader(), lycees); }
    }


    @Override
    protected @NotNull Response handleRequest(@NotNull Server.EmptyRequest req) throws Exception {
        return new Response(WebServer.db().getLycees());
    }


}

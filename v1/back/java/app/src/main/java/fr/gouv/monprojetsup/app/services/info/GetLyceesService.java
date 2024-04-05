package fr.gouv.monprojetsup.app.services.info;

import fr.gouv.monprojetsup.app.db.model.Lycee;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.common.server.Server;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetLyceesService extends fr.gouv.monprojetsup.data.services.MyService<Server.EmptyRequest, GetLyceesService.Response> {

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

package fr.gouv.monprojetsup.app.services.teacher;

import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.dto.AdminInfosDTO;
import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class GetAdminInfosService extends MyService<MyService.BasicRequest, GetAdminInfosService.Response> {

    public GetAdminInfosService() {
        super(MyService.BasicRequest.class);
    }

    public record Response(
            @NotNull ResponseHeader header,
            @Nullable AdminInfosDTO infos
    ) {
        public Response(AdminInfosDTO infos) { this(new ResponseHeader(), infos); }

        public Response(AdminInfosDTO infos, String userMsg) { this(new ResponseHeader(userMsg), infos); }
    }

    @Override
    protected @NotNull Response handleRequest(@NotNull MyService.BasicRequest req) throws Exception {
        DB.authenticator.tokenAuthenticate(req.login(), req.token());
        AdminInfosDTO infos = WebServer.db().getAdminInfos(req.login());
        return new Response(infos);
    }

}

package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.tools.server.MyService;
import fr.gouv.monprojetsup.tools.server.ResponseHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class GetFormationsOfInterestService extends MyService<GetFormationsOfInterestService.Request, GetFormationsOfInterestService.Response> {


    public GetFormationsOfInterestService() {
        super(Request.class);
    }

    public record Request(
        @Nullable Set<String> geo_pref,
        @NotNull List<String> keys
        ) {
    }

    public record Response(
            @NotNull ResponseHeader header,
            @NotNull List<String> gtas//a list of gtacods of interest
    ) {
        public Response(@NotNull List<String> gtas) { this(new ResponseHeader(), gtas); }
    }

    @Override
    protected @NotNull Response handleRequest(@NotNull Request req) throws Exception {

        List<String> gtas = ServerData.getFormationsOfInterest(
                req.keys(),
                req.geo_pref()
        );

        return new Response(gtas);
    }


}

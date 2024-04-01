package fr.gouv.monprojetsup.app.services.info;

import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.stats.StatsContainers;
import fr.gouv.monprojetsup.app.tools.server.MyService;
import fr.gouv.monprojetsup.app.tools.server.ResponseHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class GetSimpleStatsService extends MyService<GetSimpleStatsService.Request, GetSimpleStatsService.Response> {

    public GetSimpleStatsService() {
        super(Request.class);
    }

    public record Request(
        @Nullable String bac,
        @NotNull String key) {
    }

    public record Response(
            @NotNull ResponseHeader header,
            @Nullable StatsContainers.DetailFiliere stats
    ) {
        public Response(
                @NotNull StatsContainers.DetailFiliere stats
                ) {
            this(new ResponseHeader(), stats);
        }
    }

    @Override
    protected @NotNull Response handleRequest(@NotNull Request req) throws Exception {
        @NotNull StatsContainers.DetailFiliere stats = ServerData.getSimpleGroupStats(
                req.bac(),
                req.key
        );
        return new Response(stats);
    }


}

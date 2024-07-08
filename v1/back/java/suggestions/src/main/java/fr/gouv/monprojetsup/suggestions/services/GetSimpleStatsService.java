package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.dto.ResponseHeader;
import fr.gouv.monprojetsup.data.model.stats.StatsContainers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class GetSimpleStatsService extends MySuggService<GetSimpleStatsService.Request, GetSimpleStatsService.Response> {

    public GetSimpleStatsService() {
        super(Request.class);
    }

    public record Request(
        @Nullable String bac,
        @NotNull String key) {
    }

    public record Response(
            @NotNull ResponseHeader header,
            @Nullable StatsContainers.SimpleStatGroupParBac stats
    ) {
        public Response(
                @NotNull StatsContainers.SimpleStatGroupParBac stats
                ) {
            this(new ResponseHeader(), stats);
        }
    }

    @Override
    protected @NotNull Response handleRequest(@NotNull Request req) throws Exception {
        @NotNull StatsContainers.SimpleStatGroupParBac stats = ServerData.getSimpleGroupStats(
                req.bac(),
                req.key
        );
        return new Response(stats);
    }


}

package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.data.model.stats.StatsContainers;
import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import fr.gouv.monprojetsup.suggestions.dto.ResponseHeader;
import fr.gouv.monprojetsup.suggestions.server.MySuggService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetSimpleStatsService extends MySuggService<GetSimpleStatsService.Request, GetSimpleStatsService.Response> {

    private final SuggestionsData data;

    @Autowired
    public GetSimpleStatsService(
            SuggestionsData data
    ) {
        super(Request.class);
        this.data = data;
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
        @NotNull StatsContainers.SimpleStatGroupParBac stats = data.getSimpleGroupStats(
                req.bac(),
                req.key
        );
        return new Response(stats);
    }


}

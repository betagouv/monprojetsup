package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.algos.Explanation;
import fr.gouv.monprojetsup.app.tools.server.ResponseHeader;
import fr.gouv.monprojetsup.suggestions.server.MyService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions.getDistanceKm;

@Service
public class GetFormationsOfInterestService extends MyService<GetFormationsOfInterestService.Request, GetFormationsOfInterestService.Response> {


    public GetFormationsOfInterestService() {
        super(Request.class);
    }

    public static List<String> getFormationsOfInterest(@Nullable List<String> keys, @Nullable Set<String> cities) {
        if(keys == null || cities == null || keys.isEmpty() || cities.isEmpty()) return Collections.emptyList();
        return cities.stream().flatMap(city ->
                        keys.stream()
                                .flatMap(key -> ServerData.reverseFlGroups.containsKey(key)
                                        ? AlgoSuggestions.getDistanceKm(ServerData.reverseFlGroups.get(key), city).stream()
                                        : getDistanceKm(key, city).stream()
                                )
                ).filter(Objects::nonNull)
                .map(Explanation.ExplanationGeo::form)
                .toList();
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
    protected @NotNull Response handleRequest(@NotNull Request req) {

        List<String> gtas = getFormationsOfInterest(
                req.keys(),
                req.geo_pref()
        );

        return new Response(gtas);
    }


}

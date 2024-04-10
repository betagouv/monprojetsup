package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.common.dto.ExplanationGeo;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.distances.Distances;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GetFormationsOfInterestService extends MyService<GetFormationsOfInterestService.Request, GetFormationsOfInterestService.Response> {


    public GetFormationsOfInterestService() {
        super(Request.class);
    }

    public static List<ExplanationGeo> getGeographicInterests(@Nullable List<String> flKeys, @Nullable Set<String> cities, int maxFormationsPerFiliere) {
        if(flKeys == null || cities == null || flKeys.isEmpty() || cities.isEmpty()) return Collections.emptyList();
        return cities.stream().flatMap(city ->
                        flKeys.stream()
                                .flatMap(key -> ServerData.reverseFlGroups.containsKey(key)
                                        ? Distances.getGeoExplanations(ServerData.reverseFlGroups.get(key), city, maxFormationsPerFiliere).stream()
                                        : Distances.getGeoExplanations(List.of(key), city, maxFormationsPerFiliere).stream()
                                )
                ).filter(Objects::nonNull)
                .sorted(Comparator.comparing(ExplanationGeo::distance))
                .toList();
    }



    public record Request(
            @ArraySchema(arraySchema = @Schema(name = "geo_pref", description = "villes préférées pour étudier", example = "[\"Soulac-sur-Mer\",\"Nantes\"]", required = false))
            @Nullable Set<String> geo_pref,

            @ArraySchema(arraySchema = @Schema(name = "keys", description = "clés", example = "[\"fl2014\",\"fl2007\"]", required = false))
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

        List<String> gtas = getGeographicInterests(
                req.keys(),
                req.geo_pref(),
                2
        ).stream().map(ExplanationGeo::form).toList();

        return new Response(gtas);
    }


}

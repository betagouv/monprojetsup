package fr.gouv.monprojetsup.app.services.info;

import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.common.dto.ExplanationGeo;
import fr.gouv.monprojetsup.common.dto.ProfileDTO;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.distances.Distances;
import fr.gouv.monprojetsup.data.model.stats.StatsContainers;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;

import static fr.gouv.monprojetsup.data.distances.Distances.getGeoExplanations;
import static java.lang.Math.min;

@Service
public class GetDetailsService extends MyService<GetDetailsService.Request, GetDetailsService.Response> {


    public GetDetailsService() {
        super(Request.class);
    }

    public record Request(

            @Schema(description = "Profil utilisé pour générer les détails.")
            @NotNull ProfileDTO profile,
            @ArraySchema(arraySchema = @Schema(description = "Liste des clés des formations dont les détails sont demandés.", example = "[\"fl201\",\"fl11\"]"))
            @NotNull List<String> keys

    ) {

    }

    public record DetailedSuggestion(
            @Schema(description = "clé", example = "fl2014")
            @NotNull String key,
            @ArraySchema(arraySchema = @Schema(description = "formations d'intérêt", example = "[\"ta201\",\"ta123\"]"))
            @NotNull List<String> fois,
            @ArraySchema(arraySchema = @Schema(description = "villes disponibles, triées par affinité décroissantes", example = "[\"Nantes\",\"Melun\"]"))
            @NotNull List<String> cities,
            @Schema(description = "statistiques 'admission",  required = true)
            @NotNull StatsContainers.SimpleStatGroupParBac stats
    ) {
    }
    public record Response(
            ResponseHeader header,
            @Schema(
                    name = "details",
                    description =
                            """
                               Permet de récupérer des détails sur une liste de formations, en fonction d'un profil.
                               Chaque détail inclut:
                               * des explications sur les éléments de proximité entre le profil et la formation
                               * des exemples de métiers associées, triés par affinité décroissante
                               * des lieux de formations d'intérêts, proches des villes d'intérêts du profil
                               * des villes disponibles pour faire cette formation, triées par ordre décroissant de disctance avec les villes d'intérêts du profil
                               * des stats sur la formation, adaptées au type de bac du profil
                               """,
                    required = true
            )
            List<DetailedSuggestion> details
    ) {

        public Response(@NotNull List<DetailedSuggestion>  suggestions) {
            this(
                    new ResponseHeader(),
                    suggestions
            );
        }

    }

    @Override
    protected @NotNull Response handleRequest(@NotNull GetDetailsService.Request req) throws Exception {

        //LOGGER.info("HAndling request " + req);
        final @NotNull List<DetailedSuggestion> suggestions = getDetails(
                req.profile,
                req.keys
        );

        return new GetDetailsService.Response(suggestions);
    }


    /**
     * Get all details about details
     *
     * @param pf the profile
     * @param keys the list of keys for which details are required
     * @return the details
     */
    public static List<DetailedSuggestion> getDetails(ProfileDTO pf, List<String> keys) {



        List<DetailedSuggestion> result = new ArrayList<>();

        keys.forEach(key -> {

            /* formations of interest */
            val fois = getGeographicInterests(
                    List.of(key),
                    pf.geo_pref(),
                    2
            ).stream().map(ExplanationGeo::form).toList();

            /* cities */
            val citiesDistances = new HashMap<String, Integer>();
            getGeographicInterests(
                    List.of(key),
                    pf.geo_pref(),
                    Integer.MAX_VALUE
            ).forEach(e ->
                    citiesDistances.put(
                            e.city(),
                            min(citiesDistances.getOrDefault(e.city(), Integer.MAX_VALUE), e.distance())
                    )
            );

            val cities = Distances.getCities(key, pf.geo_pref());//citiesDistances.keySet().stream().sorted(Comparator.comparing(citiesDistances::get)).toList();

            val stats = ServerData.getSimpleGroupStats(
                    pf.bac(),
                    key);

            result.add(
                    new DetailedSuggestion(
                            key,
                            fois,
                            cities,
                            stats
                    )
            );
        });

        return result;

    }

    public static List<ExplanationGeo> getGeographicInterests(@Nullable List<String> flKeys, @Nullable Set<String> cities, int maxFormationsPerFiliere) {
        if(flKeys == null || cities == null || flKeys.isEmpty() || cities.isEmpty()) return Collections.emptyList();
        return cities.stream().flatMap(city ->
                        flKeys.stream()
                                .flatMap(key -> ServerData.reverseFlGroups.containsKey(key)
                                        ? getGeoExplanations(ServerData.reverseFlGroups.get(key), city, maxFormationsPerFiliere).stream()
                                        : getGeoExplanations(List.of(key), city, maxFormationsPerFiliere).stream()
                                )
                ).filter(Objects::nonNull)
                .sorted(Comparator.comparing(ExplanationGeo::distance))
                .toList();
    }


}

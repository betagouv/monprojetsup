package fr.gouv.monprojetsup.app.services.info;

import com.google.gson.Gson;
import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.data.Helpers;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.distances.Distances;
import fr.gouv.monprojetsup.data.dto.*;
import fr.gouv.monprojetsup.data.dto.GetAffinitiesServiceDTO.Affinity;
import fr.gouv.monprojetsup.data.model.Explanation;
import fr.gouv.monprojetsup.data.model.stats.StatsContainers;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.distances.Distances.getGeoExplanations;

@Service
public class SearchService extends MyService<SearchService.Request, SearchService.Response> {

    protected static boolean USE_LOCAL_URL = true;

    public static final String LOCAL_URL = "http://localhost:8004/api/1.2/";
    public static final String REMOTE_URL = "https://monprojetsup.fr/";


    public SearchService() {
        super(Request.class);
    }

    public record Request(

            @Schema(description = "Recherche de formation.")
            boolean includeFormations,

            @Schema(description = "Recherche de metier.")
            boolean includeMetiers,

            @Schema(description = "Taille de page.")
            int pageSize,

            @Schema(description = "Numéro de page.")
            int pageNb,

            @Schema(description = "Contenu de la barre de recherche.")
            @NotNull String recherche,

            @Schema(description = "Profil utilisé pour générer les détails.")
            @NotNull ProfileDTO profile

    ) {

    }

    public record ResultatRecherche(
            @Schema(description = "clé", example = "fl2014")
            @NotNull String key,
            @Schema(description = "fait partie des favoris")
            boolean fav,
            @Schema(description = "affinite entre 0.0 et 1.0, arrondie à 6 décimales", example = "0.8")
            double affinity,
            @Schema(description = "score de recherche", example = "2")
            int searchScore,
            @Schema(description = "type", example = "formation", allowableValues = {"formation", "metier"})
            @NotNull String type,
            @ArraySchema(arraySchema = @Schema(description = "formations d'intérêt", example = "[\"ta201\",\"ta123\"]"))
            @NotNull List<String> fois,
            @ArraySchema(arraySchema = @Schema(description = "villes disponibles, triées par affinité décroissantes", example = "[\"Nantes\",\"Melun\"]"))
            @NotNull List<String> cities,
            @Schema(description = "statistiques 'admission")
            @NotNull StatsContainers.SimpleStatGroupParBac stats,
            @ArraySchema(arraySchema = @Schema(description = "explications", allOf = Explanation.class))
            @NotNull List<Explanation> explanations,
            @ArraySchema(arraySchema = @Schema(description = "examples de métiers, triés par affinité décroissante", example = "[\"met_129\",\"met_84\",\"met_5\"]"))
            @NotNull List<String> examples

    ) {
        public double sortScore() {
            return 100 * searchScore + affinity;
        }
    }
    public record Response(
            ResponseHeader header,
            @Schema(
                    name = "details",
                    description =
                            """
                               Détails sur une liste de formations, en fonction d'un profil.
                               Chaque détail inclut:
                               * des explications sur les éléments de proximité entre le profil et la formation
                               * des exemples de métiers associées, triés par affinité décroissante
                               * des lieux de formations d'intérêts, proches des villes d'intérêts du profil
                               * des villes disponibles pour faire cette formation, triées par ordre décroissant de disctance avec les villes d'intérêts du profil
                               * des stats sur la formation, adaptées au type de bac du profil
                               """,
                    required = true
            )
            @NotNull List<ResultatRecherche> details
    ) {

        public Response(@NotNull List<ResultatRecherche>  suggestions) {
            this(
                    new ResponseHeader(),
                    suggestions
            );
        }

    }

    @Override
    protected @NotNull Response handleRequest(@NotNull SearchService.Request req) throws Exception {

        Map<String, Integer> searchScores = ServerData.search(req.recherche);

        //equivalent d'un appel à /affinites
        final Pair<List<Affinity>, List<String>> affinites = getAffinities(
                req.profile
        );

        if(!req.recherche.trim().isEmpty()) {
            affinites.getLeft().removeIf(aff -> !searchScores.containsKey(aff.key()));
        }
        //calcul des cores de tri
        Map<String, Double> sortScores
                = affinites.getLeft().stream().collect(
                        Collectors.toMap(
                            Affinity::key,
                            aff -> aff.getSortScore(searchScores.getOrDefault(aff.key(), 0))
                        )
        );

        //tri des clés
        List<String> keys =
                affinites.getLeft().stream()
                        .sorted(Comparator.comparing(aff -> - sortScores.getOrDefault(aff.key(), 0.0)))
                        .map(Affinity::key)
                        .toList();

        //calcul des clés de la page
        List<String> keysPage = keys.stream().skip((long) req.pageNb * req.pageSize).limit(req.pageSize).toList();

        final @NotNull List<ResultatRecherche> suggestions = getDetails(
                req.profile,
                keysPage,
                affinites.getLeft().stream().collect(Collectors.toMap(Affinity::key, Affinity::affinite)),
                searchScores
        );

        return new SearchService.Response(suggestions);
    }

    //
    private static Pair<List<Affinity>, List<String>> getAffinities(ProfileDTO profile) throws IOException, InterruptedException {
        val request = new GetAffinitiesServiceDTO.Request(profile);
        String responseJson = post((USE_LOCAL_URL ? LOCAL_URL : REMOTE_URL) + "affinites", request);
        val response = new Gson().fromJson(responseJson, GetAffinitiesServiceDTO.Response.class);
        return Pair.of(response.affinites(), response.metiers());
    }

    @NotNull
    private static List<GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples> getExplanationsandExamples(
            ProfileDTO profile,
            List<String> keys)
            throws IOException, InterruptedException {
        val request = new GetExplanationsAndExamplesServiceDTO.Request(profile, keys);
        String responseJson = post((USE_LOCAL_URL ? LOCAL_URL : REMOTE_URL) + "explanations", request);
        val response = new Gson().fromJson(responseJson, GetExplanationsAndExamplesServiceDTO.Response.class);
        return response.liste();
    }


    private static synchronized String post(String url, Object obj) throws IOException, InterruptedException {
        String requestBody = new Gson().toJson(obj);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response =
                HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != 200) {
            throw new RuntimeException("Error: " + response.statusCode() + " " + response.body());
        }
        return response.body();
    }

    /**
     * Get all details about some formations
     *
     * @param pf           the profile
     * @param keys         the list of keys for which details are required
     * @param affinites    the affinities
     * @param searchScores the search scores
     * @return the details
     */
    public static List<ResultatRecherche> getDetails(
            ProfileDTO pf,
            List<String> keys,
            @NotNull Map<String, Double> affinites,
            Map<String, Integer> searchScores) throws IOException, InterruptedException {

        List<ResultatRecherche> result = new ArrayList<>();

        val eae = getExplanationsandExamples(pf, keys);
        if(eae.size() != keys.size()) throw new RuntimeException("Error: " + eae.size() + " explanations for " + keys.size() + " request ");

        int i = 0;
        for (String key : keys) {
            val fois = getGeographicInterests(
                    List.of(key),
                    pf.geo_pref(),
                    2
            ).stream().map(ExplanationGeo::form).toList();

            val cities = Distances.getCities(key, pf.geo_pref());

            val stats = ServerData.getSimpleGroupStats(
                    pf.bac(),
                    key
            );

            val eaee = eae.get(i);

            result.add(
                    new ResultatRecherche(
                            key,
                            pf.isFavori(key),
                            affinites.getOrDefault(key, 0.0),
                            searchScores.getOrDefault(key, 0),
                            Helpers.isFiliere(key) ? "formation" : "metier",
                            fois,
                            cities,
                            stats,
                            eaee.explanations(),
                            eaee.examples()
                    )
            );
            i++;
        }

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

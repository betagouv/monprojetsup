package fr.gouv.monprojetsup.suggestions.analysis;

import com.google.gson.Gson;
import fr.gouv.monprojetsup.common.dto.SuggestionDTO;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.suggestions.algos.Explanation;
import fr.gouv.monprojetsup.suggestions.algos.Suggestion;
import fr.gouv.monprojetsup.common.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import fr.gouv.monprojetsup.suggestions.services.GetExplanationsAndExamplesService;
import fr.gouv.monprojetsup.suggestions.services.GetSuggestionsService;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.CENTRE_INTERETS_ONISEP;
import static fr.gouv.monprojetsup.data.Constants.CENTRE_INTERETS_ROME;
import static fr.gouv.monprojetsup.data.ServerData.*;
import static fr.gouv.monprojetsup.suggestions.analysis.ReferenceCases.ReferenceCase.*;
import static java.lang.Math.min;
import static java.lang.System.lineSeparator;



public record ReferenceCases(
        List<ReferenceCase> cases
) {

    public ReferenceCases() {
        this(new ArrayList<>());
    }

    public static String toExplanationString(List<SuggestionDTO> suggestions, String sep) {
        if (suggestions == null) return sep;
        return suggestions.stream()
                .map(s -> getDebugLabel(s.fl()))
                .reduce(sep + sep, (a, b) -> a + "\n" + sep + sep + b);
    }

    public static String toExplanationString2(List<String> interests, String sep) {
        return interests.stream()
                .map(e -> getDebugLabel(e))
                .reduce(sep + sep, (a, b) -> a + "\n" + sep + sep + b);
    }

    public static String toExplanations(List<String> list, String sep) {
        return list.stream()
                .map(e -> getDebugLabel(e))
                .reduce(sep + sep, (a, b) -> a + "\n" + sep + sep + b);
    }

    public static String toExplanationString(ProfileDTO pf) {
        return "Profil: \n" + toExplanationStringShort(pf, "\t") +
                "\n\tcentres d'intérêts: " + toExplanationString2(pf.interests(), "\t") + "\n" +
                "\n\tformations et métiers d'intérêt: " + toExplanationString(pf.suggApproved(), "\t") + "\n" +
                "\n\tcorbeille (refus / rejet): " + toExplanationString(pf.suggRejected(), "\t") + "\n" +
                '}';
    }

    public static String toExplanationStringShort(ProfileDTO pf, String sep) {
        return sep + "niveau: '" + pf.niveau() + "'\n" +
                sep + "bac: '" + pf.bac() + "'\n" +
                sep + "duree: '" + pf.duree() + "'\n" +
                sep + "apprentissage: '" + Evaluate.toApprentissageExplanationString(pf.apprentissage()) + "'\n" +
                sep + "geo_pref: " + pf.geo_pref() + "'\n" +
                sep + "spe_classes: " + pf.spe_classes() + "'\n" +
                sep + "moyenne générale auto-évaluée: '" + pf.moygen() + "'\n";
    }

    public void toFile(String refCasesWithSuggestions) throws IOException {
        Serialisation.toJsonFile(refCasesWithSuggestions, this, true);
    }

    public void toDetails(String prefix, boolean includeDetails) throws IOException {
        int i = 1;
        for (ReferenceCases.ReferenceCase result : cases) {
            if (result != null) {
                String name = result.name();
                if(name.length() > 20) name = name.substring(0, 20);
                String outputFilename = prefix + " profil " + i +  (name.startsWith("ProfileDTO") ? "" : " - " +name) + ".txt";
                result.toFile(outputFilename, includeDetails);
            }
            i++;
        }
    }

    public static String evaluate(ProfileDTO pf, String expectation)
            throws IOException, InterruptedException {
        GetExplanationsAndExamplesService.Response responseExpl =

                getExplanationsAndExamples(pf, expectation);
        return responseExpl.explanations().stream().map(Explanation::toExplanation)
                .collect(Collectors.joining("\t\n", "\t", "\n"));
    }

    public static GetExplanationsAndExamplesService.Response getExplanationsAndExamples(ProfileDTO pf, String fl) throws IOException, InterruptedException {
        return callExplanationsService(
                new GetExplanationsAndExamplesService.Request(
                        pf,
                        fl
                )
        );

    }


    public static Logger LOGGER = Logger.getLogger(ReferenceCases.class.getName());


    public record ReferenceCase(
            String name,
            ProfileDTO pf,
            List<String> expectations,

            //stores label to explanations
            List<Suggestion> suggestions
    ){
        public static final String STAR_SEP = "********************************************************************\n";

        protected static boolean USE_LOCAL_URL = true;

        public static final String LOCAL_URL = "http://localhost:8002/";
        public static final String REMOTE_URL = "https://monprojetsup.fr/";

        public static void useRemoteUrl(boolean useRemote) {
            USE_LOCAL_URL = !useRemote;
        }

        public ReferenceCase(String explanationString, ProfileDTO profile) {
            this(explanationString, profile, new ArrayList<>(), new ArrayList<>());
        }

        public void toFile(String outputFilename, boolean includeDetails) throws IOException {
            outputFilename = outputFilename
                    .replace("\\","_")
                    .replace("#","_")
                    .replace("/","_")
            ;
            try (
                    OutputStreamWriter fos = new OutputStreamWriter(
                            Files.newOutputStream(Path.of(outputFilename)),
                            StandardCharsets.UTF_8
                    )
            ) {
                fos.write(STAR_SEP);
                fos.write("************ Profile " + (name == null || name.startsWith("ProfileDTO") ? "" : name) + " ***********\n");
                fos.write(STAR_SEP);
                fos.write("Profile: " + toExplanationString(pf));
                fos.write(lineSeparator() + STAR_SEP + lineSeparator() );
                if(expectations != null && !expectations.isEmpty()) {
                    fos.write("Suggestions définies par experts:\n" + expectations.stream()
                            .collect(Collectors.joining("\n\t", "\t", "\n")));
                } else {
                    fos.write("Aucune suggestion définie par experts\n");
                }
                fos.write(lineSeparator() + STAR_SEP + lineSeparator() );
                fos.write("Suggestions effectuées par l'algorithme:\n\n" + suggestions.stream().map(e -> ServerData.getDebugLabel(e.fl()))
                        .collect(Collectors.joining("\n\t", "\t", "\n")));
                //fos.write(lineSeparator() + STAR_SEP + lineSeparator() );
                if(includeDetails) {
                    for (Suggestion entry : suggestions) {
                        String label = ServerData.getDebugLabel(entry.fl());
                        fos.write(lineSeparator() + STAR_SEP + lineSeparator());
                        fos.write("Explications détaillées sur la suggestion:" + lineSeparator());
                        fos.write(entry.humanReadable());
                    }
                }
            }
        }


    }

    public static ReferenceCases loadFromFile(String filename) throws IOException {
        return Serialisation.fromJsonFile(filename, ReferenceCases.class);
    }

    public static void main(String[] args) throws Exception {


        ReferenceCases cases = ReferenceCases.loadFromFile("referenceCases.json");

        SuggestionServer.loadConfig();

        try {
            ServerData.statistiques = new PsupStatistiques();
            ServerData.statistiques.labels = Serialisation.fromJsonFile(
                    "labelsDebug.json",
                      Map.class
            );
        } catch (Exception e) {
            SuggestionServer server = new SuggestionServer();
            server.init();
        }


        /*
        //uncomment to anonymize the reference cases
        //cases.cases.stream().forEach(c -> c.pf.anonymize());
        List<ReferenceCase> newCases = cases.cases.stream().map(c -> c.makeHumanReadable()).toList();
        Serialisation.toJsonFile("referenceCasesAnonymized.json", newCases, true);
        */

        try (
                OutputStreamWriter fos = new OutputStreamWriter(
                        Files.newOutputStream(Path.of("profiles_expectations_suggestions.txt")),
                        StandardCharsets.UTF_8
                )
        ) {
            fos.write(cases.resume());
        }
    }

    public String resume() {
        StringBuilder fos = new StringBuilder();
        int i = 1;
        for (ReferenceCase refCase : cases) {
            fos.append(lineSeparator()).append(lineSeparator()).append(lineSeparator()).append(STAR_SEP);
            fos.append("************ Profile ").append(i).append(" ***********\n");
            fos.append("************ ").append(refCase.name()).append(" ***********\n");
            fos.append(STAR_SEP);
            fos.append(toExplanationString(refCase.pf));
            i++;
            if(refCase.expectations != null) {
                fos.append("\n\n" + STAR_SEP);
                fos.append("Suggestions définies par expertise:\n\n").append(refCase.expectations.stream()
                        .collect(Collectors.joining("\n\t", "\t", "\n")));
            }
            if(refCase.suggestions != null) {
                fos.append("\n\n" + STAR_SEP);
                fos.append("Suggestions calculées par algorithme:\n\n").append(refCase.suggestions.stream()
                        .map(e -> ServerData.getDebugLabel(e.fl()))
                        .collect(Collectors.joining("\n\t", "\t", "\n")));
                /*fos.write(lineSeparator() + STAR_SEP);
                    fos.write("Explication détaillées sur la suggestion:" +  lineSeparator());
                        fos.write(entry.humanReadable());*/

            }
        }
        return fos.toString();
    }

    public void resumeCsv(String filename) throws IOException {

        try (CsvTools output = new CsvTools(filename, ',')) {
            output.append(List.of(
                    "Nom",
                    "Profil",
                    "Goûts et compétences",//Intérets
                    "Centres intérêts",//thematiques
                    "Idées et Favoris Métiers",//isMetier
                    "Idées et Favoris Formatione",//isFormation
                    "Corbeille (refus / pas intéressé)",
                    "Suggestions définies par experts",
                    "Suggestions effectuées par l'algorithme",
                    "Sujets de discussion avec le lycéen",
                    "Notes et Remarques")
            );
            int i = 1;
            Set<String> interets = new HashSet<>(List.of(CENTRE_INTERETS_ROME, CENTRE_INTERETS_ONISEP));

            for (ReferenceCase refCase : cases) {
                String name = "Profil " + i;
                i++;
                if (refCase.name() != null && !refCase.name.contains("ProfileDTO")) name = refCase.name();
                output.append(name);
                output.append(toExplanationStringShort(refCase.pf, ""));

                output.append(toExplanations(refCase.pf.interests().stream()
                        .filter(e -> isInteret(e)
                        )
                        .toList(),""));

                output.append(toExplanations(refCase.pf.interests().stream()
                        .filter(e -> isTheme(e)
                        )
                        .toList(),""));

                output.append(toExplanationString(refCase.pf.suggApproved().stream().filter(s -> isMetier(s.fl())).toList(), ""));
                output.append(toExplanationString(refCase.pf.suggApproved().stream().filter(s -> isFiliere(s.fl())).toList(), ""));
                output.append(toExplanationString(refCase.pf.suggRejected(), ""));
                output.append(
                        refCase.expectations == null ? "" :
                                refCase.expectations.stream()
                                        .collect(
                                                Collectors.joining("\n")
                                        )
                );
                output.append(toExplanationString(refCase.suggestions.stream().map(s -> s.toDTO()).toList(), ""));
                output.append("");
                output.append("");
                output.newLine();
            }
        }
    }


    public @Nullable ReferenceCase getSuggestionsAndExplanations(
            ReferenceCase refCase
    ) throws IOException, InterruptedException {
        if (refCase.pf == null) return null;
        List<GetSuggestionsService.SuggestionsDTO.Suggestion> suggestions = callSuggestionsService(refCase.pf);

        ReferenceCase answer = new ReferenceCase(
                refCase.name,
                refCase.pf,
                refCase.expectations,
                new ArrayList<>()
        );

        LOGGER.info("\tgetting details #");
        AtomicInteger j = new AtomicInteger(1);

        answer.suggestions.addAll(
                suggestions
                        .stream()
                        .map(suggestion -> {

                                    LOGGER.info("\tgetting explanations for suggestion #" + j.get() + " "
                                            + ServerData.getDebugLabel(suggestion.fl()));
                                    j.getAndIncrement();

                            GetExplanationsAndExamplesService.Response responseExpl = null;
                            try {
                                responseExpl = callExplanationsService(
                                        new GetExplanationsAndExamplesService.Request(
                                                refCase.pf,
                                                suggestion.fl()
                                        )
                                );
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            return
                                            Suggestion.getPendingSuggestion(
                                                    suggestion.fl(),
                                                    responseExpl.explanations(),
                                                    List.of()
                                            );
                                }
                        )
                        .toList()
        );
        return answer;
    }


    public ReferenceCases getSuggestionsAndExplanations(Integer restricttoIndex) {
        AtomicInteger i = new AtomicInteger(1);

        val results = new ReferenceCases();
        results.cases.addAll(
                cases
                        .stream()
                        .map(refCase -> {
                            try {
                                String name = refCase.name();
                                if (restricttoIndex != null && i.get() != restricttoIndex) {
                                    i.incrementAndGet();
                                    return null;
                                }
                                String nameCase = "case " + (i.getAndIncrement());
                                name  = (name == null) ? nameCase : nameCase + name.substring(0, min(20,name.length()));
                                LOGGER.info("getting suggestion and explanations for " + name);
                                return getSuggestionsAndExplanations(refCase);
                            } catch (IOException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .filter(Objects::nonNull)
                        .toList()
        );
        return results;
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

    public static GetExplanationsAndExamplesService.Response callExplanationsService(
            GetExplanationsAndExamplesService.Request request
    ) throws IOException, InterruptedException {
        String response = post((USE_LOCAL_URL ? LOCAL_URL : REMOTE_URL) + "api/1.1/public/explanations", request);
        return new Gson().fromJson(response, GetExplanationsAndExamplesService.Response.class);
    }

    public static List<GetSuggestionsService.SuggestionsDTO.Suggestion> callSuggestionsService(ProfileDTO pf) throws IOException, InterruptedException {
        return callSuggestionsService(new GetSuggestionsService.Request(pf)).suggestions().suggestions();
    }
    private static GetSuggestionsService.Response callSuggestionsService(GetSuggestionsService.Request pf) throws IOException, InterruptedException {
        String url = (USE_LOCAL_URL ? LOCAL_URL : REMOTE_URL) + "api/1.1/public/details";
        String response = post(url, pf);
        return new Gson().fromJson(response, GetSuggestionsService.Response.class);
    }


}

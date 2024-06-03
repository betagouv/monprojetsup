package fr.gouv.monprojetsup.suggestions.analysis;

import com.google.gson.Gson;
import fr.gouv.monprojetsup.data.Helpers;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.dto.GetAffinitiesServiceDTO;
import fr.gouv.monprojetsup.data.dto.GetExplanationsAndExamplesServiceDTO;
import fr.gouv.monprojetsup.data.dto.ProfileDTO;
import fr.gouv.monprojetsup.data.dto.SuggestionDTO;
import fr.gouv.monprojetsup.data.model.Explanation;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import fr.gouv.monprojetsup.suggestions.algos.Suggestion;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.ServerData.getDebugLabel;
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
        return interests == null ? "" : interests.stream()
                .map(ServerData::getDebugLabel)
                .reduce(sep + sep, (a, b) -> a + "\n" + sep + sep + b);
    }

    public static String toExplanations(List<String> list, String sep) {
        return list == null ? "" : list.stream()
                .map(ServerData::getDebugLabel)
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
        val responseExpl =

                getExplanationsAndExamples(pf, expectation);
        if (responseExpl.liste().size() != 1)
            throw new RuntimeException("unexpected number of explanations");

        return responseExpl.liste().get(0).explanations().stream().map(Explanation::toExplanation)
                .collect(Collectors.joining("\t\n", "\t", "\n"));
    }

    public static GetExplanationsAndExamplesServiceDTO.Response getExplanationsAndExamples(ProfileDTO pf, String fl) throws IOException, InterruptedException {
        return callExplanationsService(
                new GetExplanationsAndExamplesServiceDTO.Request(
                        pf,
                        List.of(fl)
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

        public static boolean USE_LOCAL_URL = true;


        public static final String LOCAL_URL = "http://localhost:8004/api/1.2/";
        public static final String REMOTE_URL = "https://monprojetsup.fr/";


        public static void useRemoteUrl(boolean useRemote) {
            USE_LOCAL_URL = !useRemote;
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
            ServerData.statistiques.labels =
                    Serialisation.<Map<String,String>>fromJsonFile(
                            "labelsDebug.json",
                            Map.class
                    );
        } catch (Exception e) {
            SuggestionServer server = new SuggestionServer();
            server.init();
        }

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
            }
        }
        return fos.toString();
    }

    public void resumeCsv(String filename) throws IOException {

        try (CsvTools output = new CsvTools(filename, ',')) {
            output.appendHeaders(List.of(
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

            for (ReferenceCase refCase : cases) {
                String name = "Profil " + i;
                i++;
                if (refCase.name() != null && !refCase.name.contains("ProfileDTO")) name = refCase.name();
                output.append(name);
                output.append(toExplanationStringShort(refCase.pf, ""));

                output.append(toExplanations(refCase.pf.interests().stream()
                        .filter(Helpers::isInteret
                        )
                        .toList(),""));

                output.append(toExplanations(refCase.pf.interests().stream()
                        .filter(Helpers::isTheme
                        )
                        .toList(),""));

                output.append(toExplanationString(refCase.pf.suggApproved().stream().filter(s -> Helpers.isMetier(s.fl())).toList(), ""));
                output.append(toExplanationString(refCase.pf.suggApproved().stream().filter(s -> Helpers.isFiliere(s.fl())).toList(), ""));
                output.append(toExplanationString(refCase.pf.suggRejected(), ""));
                output.append(
                        refCase.expectations == null ? "" :
                                String.join("\n", refCase.expectations)
                );
                output.append(toExplanationString(refCase.suggestions.stream().map(Suggestion::toDTO).toList(), ""));
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
        val suggestions = callSuggestionsService(refCase.pf);

        ReferenceCase answer = new ReferenceCase(
                refCase.name,
                refCase.pf,
                refCase.expectations,
                new ArrayList<>()
        );

        LOGGER.info("\tgetting details #");
        AtomicInteger j = new AtomicInteger(1);

        List<Suggestion> list = new ArrayList<>();
        for (val suggestion1 : suggestions) {

            LOGGER.info("\tgetting explanations for suggestion #" + j.get() + " "
                    + getDebugLabel(suggestion1.key()));
            j.getAndIncrement();

            GetExplanationsAndExamplesServiceDTO.Response responseExpl;
            try {
                responseExpl = callExplanationsService(
                        new GetExplanationsAndExamplesServiceDTO.Request(
                                refCase.pf,
                                List.of(suggestion1.key())
                        )
                );
                if (responseExpl.liste().size() != 1) throw new RuntimeException("unexpected number of explanations");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Suggestion unexpectedNumberOfExplanations = Suggestion.getPendingSuggestion(
                    suggestion1.key(),
                    responseExpl.liste().get(0).explanations(),
                    List.of()
            );
            list.add(unexpectedNumberOfExplanations);
        }
        answer.suggestions.addAll(
                list
        );
        return answer;
    }


    public ReferenceCases getSuggestionsAndExplanations(Integer restricttoIndex) {
        AtomicInteger i = new AtomicInteger(1);

        val results = new ReferenceCases();
        val list = cases
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
                .toList();
        results.cases.addAll(
                list
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


    public static GetExplanationsAndExamplesServiceDTO.Response callExplanationsService(
            GetExplanationsAndExamplesServiceDTO.Request request
    ) throws IOException, InterruptedException {
        String response = post((USE_LOCAL_URL ? LOCAL_URL : REMOTE_URL) + "explanations", request);
        return new Gson().fromJson(response, GetExplanationsAndExamplesServiceDTO.Response.class);
    }

    public static List<GetAffinitiesServiceDTO.Affinity> callSuggestionsService(ProfileDTO pf) throws IOException, InterruptedException {
        String url = (USE_LOCAL_URL ? LOCAL_URL : REMOTE_URL) + "suggestions";
        String response = post(url, new GetAffinitiesServiceDTO.Request(pf));
        return new Gson().fromJson(response, GetAffinitiesServiceDTO.Response.class).affinites();
    }


}

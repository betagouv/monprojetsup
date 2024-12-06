package fr.gouv.monprojetsup.suggestions.export.experts;

import com.google.gson.Gson;
import fr.gouv.monprojetsup.data.tools.CsvTools;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.suggestions.algo.Suggestion;
import fr.gouv.monprojetsup.suggestions.dto.GetAffinitiesServiceDTO;
import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.dto.ChoiceDTO;
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

import static java.lang.Math.min;
import static java.lang.System.lineSeparator;



public record ReferenceCases(
        List<ReferenceCase> cases
) {

    public static final String STAR_SEP = "********************************************************************\n";

    public static boolean USE_LOCAL_URL = false;


    public static final String LOCAL_URL = "http://localhost:8004/api/1.2/";
    public static final String REMOTE_URL = "https://beta.monprojetsup.fr/api/1.2/";

    public static void useRemoteUrl(boolean useRemote) {
        USE_LOCAL_URL = !useRemote;
    }

    public ReferenceCases() {
        this(new ArrayList<>());
    }

    public static String toExplanationString(List<ChoiceDTO> suggestions, String sep, Map<String,String> labels) {
        if (suggestions == null) return sep;
        return suggestions.stream()
                .map(s -> labels.getOrDefault(s.id(), s.id()))
                .reduce(sep + sep, (a, b) -> a + "\n" + sep + sep + b);
    }

    public static String toExplanationString2(List<String> interests, String sep, Map<String,String> labels) {
        return interests == null ? "" : interests.stream()
                .map(s -> labels.getOrDefault(s,s))
                .reduce(sep + sep, (a, b) -> a + "\n" + sep + sep + b);
    }

    public static String toExplanations(List<String> list, String sep, Map<String,String> labels) {
        return list == null ? "" : list.stream()
                .map(s -> labels.getOrDefault(s, s))
                .reduce(sep + sep, (a, b) -> a + "\n" + sep + sep + b);
    }

    public static String toExplanationString(ProfileDTO pf, Map<String,String> labels) {
        return "Profil: \n" + toExplanationStringShort(pf, "\t") +
                "\n\tcentres d'intérêts: " + toExplanationString2(pf.interests(), "\t", labels) + "\n" +
                "\n\tformations et métiers d'intérêt: " + toExplanationString(pf.suggApproved(), "\t", labels) + "\n" +
                "\n\tcorbeille (refus / rejet): " + toExplanationString(pf.suggRejected(), "\t", labels) + "\n" +
                '}';
    }

    public static String toExplanationStringShort(ProfileDTO pf, String sep) {
        return sep + "niveau: '" + pf.niveau() + "'\n" +
                sep + "bac: '" + Objects.requireNonNullElse(pf.bac(),"null") + "'\n" +
                sep + "duree: '" + pf.duree() + "'\n" +
                sep + "apprentissage: '" + SuggestionsEvaluator.toApprentissageExplanationString(pf.apprentissage()) + "'\n" +
                sep + "geo_pref: " + pf.geo_pref() + "'\n" +
                sep + "spe_classes: " + pf.spe_classes() + "'\n" +
                sep + "moyenne générale auto-évaluée: '" + pf.moygen() + "'\n";
    }

    public void toFile(String refCasesWithSuggestions) throws IOException {
        Serialisation.toJsonFile(refCasesWithSuggestions, this, true);
    }

    public void toDetails(String prefix, boolean includeDetails, Map<String,String> labels) throws IOException {
        int i = 1;
        for (ReferenceCase result : cases) {
            if (result != null) {
                String name = result.name();
                if(name.length() > 20) name = name.substring(0, 20);
                String outputFilename = prefix + " profil " + i +  (name.startsWith("ProfileDTO") ? "" : " - " +name) + ".txt";
                toFile(result, outputFilename, includeDetails, labels);
            }
            i++;
        }
    }


    private void toFile(ReferenceCase result, String outputFilename, boolean includeDetails, Map<String,String> labels) throws IOException {
        outputFilename = outputFilename
                .replace("\\", "_")
                .replace("#", "_")
                .replace("/", "_")
        ;
        try (
                OutputStreamWriter fos = new OutputStreamWriter(
                        Files.newOutputStream(Path.of(outputFilename)),
                        StandardCharsets.UTF_8
                )
        ) {
            fos.write(STAR_SEP);
            fos.write("************ Profile " + (result.name() == null || result.name().startsWith("ProfileDTO") ? "" : result.name()) + " ***********\n");
            fos.write(STAR_SEP);
            fos.write("Profile: " + ReferenceCases.toExplanationString(result.pf(), labels));
            fos.write(lineSeparator() + STAR_SEP + lineSeparator());
            val expectations = result.expectations();
            if (expectations != null && !expectations.isEmpty()) {
                fos.write("Suggestions définies par experts:\n" + expectations.stream()
                        .collect(Collectors.joining("\n\t", "\t", "\n")));
            } else {
                fos.write("Aucune suggestion définie par experts\n");
            }
            fos.write(lineSeparator() + STAR_SEP + lineSeparator());

            val suggestions = result.suggestions();
            fos.write("Suggestions effectuées par l'algorithme:\n\n" + suggestions.stream().map(e ->  labels.getOrDefault(e.id(), e.id()))
                    .collect(Collectors.joining("\n\t", "\t", "\n")));

            if (includeDetails) {
                for (Suggestion entry : suggestions) {
                    fos.write(lineSeparator() + STAR_SEP + lineSeparator());
                    fos.write("Explications détaillées sur la suggestion:" + lineSeparator());
                    fos.write(entry.humanReadable(labels));
                }
            }
        }
    }

    public static String evaluate(ProfileDTO pf, String expectation, Map<String,String> labels)
            throws IOException, InterruptedException {
        val responseExpl =

                getExplanationsAndExamples(pf, expectation);
        if (responseExpl.liste().size() != 1)
            throw new RuntimeException("unexpected number of explanations");

        return responseExpl.liste().get(0).explanations().stream().map(z -> z.toExplanation(labels))
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


    public static ReferenceCases loadFromFile(String filename) throws IOException {
        return Serialisation.fromJsonFile(filename, ReferenceCases.class);
    }

    public String resume(Map<String,String> labels) {
        StringBuilder fos = new StringBuilder();
        int i = 1;
        for (ReferenceCase refCase : cases) {
            fos.append(lineSeparator()).append(lineSeparator()).append(lineSeparator()).append(STAR_SEP);
            fos.append("************ Profile ").append(i).append(" ***********\n");
            fos.append("************ ").append(refCase.name()).append(" ***********\n");
            fos.append(STAR_SEP);
            fos.append(toExplanationString(refCase.pf(), labels));
            i++;
            if(refCase.expectations() != null) {
                fos.append("\n\n" + STAR_SEP);
                fos.append("Suggestions définies par expertise:\n\n").append(refCase.expectations().stream()
                        .collect(Collectors.joining("\n\t", "\t", "\n")));
            }
            if(refCase.suggestions() != null) {
                fos.append("\n\n" + STAR_SEP);
                fos.append("Suggestions calculées par algorithme:\n\n").append(refCase.suggestions().stream()
                        .map(e -> labels.getOrDefault(e.id(), e.id()))
                        .collect(Collectors.joining("\n\t", "\t", "\n")));
            }
        }
        return fos.toString();
    }

    public void resumeCsv(String filename, Map<String, String> labels) throws IOException {

        try (CsvTools output =  CsvTools.getWriter(filename)) {
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
                if (refCase.name() != null && !refCase.name().contains("ProfileDTO")) name = refCase.name();
                val line = new ArrayList<String >();
                line.add(name);
                line.add(toExplanationStringShort(refCase.pf(), ""));

                line.add(toExplanations(
                        refCase.pf().interests().stream()
                        .toList(),"", labels));

                line.add(toExplanations(refCase.pf().interests().stream()
                        .toList(),"", labels));

                line.add(toExplanationString(refCase.pf().suggApproved(), "", labels));
                line.add(toExplanationString(refCase.pf().suggRejected(), "", labels));
                line.add(
                        refCase.expectations() == null ? "" :
                                String.join("\n", refCase.expectations())
                );
                line.add(toExplanationString(refCase.suggestions().stream().map(Suggestion::toDTO).toList(), "", labels));
                line.add("");
                line.add("");
                output.append(line);
            }
        }
    }


    public @Nullable ReferenceCase getSuggestionsAndExplanations(
            ReferenceCase refCase, Map<String, String> labels
    ) throws IOException, InterruptedException {
        if (refCase.pf() == null) return null;
        List<GetAffinitiesServiceDTO.Affinity> suggestions = callSuggestionsService(refCase.pf());
        suggestions.removeIf(s -> s.affinite() < 0.01);
        suggestions = suggestions.stream().limit(20).toList();

        ReferenceCase answer = new ReferenceCase(
                refCase.name(),
                refCase.pf(),
                refCase.expectations(),
                refCase.rejections(),
                new ArrayList<>()
        );

        LOGGER.info("\tgetting details #");
        AtomicInteger j = new AtomicInteger(1);

        List<Suggestion> list = new ArrayList<>();
        for (val suggestion1 : suggestions) {

            LOGGER.info("\tgetting explanations for suggestion #" + j.get() + " "
                    + labels.getOrDefault(suggestion1.key(), suggestion1.key()));
            j.getAndIncrement();

            GetExplanationsAndExamplesServiceDTO.Response responseExpl;
            try {
                responseExpl = callExplanationsService(
                        new GetExplanationsAndExamplesServiceDTO.Request(
                                refCase.pf(),
                                List.of(suggestion1.key())
                        )
                );
                if (responseExpl.liste().size() != 1) throw new RuntimeException("unexpected number of explanations");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Suggestion unexpectedNumberOfExplanations = Suggestion.getPendingSuggestion(
                    suggestion1.key(),
                    responseExpl.liste().get(0).explanations()
            );
            list.add(unexpectedNumberOfExplanations);
        }
        answer.suggestions().addAll(
                list
        );
        return answer;
    }


    public ReferenceCases getSuggestionsAndExplanations(Integer restrictToIndex, Map<String, String> labels) {
        AtomicInteger i = new AtomicInteger(0);

        val results = new ReferenceCases();
        val now = System.currentTimeMillis();
        val list = cases
                .stream()
                .parallel()
                .map(refCase -> {
                    try {
                        String name = refCase.name();
                        if (restrictToIndex != null && i.get() != restrictToIndex) {
                            i.incrementAndGet();
                            return null;
                        }
                        String nameCase = "case " + (i.incrementAndGet());
                        name = (name == null) ? nameCase : nameCase + name.substring(0, min(20, name.length()));
                        LOGGER.info("getting suggestion and explanations for " + name);
                        return getSuggestionsAndExplanations(refCase, labels);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(Objects::nonNull)
                .toList();
        results.cases.addAll(
                list
        );
        val elapsedtimeSeconds = (System.currentTimeMillis() - now) / 1000.0;
        val timePerProfile = elapsedtimeSeconds / i.get();
        LOGGER.info("time per profile: " + timePerProfile + " s");
        return results;
    }

    private static final HttpClient client = HttpClient.newHttpClient();

    private static synchronized String post(String url, Object obj) throws IOException, InterruptedException {
        String requestBody = new Gson().toJson(obj);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
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
        String response = post(url, new GetAffinitiesServiceDTO.Request(pf, true));
        return new Gson().fromJson(response, GetAffinitiesServiceDTO.Response.class).affinites();
    }


}

package fr.gouv.monprojetsup.suggestions.analysis;

import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.suggestions.algos.Suggestion;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.gouv.monprojetsup.data.Helpers.isFiliere;
import static fr.gouv.monprojetsup.suggestions.analysis.Simulate.LOGGER;
import static fr.gouv.monprojetsup.suggestions.analysis.Simulate.REF_CASES_WITH_SUGGESTIONS;

public class Evaluate {


    private static final LevenshteinDistance levenAlgo = new LevenshteinDistance(10);

    public static void main(String[] args) throws Exception {

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

        ReferenceCases cases = ReferenceCases.loadFromFile(REF_CASES_WITH_SUGGESTIONS);


        try (
                OutputStreamWriter fos = new OutputStreamWriter(
                        Files.newOutputStream(Path.of("profiles_expectations_suggestions.txt")),
                        StandardCharsets.UTF_8
                )
        ) {
            fos.write(cases.resume());
        }

        boolean stopOnFirstKO = true;
        try (
                OutputStreamWriter fos = new OutputStreamWriter(
                        Files.newOutputStream(Path.of("evaluation.txt")),
                        StandardCharsets.UTF_8
                )
        ) {
            int i = 1;
            long last = System.currentTimeMillis();
            for (ReferenceCases.ReferenceCase refCase : cases.cases()) {
                long now = System.currentTimeMillis();
                LOGGER.info("Evaluating #" + i + " " + (now - last) + "ms");
                last = now;

                if(refCase.suggestions().isEmpty()) {
                    continue;
                }

                boolean stoppedBecauseOfKo = false;
                Map<String, Integer> suggestionsRanks = new HashMap<>();
                int k = 1;
                for (Suggestion sugg : refCase.suggestions()) {
                    if(isFiliere(sugg.fl())) {
                        suggestionsRanks.put(ServerData.getLabel(sugg.fl(), sugg.fl()), k);
                        k++;
                    }
                }
                Suggestion worstExpl = refCase.suggestions()
                        .stream().filter(s -> isFiliere(s.fl()) )
                        .reduce((suggestion, suggestion2) -> suggestion2)
                        .orElse(null);
                String worst = worstExpl == null ? "null" : worstExpl.humanReadable();
                String worstName = worstExpl == null ? "null" : ServerData.getDebugLabel(worstExpl.fl());

                if(refCase.expectations() != null && !refCase.expectations().isEmpty()) {
                    fos.write("\n\n\n***********************************************\n");
                    fos.write("************ Profile " + i + " ***********\n");
                    fos.write("************ " + refCase.name() + " ***********\n");
                    fos.write("***********************************************\n");
                    fos.write("\n");
                    fos.write("************ EXPECTATIONS ******************\n");
                    for (String expectation : refCase.expectations()) {//Todo compute distance between expectation and details
                        /*if(!expectation.contains("parcours coordination et gestion des établissements et services sanitaires ")
                        || expectation.contains("apprentissage")) continue;*/
                        int rank = getRank(expectation, suggestionsRanks);
                        if (rank > 0) {
                            fos.write("OK: rank " + rank + " " + expectation + "\n");
                        } else {
                            fos.write("    KO: " + expectation + "\n");
                            //TODO: retro details compute interests ok KO
                            try (
                                    OutputStreamWriter fos2 = new OutputStreamWriter(
                                            Files.newOutputStream(Path.of("profile " + i + " fail " + expectation.replace("\\","_")
                                                    .replace("#","_")
                                                    .replace("/","_") + ".txt")),
                                            StandardCharsets.UTF_8
                                    )
                            ) {
                                fos2.write(ReferenceCases.toExplanationString(refCase.pf()));
                                fos2.write("\n\n************ MISSED EXPECTATION ******************\n");
                                fos2.write("\n\t" + expectation
                                        + "\n\n\t\twas overtaken by \n\n\t"
                                        + worstName + "\n\n");
                                fos2.write("************ " + expectation +  "******************\n\n");
                                @Nullable String cod = getFlCodFromLabel(expectation);
                                if (cod == null) {
                                    fos2.write("could not find '" + expectation + "'");
                                } else {
                                    fos2.write(cases.evaluate(refCase.pf(), cod));
                                }
                                fos2.write("\n***********************************************************************\n");
                                fos2.write("\n************************** OVERTAKEN BY ***************************\n");
                                fos2.write("\n***********************************************************************\n");
                                fos2.write(worst);
                                /*
                                if (stopOnFirstKO) {
                                    stoppedBecauseOfKo = true;
                                    break;
                                }*/
                            }
                        }
                    }
                    fos.write("\n");
                    if(refCase.suggestions() != null) {
                        fos.write("************ ACTUAL SUGGESTIONS ******************\n");
                        fos.append(refCase.suggestions().stream()
                                .filter(s -> isFiliere(s.fl()))
                                .map(e -> ServerData.getDebugLabel(e.fl()))
                                .collect(Collectors.joining("\n\t", "\t", "\n")));
                    }


                }
                i++;
                if(stoppedBecauseOfKo && stopOnFirstKO) {
                    break;
                }
            }
            fos.flush();
        }

    }

    private static int getRank(String expectation, Map<String, Integer> suggestionsRanks) {
        List<Map.Entry<String, Integer>> z =  suggestionsRanks.entrySet().stream()
                .filter(e -> {
                            String target = e.getKey();
                            int i = target.indexOf(" groupe");
                            if(i >= 0) target = target.substring(0, i);
                            i = target.indexOf(" (");
                            if(i >= 0) target = target.substring(0, i);
                            int dist = levenAlgo.apply(expectation,target);
                            return dist >= 0 && dist < 5;
                        }
                    )
                .toList();

        return z.stream()
                .mapToInt(Map.Entry::getValue)
                .min()
                .orElse(-1);
    }


    static String toApprentissageExplanationString(String apprentissage) {
        if (apprentissage == null) return "Non-renseigné";
        if (apprentissage.equals("A")) return "Indifférent";
        if (apprentissage.equals("B")) return "Indifférent";
        if (apprentissage.equals("C")) return "Peu intéressé";
        return apprentissage;
    }


    private static boolean isIn(String expectation, Stream<String> suggested) {
        return suggested.anyMatch(s -> levenAlgo.apply(expectation, s) < 5);
    }

    public static @Nullable String getFlCodFromLabel(String expectation) {
        return
                ServerData.statistiques.labels.entrySet().stream()
                        .map(e -> Pair.of(e.getKey(), levenAlgo.apply(e.getValue(), expectation)))
                        .filter(e -> e.getRight() >= 0)
                        .min(Comparator.comparingInt(Pair::getRight))
                        .map(Pair::getLeft)
                        .orElse(null);
    }
}

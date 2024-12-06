package fr.gouv.monprojetsup.suggestions.export.experts;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.suggestions.algo.Suggestion;
import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import fr.gouv.monprojetsup.suggestions.dto.ChoiceDTO;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.suggestions.export.experts.SuggestionsGenerator.LOGGER;
import static fr.gouv.monprojetsup.suggestions.export.experts.SuggestionsGenerator.REF_CASES_WITH_SUGGESTIONS;

@Component
public class SuggestionsEvaluator {


    private final SuggestionsData data;

    @Autowired
    public SuggestionsEvaluator(SuggestionsData data) {
        this.data = data;
    }

    private static final LevenshteinDistance levenAlgo = new LevenshteinDistance(10);

    static Map<String, String> labelsNoDebug;

    public void evaluate() throws Exception {


        ReferenceCases cases = ReferenceCases.loadFromFile(REF_CASES_WITH_SUGGESTIONS);

        cases.toDetails("details", true, data.getDebugLabels());


        try (
                OutputStreamWriter fos = new OutputStreamWriter(
                        Files.newOutputStream(Path.of("profiles_expectations_suggestions.txt")),
                        StandardCharsets.UTF_8
                )
        ) {
            fos.write(cases.resume(data.getDebugLabels()));
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
            val labels = data.getDebugLabels();
            for (ReferenceCase refCase : cases.cases()) {
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
                    if(Constants.isFiliere(sugg.id())) {
                        suggestionsRanks.put(sugg.id(), k);
                        k++;
                    }
                }
                Suggestion worstExpl = refCase.suggestions()
                        .stream().filter(s -> Constants.isFiliere(s.id()) )
                        .reduce((suggestion, suggestion2) -> suggestion2)
                        .orElse(null);
                String worst = worstExpl == null ? "null" : worstExpl.humanReadable(labels);
                String worstName = worstExpl == null ? "null" : data.getLabel(worstExpl.id());

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
                            fos.write("OK rank " + rank + " \t" + expectation + "\n");
                        } else {
                            fos.write("           \t" + expectation + "\n");
                            //TODO: retro details compute interests ok KO
                            try (
                                    OutputStreamWriter fos2 = new OutputStreamWriter(
                                            Files.newOutputStream(Path.of("profile " + i + " fail " + expectation.replace("\\","_")
                                                    .replace("#","_")
                                                    .replace("/","_") + ".txt")),
                                            StandardCharsets.UTF_8
                                    )
                            ) {
                                fos2.write(ReferenceCases.toExplanationString(refCase.pf(), labels));
                                fos2.write("\n\n************ MISSED EXPECTATION ******************\n");
                                fos2.write("\n\t" + expectation
                                        + "\n\n\t\twas overtaken by \n\n\t"
                                        + worstName + "\n\n");
                                fos2.write("************ " + expectation +  "******************\n\n");
                                @Nullable String cod = getFlCodFromLabel(expectation);
                                if (cod == null) {
                                    fos2.write("could not find '" + expectation + "'");
                                } else {
                                    fos2.write(ReferenceCases.evaluate(refCase.pf(), cod, labels));
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
                                .filter(s -> Constants.isFiliere(s.id()))
                                .map(e -> getOKPrefix(refCase, e.id()) + data.getLabel(e.id()))
                                .collect(Collectors.joining("\n", "", "\n")));
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

    private static String getOKPrefix(ReferenceCase refCase, String flCodSugg) {
        val favoris = refCase.pf().suggApproved().stream()
                .map(ChoiceDTO::id)
                .filter(Constants::isFiliere)
                .collect(Collectors.toMap(fl -> fl, fl -> 1));
        int rank = getRank(flCodSugg, favoris);
        if(rank > 0) return "fav      \t";
        //si dans conseils alors
        Map<String, Integer> expectations = new HashMap<>();
        int k = 1;
        for (String expec : refCase.expectations()) {/*refCase.expectations().stream()
                .collect(Collectors.toMap(e -> e, e -> 1, (existingValue, newValue) -> existingValue, HashMap::new)*/
            String flCod = getFlCodFromLabel(expec);
            if(flCod == null) {
                throw new RuntimeException("could not match '" + expec + "' to a code");
            }
            expectations.put(flCod, k++);
        }
        rank = getRank(flCodSugg, expectations);

        if(rank > 0) return "exp      \t";
        return "         \t";
    }


    private static String removeCodes(String str) {
        int i = str.indexOf(" groupe");
        if(i >= 0) str = str.substring(0, i);
        i = str.indexOf(" (fl");
        if(i >= 0) str = str.substring(0, i);
        i = str.indexOf(" (fr");
        if(i >= 0) str = str.substring(0, i);
        return str;
    }

    private static int getRank(String expectation, Map<String, Integer> suggestionsCodes) {
        final String expectation2 = removeCodes(expectation);
        return suggestionsCodes.getOrDefault(expectation2, -1);
    }


    static String toApprentissageExplanationString(String apprentissage) {
        if (apprentissage == null) return "Non-renseigné";
        if (apprentissage.equals("A")) return "Indifférent";
        if (apprentissage.equals("B")) return "Indifférent";
        if (apprentissage.equals("C")) return "Peu intéressé";
        return apprentissage;
    }


    public static @Nullable String getFlCodFromLabel(String expectation) {
        return
                labelsNoDebug.entrySet().stream()
                        .map(e -> Pair.of(e.getKey(), levenAlgo.apply(e.getValue(), expectation)))
                        .filter(e -> e.getRight() >= 0 && e.getRight() < 5)
                        .min(Comparator.comparingInt(Pair::getRight))
                        .map(Pair::getLeft)
                        .orElse(null);
    }
}

package fr.gouv.monprojetsup.data.carte.algos.tags;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Scores {


    public static final int MIN_LENGTH_FOR_FIL_KEYWORD = 3;

    public static List<String> cleanAndSplit(String chain, Set<String> ignoredWords) {
        return cleanAndSplit(chain, ignoredWords, false);
    }

    /* remove accents */
    public static String removeAccents(String input) {
        return input == null ? null
                : Normalizer
                .normalize(input, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}","");
    }
    //the words have length >+2 and are not in IGNORED_WORDS
    public static List<String> cleanAndSplit(String chain, Set<String> ignoredWords, boolean noSplitMode) {
        boolean isNumber = chain.matches("\\d+");
        if (!isNumber && !noSplitMode) {
            chain =
                    chain.replaceAll("\\p{Z}", " ")
                            .replaceAll("\\p{C}", " ")
                            .replaceAll("[,;:/&?=+\"_()><%$']", " ")
                            .replaceAll("[\\u0800-\\uFFFF]", " ")
                            .replaceAll("\\s+", " ")
                            .replaceAll("^\\s|\\s$", "");
        } else {
            chain =
                    chain.replaceAll("\\p{Z}", " ")
                            .replaceAll("\\p{C}", " ")
                            .replaceAll("[\\u0800-\\uFFFF]", " ")
                            .replaceAll("\\s+", " ")
                            .replaceAll("^\\s|\\s$", "");
        }

        Set<String> result1;
        if (!noSplitMode) {
            result1 = Arrays.stream(chain.split("\\s+"))
                    .map(w -> w.toLowerCase().trim())
                    .filter(w -> (w.matches("\\d+") || w.length() >= 2) && !ignoredWords.contains(w))
                    .collect(Collectors.toSet());
            Set<String> result2 = new HashSet<>(result1);
            List<String> specials = Arrays.asList("-", ".");

            result1.forEach(w -> specials.forEach(special -> {
                if (w.contains(special)) {
                    result2.add(w.replace(special, ""));
                    result2.addAll(
                            Arrays.stream(w.split(special))
                                    .map(String::trim).toList()
                    );
                    result2.remove(w);
                }
            }));
            return result2.stream().map(w -> w.toLowerCase().trim())
                    .filter(w -> !w.isEmpty() && (w.matches("\\d+") || w.length() >= 2) && !ignoredWords.contains(w))
                    .collect(Collectors.toList());
        } else {
            return Arrays.stream(chain.split("\\s+|;"))
                    .map(w -> removeAccents(w.trim()))
                    .filter(w -> (w.matches("\\d+") || w.length() >= 2)
                            && !ignoredWords.contains(w)).distinct().collect(Collectors.toList());
        }
    }

    private Scores() {

    }

}

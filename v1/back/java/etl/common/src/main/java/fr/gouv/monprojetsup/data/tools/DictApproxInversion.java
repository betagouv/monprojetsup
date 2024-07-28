package fr.gouv.monprojetsup.data.tools;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DictApproxInversion {

    private static final LevenshteinDistance levenAlgo = new LevenshteinDistance(10);

   /**
     * maps lbales to key with closest correspondance
     * @return
     */
    public static @Nullable String findKey(String label, Map<String,String> keyToLabelbel) {
        if(label == null || label.length() <= 3) return null;
        String bestMatch = null;
        String bestLib = null;
        int bestScore = Integer.MAX_VALUE;
        for (Map.Entry<String, String> entry : keyToLabelbel.entrySet()) {
            String lib = entry.getValue();
            if(lib == null) continue;
            int distance = levenAlgo.apply(label, lib);
            int i = lib.indexOf("/");
            if(i > 0) {
                int distance2 = levenAlgo.apply(label, lib.substring(i));
                if(distance2 >= 0 && distance2 < distance) {
                    distance = distance2;
                }
            }
            if (distance >= 0 && distance < bestScore) {
                bestScore = distance;
                bestMatch = entry.getKey();
                bestLib = lib;
            }
        }
        if(bestMatch == null || bestScore > 1 + label.length() / 5 || bestScore > 1 + bestLib.length() / 5) {
            return null;
        } else {
            return bestMatch;
        }
    }

}

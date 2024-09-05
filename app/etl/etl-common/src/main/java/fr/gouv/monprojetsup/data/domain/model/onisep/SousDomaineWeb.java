package fr.gouv.monprojetsup.data.domain.model.onisep;

import com.google.gson.annotations.SerializedName;
import fr.gouv.monprojetsup.data.domain.Constants;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;



    public record SousDomaineWeb(

            String ideo,

            @SerializedName("domaine_onisep")
            @NotNull String domaineOnisep,
            @SerializedName("sous-domaine_onisep")
            @NotNull String sousDomaineOnisep
    ) {




        private static final LevenshteinDistance levenAlgo = new LevenshteinDistance(10);

        /**
         * Extract keys from strings in the domaines fields of DataSources.METIERS_PATH like for example
         * ""information-communication, audiovisuel/communication| commerce, marketing, vente/marketing, vente"
         *
         * @param domaines    the field to extract keys from
         * @param domainesPro
         * @return the keys
         */
        public static Set<SousDomaineWeb> extractDomaines(@NotNull String domaines, Map<String, SousDomaineWeb> sousDomainesWeb) {
            Set<SousDomaineWeb> answer = new HashSet<>();
            String[] split = domaines.split("\\|");
            for (String dom : split) {
                SousDomaineWeb domaine = extractDomaine(dom.trim(), sousDomainesWeb);
                if (domaine != null) {
                    answer.add(domaine);
                }
            }
            return answer;
        }

        private static SousDomaineWeb extractDomaine(String label, Map<String, SousDomaineWeb> domainesWeb) {

            //when it is the key and not the label
            if(domainesWeb.containsKey(label)){
                return domainesWeb.get(label);
            }

            int i = label.indexOf("/");
            if (i <= 0 || i >= label.length() - 1) return null;

            String domaine = label.substring(0, i);
            String sousdomaine = label.substring(i + 1);

            SousDomaineWeb bestMatch = null;
            String bestLib = null;
            int bestScore = Integer.MAX_VALUE;
            for (SousDomaineWeb dom : domainesWeb.values()) {
                int distance = levenAlgo.apply(domaine, dom.domaineOnisep) + levenAlgo.apply(sousdomaine, dom.sousDomaineOnisep);
                if (distance >= 0 && distance < bestScore) {
                    bestScore = distance;
                    bestMatch = dom;
                    bestLib = dom.domaineOnisep + "/" + dom.sousDomaineOnisep;
                }

            }
            if (bestMatch == null || bestScore > 1 + label.length() / 5 || bestScore > 1 + bestLib.length() / 5) {
                return null;
            } else {
                return bestMatch;
            }

        }

        public @NotNull String mpsId() {
            return Constants.cleanup(ideo);
        }

    }

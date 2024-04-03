package fr.gouv.monprojetsup.data.update.onisep;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



    public record DomainePro(
            String domaine_onisep,
            String sous_domaine_onisep
    ) {


        public static List<DomainePro> load() throws IOException {
            return Serialisation.fromJsonFile(
                    DataSources.getSourceDataFilePath(DataSources.DOMAINES_PRO_PATH),
                    new TypeToken<List<DomainePro>>() {
                    }.getType()
            );
        }

        private static final LevenshteinDistance levenAlgo = new LevenshteinDistance(10);

        /**
         * Extract keys from strings in the domaines fields of DataSources.METIERS_PATH like for example
         * ""information-communication, audiovisuel/communication| commerce, marketing, vente/marketing, vente"
         *
         * @param domaines    the field to extract keys from
         * @param domainesPro
         * @return the keys
         */
        public static Set<DomainePro> extractDomaines(@NotNull String domaines, List<DomainePro> domainesPro) {
            Set<DomainePro> answer = new HashSet<>();
            String[] split = domaines.split("\\|");
            for (String dom : split) {
                DomainePro key = extractDomaine(dom.trim(), domainesPro);
                if (key != null) {
                    answer.add(key);
                }
            }
            return answer;
        }

        private static DomainePro extractDomaine(String label, List<DomainePro> domainesPro) {
            int i = label.indexOf("/");
            if (i <= 0 || i >= label.length() - 1) return null;

            String domaine = label.substring(0, i);
            String sousdomaine = label.substring(i + 1);

            DomainePro bestMatch = null;
            String bestLib = null;
            int bestScore = Integer.MAX_VALUE;
            for (DomainePro dom : domainesPro) {
                int distance = levenAlgo.apply(domaine, dom.domaine_onisep) + levenAlgo.apply(sousdomaine, dom.sous_domaine_onisep);
                if (distance >= 0 && distance < bestScore) {
                    bestScore = distance;
                    bestMatch = dom;
                    bestLib = dom.domaine_onisep + "/" + dom.sous_domaine_onisep;
                }

            }
            if (bestMatch == null || bestScore > 1 + label.length() / 5 || bestScore > 1 + bestLib.length() / 5) {
                return null;
            } else {
                return bestMatch;
            }

        }
    }

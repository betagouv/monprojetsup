package fr.gouv.monprojetsup.data.model.metiers;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record MetiersScrapped(Map<String, MetierScrap> metiers) {

    public MetiersScrapped() {
        this(new HashMap<>());
    }

    public @Nullable MetierScrap get(String key) {
        return metiers.get(key);
    }

    public record MetierScrap(
            String key,
            String url,
            String nom,
            String accroche,
            String metier,
            String etudes,
            String emploi,
            String competences,
            String ou,
            String salaire,
            List<String> warnings,
            String error
    ) {

        public static MetierScrap getError(Exception e, List<String> warnings) {
            return new MetierScrap(e.getMessage(), warnings);
        }

        public static MetierScrap getError(String error, List<String> warnings) {
            return new MetierScrap(error, warnings);
        }

        public static MetierScrap get(String key, String url, String nom, String accroche, String metier, String etudes, String emploi, String competences, String ou, String salaire, List<String> warnings) {
            return new MetierScrap(key, url, nom, accroche, metier, etudes, emploi,competences, ou, salaire, warnings,null);
        }

        private MetierScrap(String error, List<String> warnings) {
            this( null, null, null, null, null, null, null, null, null, null, warnings ,error);
        }

        public boolean isError() {
            return error != null && !error.isEmpty();
        }
    }
}

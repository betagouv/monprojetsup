package fr.gouv.monprojetsup.data.model.metiers;

import fr.gouv.monprojetsup.data.update.onisep.DomainePro;
import fr.gouv.monprojetsup.data.update.onisep.MetierOnisep;
import fr.gouv.monprojetsup.data.tools.DictApproxInversion;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.cleanup;

public record Metiers(
        /**
         * indexed by metiers fl e.g. MET.7776
         */
        Map<String,Metier> metiers
) {
    public Metiers() {
        this(new HashMap<>());
    }

    public Metiers(List<MetierOnisep> metiers, List<DomainePro> domainesPro) {
        this(new HashMap<>());
        for (MetierOnisep m : metiers) {
            String url = m.lien_site_onisepfr();
            int index = url.lastIndexOf("/");
            if (index < 0) {
                throw new RuntimeException("Impossible to extract metiers fl style MET.7776 from the url of " + m);
            }
            String metierID = m.lien_site_onisepfr().substring(index + 1);
            if (!metierID.contains("MET.")) {
                throw new RuntimeException("Impossible to extract metiers fl style MET.7776 from the url of " + m);
            }
            this.metiers.put(cleanup(metierID), new Metier(m, domainesPro));
        }
    }

    private static final LevenshteinDistance levenAlgo = new LevenshteinDistance(10);

    /**
     * maps full string to MET.* code, with spell check
     * @param metierLabel
     * @return
     */
    public @Nullable String findMetierKey(String metierLabel) {
        return DictApproxInversion.findKey(
                metierLabel,
                metiers.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().lib()
                            )
                        )
        );
    }
    public synchronized @Nullable Metier findMetier(String metierLabel) {
        return metiers.get(findMetierKey(metierLabel.trim()));
    }

    /**
     * Extracts metiers from a string, separated by ,;:. or .
     * @param substring
     * @return list of (MET_xxx, original text, metier)
     */
    public List<Triple<String,String,Metier>> extractMetiers(String substring) {
        return Arrays.stream(substring.split("[;:,;.]"))
                .map(String::trim)
                .map(s -> Pair.of(findMetierKey(s),s))
                .filter(p -> p.getLeft() != null)
                .map(p -> Triple.of(p.getLeft(),p.getRight(), metiers.get(p.getLeft())))
                .collect(Collectors.toList());
    }

    public List<String> extractMetiersKeys(String substring) {
        return extractMetiers(substring).stream().map(Triple::getLeft).collect(Collectors.toList());
    }


    public void inject(MetiersScrapped other) {
        other.metiers().values().forEach(m -> {
            String cs = cleanup(m.key());
            if(!metiers.containsKey(cs) && m.nom()!= null && !m.nom().isEmpty()) {
                metiers.put(cs, new Metier(
                        m.nom(),
                        m.url(),
                        null,
                        null,
                        null,
                        new HashSet<>()
                ));
            }
        });
    }


    public record Metier(
            String lib,
            String url,
            String libRome,
            String urlRome,
            String codeRome,

            Set<DomainePro> domaines
    ) {
        public static final String ONISEP_BASE_URL = "https://onisep.fr/http/redirection/metier/slug/";

        public Metier(MetierOnisep m, List<DomainePro> domainesPro) {
            this(m.libelle_metier(),
                    m.lien_site_onisepfr(),
                    m.libelle_rome(),
                    m.lien_rome(),
                    m.code_rome(),
                    m.getDomaines(domainesPro)
            );
        }

        public @Nullable String id(){
            String url = this.url();
            int index = url.lastIndexOf("/");
            if(index < 0) return null;
            return url.substring(index + 1);
        }
    }
}

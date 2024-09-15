package fr.gouv.monprojetsup.data.model.onisep;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.model.descriptifs.DescriptifsFormationsMetiers;
import fr.gouv.monprojetsup.data.model.formations.FilieresPsupVersIdeoData;
import fr.gouv.monprojetsup.data.model.formations.FormationIdeoDuSup;
import fr.gouv.monprojetsup.data.model.taxonomie.Taxonomie;
import fr.gouv.monprojetsup.data.model.metiers.MetierIdeoDuSup;
import fr.gouv.monprojetsup.data.model.onisep.metiers.FicheMetierIdeo;
import fr.gouv.monprojetsup.data.tools.DictApproxInversion;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record OnisepData(
        Taxonomie domaines,

        Taxonomie interets,

        List<Pair<String,String>> edgesFormationsDomaines,

        List<Pair<String,String>> edgesDomainesMetiers,
        List<Pair<String,String>> edgesSecteursMetiers,

        List<Pair<String,String>> edgesMetiersFormations,

        List<Pair<String,String>> edgesInteretsMetiers,

        List<FilieresPsupVersIdeoData> filieresToFormationsOnisep,

        List<MetierIdeoDuSup> metiersIdeo,

        List<FormationIdeoDuSup> formationsIdeo
        ) {


    /**
     * maps full string to MET.* code, with spell check
     * @param metierLabel e.g. "Mécanicien automobile"
     * @return MET.* code
     */
    private static @Nullable String findMetierKey(Map<String, MetierIdeoDuSup> metiers, String metierLabel) {
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

    /**
     * Extracts metiers from a string, separated by ,;:. or .
     * @param substring e.g. "Mécanicien automobile, électricien, ..."
     * @return list of (MET_xxx, original text, metier)
     */
    private static @NotNull List<Triple<String,String, MetierIdeoDuSup>> extractMetiers(Map<String, MetierIdeoDuSup> metiers, String substring) {
        return Arrays.stream(substring.split("[;:,.]"))
                .map(String::trim)
                .map(s -> Pair.of(findMetierKey(metiers, s),s))
                .filter(p -> p.getLeft() != null)
                .map(p -> Triple.of(p.getLeft(),p.getRight(), metiers.get(p.getLeft())))
                .toList();
    }

    public static List<String> extractMetiersKeys(Map<String, MetierIdeoDuSup> metiers, String substring) {
        return extractMetiers(metiers, substring).stream().map(Triple::getLeft).toList();
    }

    public @NotNull List<Pair<String,String>> getEdgesSecteursMetiers() {
        return metiersIdeo.stream().flatMap(metier -> metier.secteursActivite().stream().map(key -> Pair.of(metier.ideo(), key))).toList();
    }
    public @NotNull List<Pair<String,String>> getEdgesDomainesMetiers() {
        return metiersIdeo.stream().flatMap(metier -> metier.domainesWeb().stream().map(key -> Pair.of(metier.ideo(), key))).toList();
    }

    public @NotNull List<Pair<String,String>> getEdgesAtomeToElement() {
        return Stream.concat(
                interets.getAtomesversElements(),
                domaines.getAtomesversElements()
        ).toList();
    }


    public @NotNull List<Pair<String,String>> getEdgesMetiersAssocies() {

        return metiersIdeo.stream()
                .flatMap(metier -> metier.metiersAssocies().stream()
                        .map(m -> Pair.of(metier.idMps(), Constants.cleanup(m.id()))))
                .toList();
    }

    public @NotNull Map<String, @NotNull List<@NotNull String>> getMetiersAssociesLabels() {
        Map<String, @NotNull List<@NotNull String>> result = new HashMap<>();
        //ajout des secteurs d'activité
        metiersIdeo.forEach(fiche -> {
            String keyMetier = fiche.idMps();
            result.computeIfAbsent(keyMetier, z -> new ArrayList<>())
                    .addAll(
                            fiche.metiersAssocies().stream()
                                    .map(FicheMetierIdeo.MetierAssocie::libelle).toList()
                    );
        });
        return result;
    }




    /* filieres to metiers */
    public static Map<String, Set<String>> getFormationsVersMetiersFromDescriptifs(
            DescriptifsFormationsMetiers descriptifs,
            List<MetierIdeoDuSup> metiers
    ) {
        Map<String, Set<String>> result = new HashMap<>();
        Map<String,MetierIdeoDuSup> metiersMap = metiers.stream()
                .collect(Collectors.toMap(z -> Constants.cleanup(Objects.requireNonNull(z.ideo())), z -> z));
        descriptifs.keyToDescriptifs().forEach((key, descriptif) -> {
            if (!descriptif.hasError() && descriptif.presentation() != null) {
                int i = descriptif.presentation().indexOf("Exemples de métiers");
                if (i > 0) {
                    List<String> mets = extractMetiersKeys(metiersMap, descriptif.presentation().substring(i));
                    if(!mets.isEmpty()) {
                        result.computeIfAbsent(key, z -> new HashSet<>()).addAll(mets);
                    }
                }
            }
            if (!descriptif.hasError() && descriptif.metiers() != null) {
                List<String> mets = extractMetiersKeys(metiersMap, descriptif.metiers());
                if(!mets.isEmpty()) {
                    result.computeIfAbsent(key, z -> new HashSet<>()).addAll(mets);
                }
            }
        });
        return result;
    }


    public Map<String, String> getDomainesLabels(boolean includeKeys) {
        return domaines.getLabels(includeKeys);
    }
}

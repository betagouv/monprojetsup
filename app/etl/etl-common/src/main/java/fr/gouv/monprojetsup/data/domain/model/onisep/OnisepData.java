package fr.gouv.monprojetsup.data.domain.model.onisep;

import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.Helpers;
import fr.gouv.monprojetsup.data.domain.model.descriptifs.DescriptifsFormationsMetiers;
import fr.gouv.monprojetsup.data.domain.model.formations.FilieresPsupVersFormationsEtMetiersIdeo;
import fr.gouv.monprojetsup.data.domain.model.formations.FormationIdeoDuSup;
import fr.gouv.monprojetsup.data.domain.model.interets.Interets;
import fr.gouv.monprojetsup.data.domain.model.metiers.MetierIdeoDuSup;
import fr.gouv.monprojetsup.data.domain.model.onisep.metiers.FicheMetierIdeo;
import fr.gouv.monprojetsup.data.tools.DictApproxInversion;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.domain.Constants.PASS_FL_COD;

public record OnisepData(
        DomainesMps domainesMps,

        Interets interets,

        List<Pair<String,String>> edgesFormationsDomaines,

        List<Pair<String,String>> edgesDomainesMetiers,

        List<Pair<String,String>> edgesMetiersFormations,

        List<Pair<String,String>> edgesInteretsMetiers,

        List<FilieresPsupVersFormationsEtMetiersIdeo> filieresToFormationsOnisep,

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


    public Map<String, Set<String>> getSecteursVersMetiers() {
        Map<String, Set<String>> result = new HashMap<>();
        metiersIdeo.forEach(metier -> {
            String keyMetier = metier.ideo();
            metier.domaines().forEach(domaine -> result.computeIfAbsent(domaine, z -> new HashSet<>()).add(keyMetier));
        });
        return result;
    }
    public @NotNull List<Pair<String,String>> getEdgesSecteursMetiers() {
        return getSecteursVersMetiers().entrySet().stream()
                .flatMap(e -> e.getValue().stream().map(m -> Pair.of(
                        e.getKey(),
                        m)))
                .toList();
    }

    public @NotNull List<Pair<String,String>> getEdgesInteretsToInterets() {
        return interets.getItemVersGroupe().entrySet()
                .stream()
                .map(e -> Pair.of(e.getKey(), e.getValue()))
                .toList();
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



    //in mps id style flxxx and MET_xxx
    public static Map<String, Set<String>> getMetiersVersFormationsMps(
            List<FilieresPsupVersFormationsEtMetiersIdeo> filieresToFormationsOnisep,
            List<FormationIdeoDuSup> formationsIdeoSuSup,
            List<MetierIdeoDuSup> metiersIdeoduSup
    ) {

        Map<String, FormationIdeoDuSup> formationsIdeo = formationsIdeoSuSup.stream()
                .collect(Collectors.toMap(FormationIdeoDuSup::ideo, z -> z));

        Map<String, MetierIdeoDuSup> metiersIdeo = metiersIdeoduSup.stream()
                .collect(Collectors.toMap(MetierIdeoDuSup::ideo, z -> z));

        Map<String, Set<String>> formationsVersMetiers = new HashMap<>();
        filieresToFormationsOnisep.forEach(
                fil -> formationsVersMetiers.put(
                        fil.gFlCod(),
                        fil.ideoFormationsIds().stream()
                                .map(formationsIdeo::get)
                                .filter(Objects::nonNull)
                                .map(FormationIdeoDuSup::metiers)
                                .flatMap(Collection::stream)
                                .map(Constants::cleanup)
                                .collect(Collectors.toSet())));
        formationsVersMetiers.values().removeIf(Set::isEmpty);
        formationsVersMetiers.values().removeIf(Set::isEmpty);

        Map<String, Set<String>> metiersVersFormations = new HashMap<>();
        formationsVersMetiers.forEach(
                (f, ms) -> ms.forEach(
                        m -> metiersVersFormations.computeIfAbsent(m, z -> new HashSet<>()).add(f)
                )
        );
        return metiersVersFormations;
    }



    /**
     * metiers vers filieres
     * @return a map metiers -> filieres
     */
    public Map<String, Set<String>> getMetiersVersFormationsExtendedWithGroupsAndLASAndDescriptifs(
            Map<String, String> psupKeyToMpsKey,
            Map<String, String> genericToLas,
            DescriptifsFormationsMetiers descriptifs
            ) {


        Map<String, Set<String>> metiersVersFormations = new HashMap<>();

        this.edgesMetiersFormations().forEach(p ->
                metiersVersFormations.computeIfAbsent(p.getLeft(), z -> new HashSet<>())
                        .add(p.getRight()));

        getMetiersVersFormationsFromDescriptifs(
                descriptifs,
                metiersIdeo
        ).forEach(
                (f, ms) -> ms.forEach(
                        m -> metiersVersFormations.computeIfAbsent(m, z -> new HashSet<>()).add(f)
                )
        );

        metiersVersFormations.keySet().removeIf(k -> !k.startsWith(Constants.MET_PREFIX));
        metiersVersFormations.values().forEach(strings -> strings.removeIf(s -> !Helpers.isFiliere(s)));


        /* ajouts des las aux metiers PASS */
        String passKey = Constants.gFlCodToFrontId(PASS_FL_COD);
        Set<String> metiersPass =
                metiersVersFormations.entrySet().stream()
                        .filter(e -> e.getValue().contains( passKey))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());
        metiersPass.forEach(m ->
                metiersVersFormations.computeIfAbsent( m,
                z -> new HashSet<>())
                        .addAll(genericToLas.values())
        );


        metiersVersFormations.entrySet().forEach(e -> {
            Set<String> mpsFormationsKeysBase = new HashSet<>(e.getValue());
            Set<String> mpsFormationsKeys = new HashSet<>(mpsFormationsKeysBase);
            mpsFormationsKeysBase.forEach(mpsKey -> {
                /* ajouts des groupes génériques aux metiers des formations correspondantes */
                mpsFormationsKeys.add(psupKeyToMpsKey.getOrDefault(mpsKey,mpsKey));
                /* ajouts des las aux metiers des génériques correspondants */
                if(genericToLas.containsKey(mpsKey)) {
                    mpsFormationsKeys.add(genericToLas.get(mpsKey));
                }
            });
            e.setValue(mpsFormationsKeys);
        });
        return metiersVersFormations;
    }

    /* filieres to metiers */
    public static Map<String, Set<String>> getMetiersVersFormationsFromDescriptifs(
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
        return domainesMps.getLabels(includeKeys);
    }
}

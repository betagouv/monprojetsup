package fr.gouv.monprojetsup.data.domain.model.onisep;

import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.Helpers;
import fr.gouv.monprojetsup.data.domain.model.descriptifs.DescriptifsFormationsMetiers;
import fr.gouv.monprojetsup.data.domain.model.formations.FiliereToFormationsOnisep;
import fr.gouv.monprojetsup.data.domain.model.graph.Edges;
import fr.gouv.monprojetsup.data.domain.model.interets.Interets;
import fr.gouv.monprojetsup.data.domain.model.metiers.MetierIdeoDuSup;
import fr.gouv.monprojetsup.data.domain.model.formations.FormationIdeoDuSup;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.PsupToIdeoCorrespondance;
import fr.gouv.monprojetsup.data.domain.model.onisep.metiers.FicheMetierIdeo;
import fr.gouv.monprojetsup.data.domain.model.rome.InteretsRome;
import fr.gouv.monprojetsup.data.domain.model.thematiques.Thematiques;
import fr.gouv.monprojetsup.data.tools.DictApproxInversion;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.domain.Constants.PASS_FL_COD;
import static fr.gouv.monprojetsup.data.domain.Constants.cleanup;

public record OnisepData(
        Thematiques thematiques,

        Interets interets,

        Edges edgesFilieresThematiques,

        Edges edgesThematiquesMetiers,

        Edges edgesMetiersFilieres,

        Edges edgesInteretsMetiers,

        List<FiliereToFormationsOnisep> filieresToFormationsOnisep,

        PsupToIdeoCorrespondance billy,

        List<MetierIdeoDuSup> metiersIdeo,

        List<FormationIdeoDuSup> formationsIdeo
        ) {

    public static final double EDGES_INTERETS_METIERS_WEIGHT = 0.001;

    public static final double EDGES_METIERS_SECTEURS_WEIGHT = 0.01;

    public static final double EDGES_METIERS_METIERS_WEIGHT = 0.8;

    public static final double EDGES_INTERETS_INTERETS_WEIGHT = 1.0;

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
    public @NotNull Edges getEdgesSecteursMetiers() {
        Map<String, Collection<String>> m = new HashMap<>(getSecteursVersMetiers());
        return new Edges(m, EDGES_METIERS_SECTEURS_WEIGHT, true);
    }

    public @NotNull Edges getEdgesInteretsToInterets() {
        Map<String,Collection<String>> m = interets.itemVersGroupe().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> List.of(e.getValue()))
                );
        return new Edges(m, EDGES_INTERETS_INTERETS_WEIGHT, true);
    }


    public @NotNull Edges getEdgesMetiersAssocies() {
        Edges result = new Edges();

        metiersIdeo.forEach(metier -> {
            String keyMetier = metier.idMps();
            metier.metiersAssocies().stream()
                    .map(z -> Constants.cleanup(z.id()))
                    .sequential()
                    .forEach(
                            keyMetierAssocie ->  result.put(keyMetier, keyMetierAssocie, true, EDGES_METIERS_METIERS_WEIGHT)
                    );
        });
        return result;
    }

    public @NotNull Map<String, @NotNull List<@NotNull String>> getMetiersAssociesLabels() {
        Map<String, @NotNull List<@NotNull String>> result = new HashMap<>();
        //ajout des secteurs d'activité
        metiersIdeo.forEach(fiche -> {
            String keyMetier = fiche.idMps();
            result.computeIfAbsent(keyMetier, z -> new ArrayList<>())
                    .addAll(
                            fiche.metiersAssocies().stream()
                                    .map(FicheMetierIdeo.MetiersAssocies.MetierAssocie::libelle).toList()
                    );
        });
        return result;
    }





    public void insertRomeData(InteretsRome romeInterets) {
        romeInterets.arbo_centre_interet().forEach(item -> {
            Set<String> codes = item.liste_metier().stream()
                    .map(InteretsRome.Metier::code_rome)
                    .collect(Collectors.toSet());
            List<MetierIdeoDuSup> l = metiersIdeo.stream()
                    .filter(m -> m.codeRome() != null && codes.contains(m.codeRome()))
                    .toList();
            if (!l.isEmpty()) {
                String key = Interets.getKey(item);
                //ajout de l'intérêt
                interets().put(key, item.libelle_centre_interet());
                //ajout des arètes
                l.forEach(metier -> {
                            val id = metier.ideo();
                            if (id != null) {
                                edgesInteretsMetiers.put(
                                        key,
                                        cleanup(id),
                                        false,
                                        EDGES_INTERETS_METIERS_WEIGHT
                                );
                            }
                        }
                );
            }
        });
    }



    //in mps id style flxxx and MET_xxx
    public static Map<String, Set<String>> getMetiersVersFormationsMps(
            List<FiliereToFormationsOnisep> filieresToFormationsOnisep,
            List<FormationIdeoDuSup> formationsIdeoSuSup,
            PsupToIdeoCorrespondance billy,
            List<MetierIdeoDuSup> metiersIdeoduSup
    ) {

        Map<String, FormationIdeoDuSup> formationsIdeo = formationsIdeoSuSup.stream()
                .collect(Collectors.toMap(FormationIdeoDuSup::ideo, z -> z));

        Map<String, MetierIdeoDuSup> metiersIdeo = metiersIdeoduSup.stream()
                .collect(Collectors.toMap(MetierIdeoDuSup::ideo, z -> z));

        Map<String, Set<String>> formationsVersMetiers = new HashMap<>();
        //two sources of info:
        //the infamous xml with holes
        filieresToFormationsOnisep.forEach(
                fil -> formationsVersMetiers.put(
                        fil.gFlCod(),
                        fil.ideoIds().stream()
                                .map(formationsIdeo::get)
                                .filter(Objects::nonNull)
                                .map(FormationIdeoDuSup::metiers)
                                .flatMap(Collection::stream)
                                .map(Constants::cleanup)
                                .collect(Collectors.toSet())));

        formationsVersMetiers.values().removeIf(Set::isEmpty);
        //the JM and Claire file
        billy.psupToIdeo2().forEach(
                line -> {
                    List<String> keys =
                            Arrays.stream(line.METIER_IDEO2().split(";"))
                                    .map(String::trim)
                                    .filter(metiersIdeo::containsKey)
                                    .map(Constants::cleanup)
                                    .toList();
                    String key = Constants.gFlCodToFrontId(line.G_FL_COD());
                    formationsVersMetiers
                            .computeIfAbsent(key, z -> new HashSet<>())
                            .addAll(keys);
                }
        );
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

        this.edgesMetiersFilieres().edges().forEach((s, strings) ->
                metiersVersFormations.computeIfAbsent(s, z -> new HashSet<>())
                        .addAll(strings));

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



}

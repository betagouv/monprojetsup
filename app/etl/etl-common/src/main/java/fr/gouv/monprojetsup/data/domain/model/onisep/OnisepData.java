package fr.gouv.monprojetsup.data.domain.model.onisep;

import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.Helpers;
import fr.gouv.monprojetsup.data.domain.model.descriptifs.DescriptifsFormationsMetiers;
import fr.gouv.monprojetsup.data.domain.model.formations.FilieresToFormationsOnisep;
import fr.gouv.monprojetsup.data.domain.model.graph.Edges;
import fr.gouv.monprojetsup.data.domain.model.interets.Interets;
import fr.gouv.monprojetsup.data.domain.model.metiers.Metiers;
import fr.gouv.monprojetsup.data.domain.model.onisep.billy.PsupToOnisepLines;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.FormationsOnisep;
import fr.gouv.monprojetsup.data.domain.model.rome.InteretsRome;
import fr.gouv.monprojetsup.data.domain.model.thematiques.Thematiques;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.domain.Constants.PASS_FL_COD;
import static fr.gouv.monprojetsup.data.domain.Constants.cleanup;

public record OnisepData(
        Metiers metiers,

        Thematiques thematiques,

        Interets interets,

        Edges edgesFilieresThematiques,

        Edges edgesThematiquesMetiers,

        Edges edgesMetiersFilieres,

        Edges edgesInteretsMetiers,

        FilieresToFormationsOnisep filieresToFormationsOnisep,

        PsupToOnisepLines billy,

        FichesMetierOnisep fichesMetiers,

        FormationsOnisep formationsOnisep
        ) {

    public static final double EDGES_INTERETS_METIERS_WEIGHT = 0.001;

    public static final double EDGES_METIERS_SECTEURS_WEIGHT = 0.01;

    public static final double EDGES_METIERS_METIERS_WEIGHT = 0.8;

    public static final double EDGES_INTERETS_INTERETS_WEIGHT = 1.0;


    public Map<String, Set<String>> getSecteursVersMetiers() {
        val formationsDuSup = formationsOnisep.getFormationsDuSup();
        Map<String, Set<String>> result = new HashMap<>();
        fichesMetiers.metiers().metier().forEach(fiche -> {
            String keyMetier = cleanup(fiche.identifiant());
            if (fiche.secteurs_activite() != null && fiche.secteurs_activite().secteur_activite() != null) {
                fiche.secteurs_activite().secteur_activite().forEach(secteur -> {
                    String keySecteur = cleanup(secteur.id());
                    if(fiche.isMetierSup(formationsDuSup)) {
                        result.computeIfAbsent(keySecteur, z -> new HashSet<>()).add(keyMetier);
                    }
                });
            }
        });
        return result;
    }
    public @NotNull Edges getEdgesSecteursMetiers() {
        Map<String, Collection<String>> m = new HashMap<>(getSecteursVersMetiers());
        return new Edges(m, EDGES_METIERS_SECTEURS_WEIGHT, true);
    }

    public @NotNull Edges getEdgesInteretsToInterets() {
        Map<String, Collection<String>> m = new HashMap<>(interets.expansion());
        return new Edges(m, EDGES_INTERETS_INTERETS_WEIGHT, true);
    }


    public @NotNull Edges getEdgesMetiersAssocies() {
        Edges result = new Edges();
        //ajout des secteurs d'activité
        fichesMetiers().metiers().metier().forEach(fiche -> {
            String keyMetier = cleanup(fiche.identifiant());
            if (fiche.secteurs_activite() != null
                    && fiche.secteurs_activite().secteur_activite() != null
                    && fiche.metiers_associes() != null
                    && fiche.metiers_associes().metier_associe() != null) {
                fiche.metiers_associes().metier_associe().forEach(metierAssocie -> {
                    String keyMetierAssocie = cleanup(metierAssocie.id());
                    result.put(keyMetier, keyMetierAssocie, true, EDGES_METIERS_METIERS_WEIGHT);
                });
            }

        });
        return result;
    }

    public @NotNull Map<String, @NotNull List<@NotNull String>> getMetiersAssocies() {
        Map<String, @NotNull List<@NotNull String>> result = new HashMap<>();
        //ajout des secteurs d'activité
        fichesMetiers().metiers().metier().forEach(fiche -> {
            String keyMetier = cleanup(fiche.identifiant());
            if (fiche.secteurs_activite() != null
                    && fiche.secteurs_activite().secteur_activite() != null
                    && fiche.metiers_associes() != null
                    && fiche.metiers_associes().metier_associe() != null) {
                fiche.metiers_associes().metier_associe().forEach(metierAssocie -> {
                    String keyMetierAssocie = cleanup(metierAssocie.id());
                    result.computeIfAbsent(keyMetier, z -> new ArrayList<>()).add(keyMetierAssocie);
                });
            }
        });
        return result;
    }





    public void insertRomeData(InteretsRome romeInterets) {
        romeInterets.arbo_centre_interet().forEach(item -> {
            Set<String> codes = item.liste_metier().stream()
                    .map(InteretsRome.Metier::code_rome)
                    .collect(Collectors.toSet());
            List<Metiers.Metier> l = metiers.metiers().values().stream()
                    .filter(m -> codes.contains(m.codeRome()))
                    .toList();
            if (!l.isEmpty()) {
                String key = Interets.getKey(item);
                //ajout de l'intérêt
                interets().put(key, item.libelle_centre_interet());
                //ajout des arètes
                l.forEach(metier -> {
                            val id = metier.id();
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



    public static Map<String, Set<String>> getMetiersVersFormations(
            FilieresToFormationsOnisep filieresToFormationsOnisep,
            PsupToOnisepLines billy,
            Metiers metiers
    ) {
        Map<String, Set<String>> result = new HashMap<>();
        //two sources of info:
        //the infamous xml with holes
        filieresToFormationsOnisep.filieres().forEach(
                fil -> result.put(
                        fil.gFlCod(),
                        fil.formationOniseps().stream()
                                .map(FilieresToFormationsOnisep.FormationOnisep::metiers)
                                .flatMap(Collection::stream)
                                .collect(Collectors.toSet())));

        result.values().removeIf(Set::isEmpty);
        //the JM and Claire file
        billy.psupToIdeo2().forEach(
                line -> {
                    Set<String> keys =
                            Arrays.stream(line.METIER_IDEO2().split(";"))
                                    .map(String::trim)
                                    .map(metiers::findMetier)
                                    .filter(Objects::nonNull)
                                    .map(Metiers.Metier::id)
                                    .collect(Collectors.toSet());
                    String key = Constants.FILIERE_PREFIX + line.G_FL_COD();
                    result
                            .computeIfAbsent(key, z -> new HashSet<>())
                            .addAll(keys);
                }
        );
        result.values().removeIf(Set::isEmpty);

        Map<String, Set<String>> metiersVersFormations = new HashMap<>();
        result.forEach(
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
                this.metiers
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
            Metiers metiers
    ) {
        Map<String, Set<String>> result = new HashMap<>();
        descriptifs.keyToDescriptifs().forEach((key, descriptif) -> {
            if (!descriptif.hasError() && descriptif.presentation() != null) {
                int i = descriptif.presentation().indexOf("Exemples de métiers");
                if (i > 0) {
                    List<String> mets = metiers.extractMetiersKeys(descriptif.presentation().substring(i));
                    if(!mets.isEmpty()) {
                        result.computeIfAbsent(key, z -> new HashSet<>()).addAll(mets);
                    }
                }
            }
            if (!descriptif.hasError() && descriptif.metiers() != null) {
                List<String> mets = metiers.extractMetiersKeys(descriptif.metiers());
                if(!mets.isEmpty()) {
                    result.computeIfAbsent(key, z -> new HashSet<>()).addAll(mets);
                }
            }
        });
        return result;
    }



}

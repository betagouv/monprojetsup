package fr.gouv.monprojetsup.suggestions.infrastructure.onisep;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.suggestions.domain.Constants;
import fr.gouv.monprojetsup.suggestions.domain.Helpers;
import fr.gouv.monprojetsup.suggestions.infrastructure.DataSources;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.Edges;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.descriptifs.DescriptifsFormations;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.disciplines.LiensMetiersThemesOnisep;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.formations.FilieresToFormationsOnisep;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.interets.Interets;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.metiers.Metiers;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.thematiques.Thematiques;
import fr.gouv.monprojetsup.suggestions.infrastructure.onisep.billy.PsupToOnisepLines;
import fr.gouv.monprojetsup.suggestions.infrastructure.onisep.formations.FormationsAvecMetiers;
import fr.gouv.monprojetsup.suggestions.infrastructure.onisep.formations.FormationsOnisep;
import fr.gouv.monprojetsup.suggestions.infrastructure.rome.InteretsRome;
import fr.gouv.monprojetsup.suggestions.tools.DictApproxInversion;
import fr.gouv.monprojetsup.suggestions.tools.Serialisation;
import fr.gouv.monprojetsup.suggestions.tools.csv.CsvTools;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.suggestions.domain.Constants.*;

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

        SecteursPro domainesWeb,

        FichesMetierOnisep fichesMetiers,

        FormationsOnisep formationsOnisep
        ) {

    private static final Logger LOGGER = Logger.getLogger(OnisepData.class.getSimpleName());

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
            if (fiche.secteurs_activite() != null && fiche.secteurs_activite().secteur_activite() != null) {
                if (fiche.metiers_associes() != null && fiche.metiers_associes().metier_associe() != null) {
                    fiche.metiers_associes().metier_associe().forEach(metierAssocie -> {
                        String keyMetierAssocie = cleanup(metierAssocie.id());
                        result.put(keyMetier, keyMetierAssocie, true, EDGES_METIERS_METIERS_WEIGHT);
                    });
                }
            }
        });
        return result;
    }

    public static @NotNull OnisepData fromFiles(DataSources sources) throws IOException {

        LOGGER.info("Chargement des secteurs et domaines pro");
        SecteursPro secteursPro = SecteursPro.loadSecteursPro(sources);
        List<DomainePro> domainesPro =  Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.DOMAINES_PRO_PATH),
                new TypeToken<List<DomainePro>>() {
                }.getType()
        );

        LOGGER.info("Chargement des fiches metiers");
        FichesMetierOnisep fichesMetiers = FichesMetierOnisep.loadFichesMetierOnisep(sources);

        List<MetierOnisep> metiersOnisep = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.METIERS_PATH),
                new TypeToken<List<MetierOnisep>>() {
                }.getType()
        );
        Metiers metiers = new Metiers(metiersOnisep, domainesPro);

        MetiersScrapped metiersScrapped = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.ONISEP_DESCRIPTIFS_METIERS_PATH),
                MetiersScrapped.class
        );

        metiers.inject(metiersScrapped);

        Thematiques thematiques = Thematiques.loadThematiques(sources);

        InteretsOnisep interetsOnisep = Serialisation.fromJsonFile(sources.getSourceDataFilePath(DataSources.INTERETS_PATH), InteretsOnisep.class);
        val groupes = CsvTools.readCSV(sources.getSourceDataFilePath(DataSources.INTERETS_GROUPES_PATH), '\t');
        Interets interets = new Interets(interetsOnisep, groupes);

        Edges edgesThematiquesFilieres = new Edges();
        Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.THEMATIQUES_FORMATIONS_PAIRES_PATH),
                LienThematiqueFormation.class
        ).paires().forEach(
                p -> edgesThematiquesFilieres.put(
                        Constants.gFlCodToFrontId(p.gFlCod()),
                        thematiques.representatives(p.item())
                )
        );

        LienThematiqueFormation2 indexThematiqueFormation2
                = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.THEMATIQUES_FORMATIONS_PAIRES_v2_PATH),
                LienThematiqueFormation2.class
        );
        indexThematiqueFormation2.paires().forEach(
                p -> edgesThematiquesFilieres.put(
                        p.flCod(),
                        thematiques.representatives(p.Themas())
                )
        );


        Edges edgesThematiquesMetiers = new Edges();
        LiensMetiersThemesOnisep liens = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.METIERS_THEMATIQUES_PAIRES_PATH),
                LiensMetiersThemesOnisep.class
        );

        liens.contents().content().forEach(content -> {
                    String keyMetier = cleanup(content.keyMetier());
                    if (!metiers.metiers().containsKey(keyMetier)) {
                        String title = content.title();
                        if (title != null && !title.isEmpty()) {
                            metiers.metiers().put(keyMetier,
                                    new Metiers.Metier(
                                            title,
                                            Metiers.Metier.ONISEP_BASE_URL + content.keyMetier(),
                                            null,
                                            null,
                                            null,
                                            new HashSet<>()
                                    )
                            );
                        }
                    }
                    content.thesaurusIdeoDisciplines().forEach(theme ->
                            edgesThematiquesMetiers.put(
                                    keyMetier,
                                    thematiques.representatives(theme.keyTheme())
                            )
                    );
                }
        );


        Set<String> themesUsed = new HashSet<>();
        themesUsed.addAll(edgesThematiquesFilieres.targets());
        themesUsed.addAll(edgesThematiquesMetiers.targets());
        themesUsed.removeIf(t -> !t.startsWith("T_ITM"));
        thematiques.retainAll(themesUsed);

        Edges edgesInteretsMetiers = new Edges();
        Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.METIERS_INTERETS_PAIRES_PATH),
                LienInteretMetier.class
        ).paires().forEach(p ->
                edgesInteretsMetiers.put(
                        p.id_centre_interet(),
                        p.id_formation(),
                        false,
                        EDGES_INTERETS_METIERS_WEIGHT
                )//id_formation should be metier...
        );

        LOGGER.info("Chargement de " + DataSources.ONISEP_FORMATIONS_PATH);
        FormationsOnisep formationsOnisep = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.ONISEP_FORMATIONS_PATH),
                FormationsOnisep.class
        );

        removeMetiersBelowBac(
                metiersScrapped,
                fichesMetiers,
                metiers,
                edgesInteretsMetiers,
                formationsOnisep.getFormationsDuSup()
        );


        LOGGER.info("Chargement de " + DataSources.ONISEP_IDEO_FICHES_FORMATIONS_WITH_METIER_PATH);
        FormationsAvecMetiers formationsAvecMetiers = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.ONISEP_IDEO_FICHES_FORMATIONS_WITH_METIER_PATH),
                FormationsAvecMetiers.class
        );

        LOGGER.info("Chargement de " + DataSources.ONISEP_PSUP_TO_IDEO_PATH);
        PsupToOnisepLines lines = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(
                        DataSources.ONISEP_PSUP_TO_IDEO_PATH
                ),
                PsupToOnisepLines.class
        );

        LOGGER.info("Calcul des filieres");
        FilieresToFormationsOnisep filieresToFormationsOnisep = FilieresToFormationsOnisep.getFilieres(
                formationsOnisep,
                formationsAvecMetiers,
                lines
        );


        Map<String, String> dico =
                Serialisation.fromJsonFile(
                        sources.getSourceDataFilePath(DataSources.LABELS_DICO),
                        new TypeToken<Map<String, String>>() {
                        }.getType()
                );
        dico.keySet().removeIf(k -> k.startsWith(FORMATION_PREFIX));
        dico.entrySet().forEach(entry -> {
            String val = entry.getValue();
            int i = entry.getValue().indexOf(" groupe [");
            if (i > 0) {
                val = val.substring(0, i);
            }
            entry.setValue(val.toLowerCase());
        });


        Edges edgesMetiersFilieresLouisV1 = getEdgesMetiersFilieresV1(fichesMetiers, dico, sources);
        Edges edgesMetiersFilieresLouisV2 = getEdgesMetiersFilieresV2(sources);

        //baseline
        val edgesMetiersFilieresOrig = new Edges();
        //adding links from the big xml and jm and cécile correspondances
        getMetiersVersFormations(
                filieresToFormationsOnisep,
                lines,
                metiers
        ).forEach(
                (metier, formationss) -> formationss.forEach(
                        key -> edgesMetiersFilieresOrig.put(metier, key)
                )
        );

        val edgesMetiersFilieres = new Edges(edgesMetiersFilieresOrig);

        //ajouts louis v1 (for debug)
        val ajoutsV1 = edgesMetiersFilieresLouisV1.minus(edgesMetiersFilieres);
        toCsv(ajoutsV1, "ajouts_metiers_filiers_v1.csv");
        val ajoutsV2 = edgesMetiersFilieresLouisV2.minus(edgesMetiersFilieres);
        toCsv(ajoutsV2, "ajouts_metiers_filieres_sans_heritage.csv");

        edgesMetiersFilieres.putAll(edgesMetiersFilieresLouisV2);
        //ajouts louis v2 (for real)

        List<Map<String, String>> metiersFormationsHeritage = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.METIERS_FORMATIONS_HERITAGE),
                new TypeToken<List<Map<String, String>>>() {
                }.getType()
        );

        for (Map<String, String> stringStringMap : metiersFormationsHeritage) {
            String fl = stringStringMap.get("fl");
            //String lib1 = stringStringMap.get("nom_filiere");
            String lib2 = stringStringMap.get("nom_formation");
            String fl2 = DictApproxInversion.findKey(lib2.toLowerCase(), dico);
            if (fl2 != null) {
                edgesMetiersFilieres
                        .getPredecessors(fl2)
                        .keySet()
                        .stream().filter(Helpers::isMetier)
                        .forEach(s -> edgesMetiersFilieres.put(s, fl)
                        );
            }
        }

        val ajoutsV2herit = edgesMetiersFilieres.minus(edgesMetiersFilieresOrig);
        toCsv(ajoutsV2herit, "ajouts_metiers_filieres_avec_heritage.csv");

        return new OnisepData(metiers,
                thematiques,
                interets,
                edgesThematiquesFilieres,
                edgesThematiquesMetiers,
                edgesMetiersFilieres,
                edgesInteretsMetiers,
                filieresToFormationsOnisep,
                lines,
                secteursPro,
                fichesMetiers,
                formationsOnisep);

    }


    private static void toCsv(Map<String, Set<String>> corr, String filename) throws IOException {
        try(CsvTools csvTools = new CsvTools(filename, ',')) {
            csvTools.appendHeaders(List.of("key1","key2"));
            for (Map.Entry<String, Set<String>> entry : corr.entrySet()) {
                String key1 = entry.getKey();
                Set<String> keys2 = entry.getValue();
                for (String key2 : keys2) {
                    csvTools.append(key1);
                    csvTools.append(key2);
                    csvTools.newLine();
                }
            }
        }
    }


    private static Edges getEdgesMetiersFilieresV2(DataSources sources) {
        val edgesMetiersFilieres = new Edges();

        for (Map<String, String> stringStringMap : CsvTools.readCSV(sources.getSourceDataFilePath(DataSources.METIERS_FORMATIONS_ONISEP_PAIRES_PATH), '\t')) {
            String idMetier = stringStringMap.get("id_metier").trim().replace("MET.", "MET_");
            String flCod = stringStringMap.get("fl_cod").trim();
            String aSupprimer = stringStringMap.get("Liens à supprimer");
            if(aSupprimer.isEmpty()) {
                edgesMetiersFilieres.put(idMetier, flCod);
            }
        }
        return edgesMetiersFilieres;
    }

    private static Edges getEdgesMetiersFilieresV1(FichesMetierOnisep fichesMetiers, Map<String, String> dico, DataSources sources) throws IOException {
        val edgesMetiersFilieres = new Edges();
        Serialisation.fromJsonFile(
                        sources.getSourceDataFilePath(DataSources.METIERS_FORMATIONS_PAIRES_PATH_MANUEL),
                        LienFormationMetier2.class
                )
                .paires().forEach(p ->
                        p.metiersList(fichesMetiers).forEach(m ->
                                edgesMetiersFilieres.put(
                                        m,
                                        p.filiere()
                                )
                        )
                );
        List<Map<String,String>> metiersFormationsAjouts = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.METIERS_FORMATIONS_AJOUT),
                new TypeToken<List<Map<String,String>>>(){}.getType()
        );

        metiersFormationsAjouts.forEach(m -> {
            String filiere = m.get("fl");
            String metier = m.get("nom_metier");
            String metierKey = DictApproxInversion.findKey(metier.toLowerCase(), dico);
            if(metierKey != null) {
                edgesMetiersFilieres.put(metierKey, filiere);
            }
        });

        return edgesMetiersFilieres;
    }

    private static void removeMetiersBelowBac(
            MetiersScrapped metiersScrapped,
            FichesMetierOnisep fichesMetiers,
            Metiers metiers,
            Edges edgesInteretsMetiers,
            Collection<String> formationsDuSup
    ) {
        Set<String> metiersToRemove = fichesMetiers.metiers().metier().stream()
                .filter(m -> !m.isMetierSup(formationsDuSup))
                .map(m -> cleanup(m.identifiant()))
                .collect(Collectors.toSet());

        metiersScrapped.metiers().values().removeIf(m -> metiersToRemove.contains(m.key()));
        fichesMetiers.metiers().metier().removeIf(m -> !m.isMetierSup(formationsDuSup));
        metiers.metiers().keySet().removeAll(metiersToRemove);
        edgesInteretsMetiers.remove(metiersToRemove);

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
            DescriptifsFormations descriptifs
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
            DescriptifsFormations descriptifs,
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


    public Map<DomainePro, Set<String>> getDomainesVersMetiers() {
        Map<DomainePro, Set<String>> result = new HashMap<>();
        metiers.metiers().forEach((metierIndex, metier) -> metier.domaines().forEach(
                s1 -> result.computeIfAbsent(
                        s1,
                        z -> new HashSet<>()
                ).add(metierIndex)
        ));
        return result;
    }

}

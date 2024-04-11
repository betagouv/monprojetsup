package fr.gouv.monprojetsup.data.update.onisep;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.Helpers;
import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.model.Edges;
import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.data.model.disciplines.LiensMetiersThemesOnisep;
import fr.gouv.monprojetsup.data.model.formations.FilieresToFormationsOnisep;
import fr.gouv.monprojetsup.data.analysis.typesFormation.TypeFormationOni;
import fr.gouv.monprojetsup.data.model.interets.Interets;
import fr.gouv.monprojetsup.data.model.metiers.Metiers;
import fr.gouv.monprojetsup.data.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.model.thematiques.Thematiques;
import fr.gouv.monprojetsup.data.update.onisep.billy.PsupToOnisepLines;
import fr.gouv.monprojetsup.data.update.onisep.formations.Formations;
import fr.gouv.monprojetsup.data.update.onisep.formations.FormationsAvecMetiers;
import fr.gouv.monprojetsup.data.tools.DictApproxInversion;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.update.rome.InteretsRome;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.FORMATION_PREFIX;
import static fr.gouv.monprojetsup.data.Constants.PASS_FL_COD;
import static fr.gouv.monprojetsup.data.Helpers.isMetier;

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

        Formations formations
        ) {

    private static final Logger LOGGER = Logger.getLogger(OnisepData.class.getSimpleName());

    public static final double EDGES_INTERETS_METIERS_WEIGHT = 0.001;

    public static OnisepData fromFiles() throws IOException {

        LOGGER.info("Chargement des secteurs et domaines pro");
        SecteursPro secteursPro = SecteursPro.load();
        List<DomainePro> domainesPro = DomainePro.load();

        LOGGER.info("Chargement des fiches metiers");
        FichesMetierOnisep fichesMetiers = FichesMetierOnisep.load();

        List<MetierOnisep> metiersOnisep = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.METIERS_PATH),
                new TypeToken<List<MetierOnisep>>(){}.getType()
        );
        Metiers metiers = new Metiers(metiersOnisep, domainesPro);

        MetiersScrapped metiersScrapped = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.ONISEP_DESCRIPTIFS_METIERS_PATH),
                MetiersScrapped.class
        );

        metiers.inject(metiersScrapped);

        Thematiques thematiques = Thematiques.load();

        InteretsOnisep interetsOnisep = Serialisation.fromJsonFile(DataSources.getSourceDataFilePath(DataSources.INTERETS_PATH), InteretsOnisep.class);
        Interets interets = new Interets(interetsOnisep);

        Edges edgesThematiquesFilieres = new Edges();
        Serialisation.fromJsonFile(
                        DataSources.getSourceDataFilePath(DataSources.THEMATIQUES_FORMATIONS_PAIRES_PATH),
                        LienThematiqueFormation.class
                ).paires().forEach(
                        p -> edgesThematiquesFilieres.put(
                                Constants.gFlCodToFrontId(p.gFlCod()),
                                thematiques.representatives(p.item())
                        )
                );

        LienThematiqueFormation2 indexThematiqueFormation2
                = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.THEMATIQUES_FORMATIONS_PAIRES_v2_PATH),
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
                DataSources.getSourceDataFilePath(DataSources.METIERS_THEMATIQUES_PAIRES_PATH),
                LiensMetiersThemesOnisep.class
        );

        liens.contents().content().forEach(content -> {
                    String keyMetier = Constants.cleanup(content.keyMetier());
                    if (!metiers.metiers().keySet().contains(keyMetier)) {
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
                DataSources.getSourceDataFilePath(DataSources.METIERS_INTERETS_PAIRES_PATH),
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
        Formations formations = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.ONISEP_FORMATIONS_PATH),
                Formations.class
        );

        removeMetiersBelowBac(
                metiersScrapped,
                fichesMetiers,
                metiers,
                edgesInteretsMetiers,
                formations.getFormationsDuSup()
        );

        LOGGER.info("Chargement de " + DataSources.ONISEP_FORMATIONS_WITH_METIER_PATH);
        FormationsAvecMetiers formationsAvecMetiers = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.ONISEP_FORMATIONS_WITH_METIER_PATH),
                FormationsAvecMetiers.class
        );

        LOGGER.info("Chargement de " + DataSources.ONISEP_PSUP_TO_IDEO_PATH);
        PsupToOnisepLines lines = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(
                        DataSources.ONISEP_PSUP_TO_IDEO_PATH
                ),
                PsupToOnisepLines.class
        );

        LOGGER.info("Calcul des filieres");
        FilieresToFormationsOnisep filieres = FilieresToFormationsOnisep.getFilieres(
                formations,
                formationsAvecMetiers,
                lines
        );



        Edges edgesMetiersFilieres = new Edges();
        Serialisation.fromJsonFile(
                        DataSources.getSourceDataFilePath(DataSources.METIERS_FORMATIONS_PAIRES_PATH_MANUEL),
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
        Map<String, String> dico =
                Serialisation.fromJsonFile(
                        DataSources.getSourceDataFilePath(DataSources.LABELS_DICO),
                        new TypeToken<Map<String,String>>(){}.getType()
                );
        dico.keySet().removeIf(k -> k.startsWith(FORMATION_PREFIX));
        dico.entrySet().forEach(entry -> {
            String val = entry.getValue();
            int i = entry.getValue().indexOf(" groupe [");
            if(i > 0) {
                val = val.substring(0, i);
            }
            entry.setValue(val.toLowerCase());
        });
        List<Map.Entry<String, String>> vslle = dico.entrySet().stream().toList();
        List<Map<String,String>> metiersFormationsAjouts = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.METIERS_FORMATIONS_AJOUT),
                new TypeToken<List<Map<String,String>>>(){}.getType()
                );
        Set<String> unmatched = new HashSet<>();

        metiersFormationsAjouts.forEach(stringStringMap -> {
            /* {
               "nom_filiere": "BTS - Conception et industrialisation en construction navale",
               "fl": "fl10417",
               "nom_metier": "Architecte naval / navale"
             },*/
            String filiere = stringStringMap.get("fl");
            String metier = stringStringMap.get("nom_metier");
            String metierKey = DictApproxInversion.findKey(metier.toLowerCase(), dico);
            //metiers.findMetierKey(metier);
            if(metierKey == null) {
                unmatched.add(metier);
            } else {
                /*
                continue;
                throw new RuntimeException("Failed to parse metier " + metier);*/
                edgesMetiersFilieres.put(metierKey, filiere);
            }
        });
        List<Map<String,String>> metiersFormationsHeritage = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.METIERS_FORMATIONS_HERITAGE),
                new TypeToken<List<Map<String,String>>>(){}.getType()
        );

        metiersFormationsHeritage.forEach(stringStringMap -> {
            /*{
                "nom_filiere": "C.M.I - Acoustique et Vibrations",
                "fl": "fl4073",
                "nom_formation": "L1 - Acoustique et Vibrations"
                }, */
                    String fl = stringStringMap.get("fl");
                    String formation = stringStringMap.get("nom_formation");
                    String formationKey = DictApproxInversion.findKey(formation.toLowerCase(), dico);
//            String formationKey = filieres.findFormationKey(formation);
                    if (formationKey == null) {
                        unmatched.add(formation);
                    } else {
                        edgesMetiersFilieres
                                .getPredecessors(formationKey)
                                .keySet()
                                .stream().filter(s -> isMetier(s))
                                .forEach(s -> edgesMetiersFilieres.put(s, fl)
                        );
                    }
                }
                );



        System.out.print(unmatched.stream().collect(Collectors.joining("\n")));
        //adding links from the big xml and jm and cécile correspondances
        getMetiersVersFormations(filieres, lines, metiers).forEach(
                (key, metierss) -> metierss.forEach(
                        metier -> edgesMetiersFilieres.put(metier, key)
                )
        );

        TypeFormationOni.Typesformations typesformations =
                Serialisation.fromJsonFile(
                        DataSources.getSourceDataFilePath(DataSources.TYPES_FORMATIONS_ONISEP_PATH),
                        TypeFormationOni.Typesformations.class
                );


        return new OnisepData(metiers,
                thematiques,
                interets,
                edgesThematiquesFilieres,
                edgesThematiquesMetiers,
                edgesMetiersFilieres,
                edgesInteretsMetiers,
                filieres,
                lines,
                secteursPro,
                fichesMetiers,
                formations);

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
                .map(m -> Constants.cleanup(m.identifiant()))
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
                interets().interets().put(key, item.libelle_centre_interet());
                //ajout des arètes
                l.forEach(metier -> edgesInteretsMetiers.put(
                        key,
                        Constants.cleanup(metier.id()),
                        false,
                        EDGES_INTERETS_METIERS_WEIGHT
                )
                );
            }
        });
    }

    /**
     * maps a flcod "fl11" to metiers "MET.999"
     *
     * @return
     */
    public Map<String, Set<String>> getExtendedMetiersVersFormations() {
        return getExtendedMetiersVersFormations(
                Collections.emptyMap(),
                new PsupStatistiques.LASCorrespondance(),
                new Descriptifs()
        );
    }

    /**
     * metiers vers filieres
     * @return
     */
    public Map<String, Set<String>> getExtendedMetiersVersFormations(
            Map<String, String> groups,
            PsupStatistiques.LASCorrespondance lasCorrespondance,
            Descriptifs descriptifs
            ) {
        boolean onisepOnly = false;
        Map<String, Set<String>> metiersVersFormations = new HashMap<>();
        getMetiersVersFormations(
                this.filieresToFormationsOnisep,
                this.billy,
                this.metiers
        ).forEach(
                (f, ms) -> ms.forEach(
                        m -> metiersVersFormations.computeIfAbsent(m, z -> new HashSet<>()).add(f)
                )
        );
        getMetiersVersFormations(
                descriptifs, this.metiers
        ).forEach(
                (f, ms) -> ms.forEach(
                        m -> metiersVersFormations.computeIfAbsent(m, z -> new HashSet<>()).add(f)
                )
        );


        if(!onisepOnly) {
            this.edgesMetiersFilieres().edges().forEach((s, strings) ->
                    metiersVersFormations.computeIfAbsent(s, z -> new HashSet<>())
                    .addAll(strings));
        }
        metiersVersFormations.keySet().removeIf(k -> !k.startsWith(Constants.MET_PREFIX));
        metiersVersFormations.values().forEach(strings -> strings.removeIf(s -> !Helpers.isFiliere(s)));


        /* ajouts des las aux metiers PASS */
        String passKey = Constants.gFlCodToFrontId(PASS_FL_COD);
        Set<String> metiersPass =
                metiersVersFormations.entrySet().stream()
                        .filter(e -> e.getValue().contains( passKey))
                        .map(e -> e.getKey())
                        .collect(Collectors.toSet());
        metiersPass.forEach(m ->
                metiersVersFormations.computeIfAbsent( m,
                z -> new HashSet<>())
                        .addAll(lasCorrespondance.lasToGeneric().keySet())
        );

        Map<String, Set<String>> genericToLas = lasCorrespondance.getGenericToLas();

        Map<String, Set<String>> liensGroups = new HashMap<>();
        metiersVersFormations.forEach((s, strings) -> {
            Set<String> list2 = new HashSet<>();
            strings.forEach(s1 -> {
                list2.add(s1);
                /* ajouts des groupes génériquees aux metiers des formations correspondantes */
                list2.add(groups.getOrDefault(s1,s1));
                /* ajouts des las aux metiers des génériques correspondants */
                if(genericToLas.containsKey(s1)) {
                    list2.addAll(genericToLas.get(s1));
                }
            });
            liensGroups.put(s,list2);
        });
        return liensGroups;
    }

    /* filieres to metiers */
    public static Map<String, Set<String>> getMetiersVersFormations(
            Descriptifs descriptifs,
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
                                    .map(metier -> metiers.findMetier(metier))
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
        return result;
    }


    public static Map<String, Set<String>> getSecteursVersMetiers(
            FichesMetierOnisep fiches,
            Collection<String> formationsDuSup
    ) {
        Map<String, Set<String>> result = new HashMap<>();
        fiches.metiers().metier().forEach(fiche -> {
            String keyMetier = Constants.cleanup(fiche.identifiant());
            if (fiche.secteurs_activite() != null && fiche.secteurs_activite().secteur_activite() != null) {
                fiche.secteurs_activite().secteur_activite().forEach(secteur -> {
                    String keySecteur = Constants.cleanup(secteur.id());
                    if(fiche.isMetierSup(formationsDuSup)) {
                        result.computeIfAbsent(keySecteur, z -> new HashSet<>()).add(keyMetier);
                    }
                });
            }
        });
        return result;
    }

    public static Map<DomainePro, Set<String>> getDomainesVersMetiers(Metiers metiers) {
        Map<DomainePro, Set<String>> result = new HashMap<>();
        metiers.metiers().forEach((metierIndex, metier) -> {
            metier.domaines().forEach(
                    s1 -> result.computeIfAbsent(
                            s1,
                            z -> new HashSet<>()
                    ).add(metierIndex)
            );
        });
        return result;
    }


}

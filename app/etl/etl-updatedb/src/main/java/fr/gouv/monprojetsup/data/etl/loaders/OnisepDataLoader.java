package fr.gouv.monprojetsup.data.etl.loaders;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.model.formations.FilieresPsupVersIdeoData;
import fr.gouv.monprojetsup.data.model.formations.FormationIdeoDuSup;
import fr.gouv.monprojetsup.data.model.metiers.MetierIdeoDuSup;
import fr.gouv.monprojetsup.data.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.data.model.onisep.OnisepData;
import fr.gouv.monprojetsup.data.model.onisep.SousDomaineWeb;
import fr.gouv.monprojetsup.data.model.onisep.formations.FicheFormationIdeo;
import fr.gouv.monprojetsup.data.model.onisep.formations.FormationIdeoSimple;
import fr.gouv.monprojetsup.data.model.onisep.formations.PsupToIdeoCorrespondance;
import fr.gouv.monprojetsup.data.model.onisep.metiers.FicheMetierIdeo;
import fr.gouv.monprojetsup.data.model.onisep.metiers.MetierIdeoSimple;
import fr.gouv.monprojetsup.data.model.rome.InteretsRome;
import fr.gouv.monprojetsup.data.model.taxonomie.Taxonomie;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.gouv.monprojetsup.data.Constants.DIAGNOSTICS_OUTPUT_DIR;
import static fr.gouv.monprojetsup.data.Constants.gFlCodToMpsId;
import static fr.gouv.monprojetsup.data.Constants.gFrCodToMpsId;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.DOMAINES_MPS_PATH;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.IDEO_HERITAGES_LICENCES_CPGE_HERITIER_HEADER;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.IDEO_HERITAGES_LICENCES_CPGE_LEGATAIRES_HEADER;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.IDEO_HERITAGES_LICENCES_CPGE_PATH;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.IDEO_HERITAGES_LICENCES_MASTERS_PATH;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.IDEO_HERITAGES_MASTERS_LICENCES_HERITIER_HEADER;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.IDEO_HERITAGES_MASTERS_LICENCES_LEGATAIRES_HEADER;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.IDEO_OD_DOMAINES_PATH;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.IDEO_OD_FORMATIONS_FICHES_URL;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.IDEO_OD_FORMATIONS_SIMPLE_PATH;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.IDEO_OD_FORMATIONS_SIMPLE_URL;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.IDEO_OD_METIERS_SIMPLE_PATH;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.IDEO_OLD_TO_NEW_PATH;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.MPS_FORMATIONS_TO_MPS_DOMAINE_DOMAINE_HEADER;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.MPS_FORMATIONS_TO_MPS_DOMAINE_FORMATION_HEADER;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.OLD_TO_NEW_IDEO_NEW_IDEO_HEADER;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.OLD_TO_NEW_IDEO_OLD_IDEO_HEADER;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.PSUP_HERITAGES_HERITIER_HEADER;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.PSUP_HERITAGES_LEGATAIRES_HEADER;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.PSUP_HERITAGES_PATH;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.PSUP_TO_IDEO_CORRESPONDANCE_PATH;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.PSUP_TO_METIERS_CORRESPONDANCE_PATH;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.PSUP_TO_METIERS_CORRESPONDANCE_PATH_FORMATION_IDEO_HEADER;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.PSUP_TO_METIERS_CORRESPONDANCE_PATH_METIER_IDEO_HEADER;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.PSUP_TO_METIERS_CORRESPONDANCE_PATH_PSUP_HEADER;
import static fr.gouv.monprojetsup.data.model.formations.FormationIdeoDuSup.getSousdomainesWebMpsIds;
import static fr.gouv.monprojetsup.data.tools.CsvTools.readCSV;


public class OnisepDataLoader {
    private static final Logger LOGGER = Logger.getLogger(OnisepData.class.getSimpleName());
    private static final String FORMATION_INCONNUE = "Formation inconnue ";
    private static final String METIER_INCONNU = "Métier inconnu ";

    private static final Map<Pair<String,String>, List<String>> logLiens = new TreeMap<>();

    public static HashMap<Pair<String, String>, List<String>> exportDiagnosticsLiens(Map<String,String> labels) throws IOException {

        try(val csv = CsvTools.getWriter(Constants.DIAGNOSTICS_OUTPUT_DIR + "sourcesLiensFormationsMetiers.csv")) {
            csv.append(List.of("clé formation","label formation", "clé metier","label métoier", "source(s)"));
            logLiens.keySet().removeIf(k -> k.getLeft().startsWith("FOR."));
            logLiens.forEach((p, sourcess) -> {
                csv.append(List.of(
                        p.getLeft(),
                        labels.getOrDefault(p.getLeft(), ""),
                        p.getRight(),
                                labels.getOrDefault(p.getRight(), ""),
                        String.join("\n", sourcess)
                )
                );
            });
        }
        return new HashMap<>(logLiens);

    }

    private static void updateCreationLien(Map<String, FormationIdeoDuSup> formationsPerKey, String source) {
        int i = source.indexOf("/");
        if(i > 0) {
            source = source.substring(i);
        }
        String finalSource = source;
        formationsPerKey.forEach((ideoForKey, value) -> value.metiers().forEach(met -> {
            val p = Pair.of(ideoForKey, met);
            if (!logLiens.containsKey(p)) {
                val l = logLiens.computeIfAbsent(p, z -> new ArrayList<>());
                if(!l.contains(finalSource))
                    l.add(finalSource);
            }
        }));
    }
    private static void updateCreationLien(List<FilieresPsupVersIdeoData> filieresPsupToFormationsMetiersIdeo, String source) {
        int i = source.indexOf("/");
        if(i > 0) {
            source = source.substring(i);
        }
        String finalSource = source;
        filieresPsupToFormationsMetiersIdeo.forEach(f -> {
            val flKey = f.mpsId();
            f.ideoMetiersIds().forEach(m -> {
                val p = Pair.of(flKey, m);
                if(!logLiens.containsKey(p)) {
                    f.ideoFormationsIds().forEach(ideoForKey -> {
                        val p2 = Pair.of(ideoForKey, m);
                        val knowns = logLiens.get(p2);
                        val l = logLiens.computeIfAbsent(p , z -> new ArrayList<>());
                        if(knowns != null) {
                            knowns.forEach(known -> {
                                if(!l.contains(known))
                                    l.add(known);
                            });
                        }
                        if(!l.contains(finalSource))
                            l.add(finalSource);
                    });
                }
            });
        });
    }

    public static @NotNull OnisepData fromFiles(DataSources sources) throws Exception {

        LOGGER.info("Chargement des intérêts et des secteursActivite");

        List<SousDomaineWeb> sousDomainesWeb =  new ArrayList<>(loadDomainesSousDomaines(sources));

        Taxonomie interets = loadInterets(sources);

        Taxonomie domaines = loadDomaines(sources);

        LOGGER.info("Chargements des formations ideo");
        var formationsIdeoDuSup = loadFormationsIdeoDuSup(sources);

        LOGGER.info("Chargement des metiers ideo");
        var metiersIdeoDuSup = loadMetiers(formationsIdeoDuSup.values(), sousDomainesWeb, sources);

        LOGGER.info("Insertion des données ROME dans les données Onisep");
        val romeData = RomeDataLoader.load(sources);
        insertRomeInteretsDansMetiers(romeData.centresInterest(), metiersIdeoDuSup.values()); //before updateLabels

        val filieresPsupToFormationsMetiersIdeo = loadPsupToIdeoCorrespondance(
                sources,
                formationsIdeoDuSup
        );

        val edgesFormations = getEdgesFormations(
                sousDomainesWeb,
                filieresPsupToFormationsMetiersIdeo
        );

        val edgesFormationsDomaines = edgesFormations.getLeft();
        val edgesMetiersFormations = edgesFormations.getRight();


        LOGGER.info("Restriction des secteursActivite et intérêts aux valeurs utilisées");
        Set<String> domainesUsed = new HashSet<>();

        domainesUsed.addAll(metiersIdeoDuSup.values().stream().flatMap(m -> m.domainesWeb().stream()).distinct().toList());
        domainesUsed.addAll(edgesFormationsDomaines.stream().map(Pair::getRight).toList());

        try(val csv = CsvTools.getWriter(Constants.DIAGNOSTICS_OUTPUT_DIR + "domainesInutilises.csv")) {
            csv.append(List.of("id","domaine","sousdomaine"));
            for (SousDomaineWeb sdb : sousDomainesWeb) {
                if (!domainesUsed.contains(sdb.mpsId())) {
                    csv.append(List.of(sdb.ideo(), sdb.domaineOnisep(), sdb.sousDomaineOnisep()));
                }
            }
        }

        int before = sousDomainesWeb.size();
        sousDomainesWeb.removeIf(d -> !domainesUsed.contains(d.mpsId()));
        int after = sousDomainesWeb.size();
        LOGGER.info("Domaines: " + before + " -> " + after);

        HashSet<String> interetsUsed = metiersIdeoDuSup.values().stream().flatMap(m -> m.interets().stream()).collect(Collectors.toCollection(HashSet::new));
        before = interets.size();
        interets.retainAll(interetsUsed);
        after = interets.size();
        LOGGER.info("Intérêts: " + before + " -> " + after);

        return new OnisepData(
                domaines,
                interets,
                edgesFormationsDomaines,
                edgesMetiersFormations,
                filieresPsupToFormationsMetiersIdeo,
                metiersIdeoDuSup.values().stream().sorted(Comparator.comparing(MetierIdeoDuSup::ideo)).toList(),
                formationsIdeoDuSup.values().stream().toList()
        );

    }


    @SuppressWarnings("unused")
    private static void injectInMetiers(
            List<MetierIdeoDuSup> metiersIdeoDuSup,
            Map<String, Set<String>> richIdeoToPoorIdeo
    ) {
        Map<String, MetierIdeoDuSup> metiersIdeoDuSupByKey = metiersIdeoDuSup.stream()
                .collect(Collectors.toMap(MetierIdeoDuSup::ideo, m -> m));
            richIdeoToPoorIdeo.forEach((richId, poorsId) -> {
                val rich = metiersIdeoDuSupByKey.get(richId);
                if(rich == null) throw new RuntimeException(METIER_INCONNU + richId);
                poorsId.forEach(poorId -> {
                    val poor = metiersIdeoDuSupByKey.get(poorId);
                    if(poor == null) throw new RuntimeException(METIER_INCONNU + poorId);
                    poor.inheritFrom(rich);
                });
            });
    }

    public static void injectInFormationsIdeo(
            Map<String, FormationIdeoDuSup> formationsIdeoDuSup,
            Map<String, Set<String>> legataireToHeritier,
            boolean seulementMetiers) {
        legataireToHeritier.forEach((legataireId, heritiersIds) -> {
            val legataire = formationsIdeoDuSup.get(legataireId);
            if (legataire == null) {
                LOGGER.warning(FORMATION_INCONNUE + " légataire " + legataireId);
            } else {
                heritiersIds.forEach(heritierId -> {
                    val poor = formationsIdeoDuSup.get(heritierId);
                    if (poor == null) {
                        LOGGER.warning(FORMATION_INCONNUE + " héritier " +  heritierId);
                    } else {
                        poor.inheritFrom(legataire, seulementMetiers);
                    }
                });
            }
        });
    }

    private static void injectMetiersInFormationsIdeo(
            Map<String, FormationIdeoDuSup> formationsIdeoDuSup,
            Map<String, Set<String>> formationsIdeoToMetiersIdeo) {
        formationsIdeoToMetiersIdeo.forEach((formationId, metiersId) -> {
            val formation = formationsIdeoDuSup.get(formationId);
            if (formation == null) {
                LOGGER.warning(FORMATION_INCONNUE + formationId);
            } else {
                formation.metiers().addAll(metiersId);
            }
        });
    }

    private static void injectInFormationsPsup(
            List<FilieresPsupVersIdeoData> formations,
            Map<String, Set<String>> richPsupToPoorPsup
    ) {
        val formationsParCod = formations.stream()
                .flatMap(f -> Stream.of(
                        Pair.of(gFrCodToMpsId(f.gFrCod()), f),
                        Pair.of(gFlCodToMpsId(f.gFlCod()), f)
                        )
                ).collect(Collectors.groupingBy(Pair::getLeft));

        richPsupToPoorPsup.forEach((richId, poorsId) -> {
            val riches = formationsParCod.get(richId);
            if (riches == null) throw new RuntimeException(FORMATION_INCONNUE + richId);
            riches.forEach(rich -> poorsId.forEach(poorId -> {
                val poors = formationsParCod.get(poorId);
                if (poors == null) throw new RuntimeException(FORMATION_INCONNUE + poorId);
                poors.forEach(p -> p.getRight().inheritMetiersAndDomainesFrom(rich.getRight()));
            }));
        });

    }



    protected static Map<String,Set<String>> loadPsupHeritages(
            DataSources sources,
            Set<String> formations
    ) {
        //mps_heritier,libelle_pauvre,mps_legataire,libelle_riche
        return loadHeritageCsv(
                sources.getSourceDataFilePath(PSUP_HERITAGES_PATH),
                PSUP_HERITAGES_HERITIER_HEADER,
                PSUP_HERITAGES_LEGATAIRES_HEADER,
                formations,
                Map.of()
        );
    }

    private static Map<String, Set<String>> loadHeritageCsv(
            String filename,
            String heritierHeader,
            String legataireHeader,
            Set<String> knownIds,
            Map<String, Set<String>> oldIdeoToNewIdeo
    ) {
        val requiredHeaders = List.of(
                heritierHeader,
                legataireHeader
        );
        val lines = readCSV(
                filename
                , ',',
                requiredHeaders
        );

        Map<String, Set<String>> result = new HashMap<>();

        Set<String> allIds = new HashSet<>(knownIds);
        allIds.addAll(oldIdeoToNewIdeo.values().stream().flatMap(Set::stream).collect(Collectors.toSet()));

        lines.forEach(line -> {
            //l'existence des headers est garantie
            @NotNull String oldLegataire = line.get(legataireHeader);
            final List<String> legataires;
            if(oldIdeoToNewIdeo.containsKey(oldLegataire)) {
                legataires = oldIdeoToNewIdeo.get(oldLegataire).stream().toList();
            } else {
                legataires = List.of(oldLegataire);
            }
            legataires.forEach(legataire -> {
                if (allIds.contains(legataire)) {
                    //l'existence des headers est garantie
                    @NotNull String oldHeritier = line.get(heritierHeader);
                    final List<String> heritiers;
                    if (oldIdeoToNewIdeo.containsKey(oldHeritier)) {
                        heritiers = oldIdeoToNewIdeo.get(oldHeritier).stream().toList();
                    } else {
                        heritiers = List.of(oldHeritier);
                    }
                    heritiers.forEach(heritier -> {
                        if(knownIds.contains(heritier)) {
                            result.computeIfAbsent(legataire, k -> new HashSet<>()).add(heritier);
                        } else {
                            LOGGER.warning("loadHeritageCsv: héritier inconnu " + heritier);
                        }
                    });
                } else {
                    LOGGER.warning("loadHeritageCsv: légataire inconnu " + legataire);
                }
            });
        });
        return result;


    }

    //legataire vers heritier
    protected static Map<String, Set<String>> loadIdeoHeritagesLicencesCpge(
            DataSources sources,
            Set<String> formationsIdeoDuSup, Map<String, @NotNull Set<String>> oldIdeoToNewIdeo) {
        //IDEO2_PREPA,LIBELLE_PREPA,IDEO2_LICENCE,LIBELLE_LICENCE,
        //FOR.1473,Classe préparatoire à l'école nationale des Chartes (1re année),FOR.4666,licence mention philosophie,
        return loadHeritageCsv(
                sources.getSourceDataFilePath(IDEO_HERITAGES_LICENCES_CPGE_PATH),
                IDEO_HERITAGES_LICENCES_CPGE_HERITIER_HEADER,
                IDEO_HERITAGES_LICENCES_CPGE_LEGATAIRES_HEADER,
                formationsIdeoDuSup,
                oldIdeoToNewIdeo
        );
    }

    public static Map<String, Set<String>> loadIdeoHeritagesMastersLicences(
            DataSources sources,
            Set<String> formationsIdeoDuSup,
            Map<String, @NotNull Set<String>> oldIdeoToNewIdeo) {
        //IDEO_HERITAGES_LICENCES_MASTERS_PATH
        //Mention_Licence_MM;Mention_Master_MM;Mention_Licence_ONISEP;ID_Licence_ONISEP;Mention_Master_ONISEP;ID_Master_ONISEP;Score;Remarques
        return loadHeritageCsv(
                sources.getSourceDataFilePath(IDEO_HERITAGES_LICENCES_MASTERS_PATH),
                IDEO_HERITAGES_MASTERS_LICENCES_HERITIER_HEADER,
                IDEO_HERITAGES_MASTERS_LICENCES_LEGATAIRES_HEADER,
                formationsIdeoDuSup,
                oldIdeoToNewIdeo
        );
    }


    protected static Pair<List<Pair<String, String>>, List<Pair<String, String>>> getEdgesFormations(
            List<SousDomaineWeb> sousDomainesWeb,
            List<FilieresPsupVersIdeoData> filieresPsupToFormationsMetiersIdeo) {

        val edgesMetiersFormations = filieresPsupToFormationsMetiersIdeo.stream().flatMap(
                fil -> fil.ideoMetiersIds().stream().map(metier -> Pair.of(metier, fil.mpsId()))
        ).toList();

        val sousdomainesWebByIdeoKey = sousDomainesWeb.stream().collect(Collectors.toMap(SousDomaineWeb::ideo, d -> d));
        val edgesFormationsDomaines = filieresPsupToFormationsMetiersIdeo.stream().flatMap(
                fil -> getSousdomainesWebMpsIds(fil.libellesOuClesSousdomainesWeb(),sousdomainesWebByIdeoKey)
                        .stream().map(domaineId -> Pair.of(fil.mpsId(), domaineId))
        ).toList();

        return Pair.of(edgesFormationsDomaines, edgesMetiersFormations);

    }

    private static void insertRomeInteretsDansMetiers(
            InteretsRome romeInterets,
            Collection<MetierIdeoDuSup> metiersIdeo
    ) {
        Map<String,List<MetierIdeoDuSup>> codeRomeVersMetiers = metiersIdeo.stream()
                .filter(m -> m.codeRome() != null)
                .collect(Collectors.groupingBy(MetierIdeoDuSup::codeRome));

        romeInterets.arbo_centre_interet().forEach(item -> {
            List<MetierIdeoDuSup> metiers = item.liste_metier().stream()
                    .map(InteretsRome.Metier::code_rome)
                    .flatMap(codeRome -> codeRomeVersMetiers.getOrDefault(codeRome, List.of()).stream())
                    .toList();
            if (!metiers.isEmpty()) {
                String key = item.getKey();
                //ajout des arètes
                metiers.forEach(metier -> metier.interets().add(key));
            }
        });
    }

    public static Taxonomie loadInterets(DataSources sources) {
        val lignesCsv = CsvTools.readCSV(sources.getSourceDataFilePath(DataSources.INTERETS_GROUPES_PATH), ',');
        return new  Taxonomie(InteretsLoader.getTaxonomieCategories(lignesCsv));
    }

    public static Taxonomie loadDomaines(DataSources sources) throws Exception {
        val sousDomainesWeb = loadDomainesSousDomaines(sources);
        val lignesCsv = CsvTools.readCSV(sources.getSourceDataFilePath(DOMAINES_MPS_PATH));
        val labels = sousDomainesWeb.stream().collect(Collectors.toMap(SousDomaineWeb::ideo, SousDomaineWeb::sousDomaineOnisep));
        return new Taxonomie(DomainesMpsLoader.getTaxonomieCategories(lignesCsv, labels));
    }


    public static Map<String, MetierIdeoDuSup> loadMetiers(
            Collection<FormationIdeoDuSup> formationsIdeoSuSup,
            List<SousDomaineWeb> sousDomainesWeb,
            DataSources sources
    ) throws Exception {
        List<MetierIdeoSimple> metiersOnisep = loadMetiersSimplesIdeo(sources);
        List<MetiersScrapped.MetierScrap> metiersScrapped = loadMetiersScrapped(sources);
        List<FicheMetierIdeo> fichesMetiers = loadFichesMetiersIdeo(sources);

        return extractMetiersIdeoDuSup(
                metiersOnisep,
                metiersScrapped,
                fichesMetiers,
                formationsIdeoSuSup.stream().map(FormationIdeoDuSup::ideo).collect(Collectors.toSet()),
                sousDomainesWeb
        ).stream().collect(Collectors.toMap(MetierIdeoDuSup::ideo, z -> z));

    }


    private static List<MetiersScrapped.MetierScrap> loadMetiersScrapped(DataSources sources) throws IOException {
        MetiersScrapped metiersScrapped = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.ONISEP_SCRAPPED_DESCRIPTIFS_METIERS_PATH),
                MetiersScrapped.class
        );
        return metiersScrapped.metiers().values().stream().toList();
    }


    protected static List<FilieresPsupVersIdeoData> loadPsupToIdeoCorrespondance(
            DataSources sources,
            Map<String, FormationIdeoDuSup> formationsIdeoDuSup
    ) {
        val oldIdeoToNewIdeo = OnisepDataLoader.loadOldToNewIdeo(sources);

        LOGGER.info("Chargement de " + PSUP_TO_IDEO_CORRESPONDANCE_PATH);
        val csv = CsvTools.readCSV(sources.getSourceDataFilePath(PSUP_TO_IDEO_CORRESPONDANCE_PATH), ',');
        val lines = PsupToIdeoCorrespondance.fromCsv(csv);
        val filieresPsupToFormationsMetiersIdeo = FilieresPsupVersIdeoData.compute(lines, formationsIdeoDuSup, oldIdeoToNewIdeo);
        updateCreationLien(filieresPsupToFormationsMetiersIdeo, PSUP_TO_IDEO_CORRESPONDANCE_PATH);

        filieresPsupToFormationsMetiersIdeo.forEach( f -> f.updateOldToNewIdeo(oldIdeoToNewIdeo));
        updateCreationLien(filieresPsupToFormationsMetiersIdeo, IDEO_OLD_TO_NEW_PATH);

        val psupToMetiersIdeo = loadLiensFormationsPsupMetiers(sources);
        FilieresPsupVersIdeoData.replaceLiensFormationsPsupMetiers(filieresPsupToFormationsMetiersIdeo, psupToMetiersIdeo);
        updateCreationLien(filieresPsupToFormationsMetiersIdeo, PSUP_TO_METIERS_CORRESPONDANCE_PATH);

        LOGGER.info("Application des héritages psup --> psup");
        val formationsPsup = filieresPsupToFormationsMetiersIdeo.stream()
                .flatMap(l -> Stream.of(
                        Constants.gFlCodToMpsId(l.gFlCod()),
                        gFrCodToMpsId(l.gFrCod())
                ))
                .collect(Collectors.toSet());
        val heritages = loadPsupHeritages(sources, formationsPsup);
        injectInFormationsPsup(filieresPsupToFormationsMetiersIdeo, heritages);
        updateCreationLien(filieresPsupToFormationsMetiersIdeo, PSUP_HERITAGES_PATH);

        return filieresPsupToFormationsMetiersIdeo;
    }


    protected static @NotNull Map<String,@NotNull List<@NotNull String>> loadLiensFormationsPsupMetiers(DataSources sources) {
        Map<String,@NotNull List<@NotNull String>> result = new HashMap<>();
        LOGGER.info("chargement de " + PSUP_TO_METIERS_CORRESPONDANCE_PATH);
        val lines = CsvTools.readCSV(sources.getSourceDataFilePath(PSUP_TO_METIERS_CORRESPONDANCE_PATH), ',');
        for (Map<String,String> line : lines) {
            if(line.isEmpty()) continue;
            String psupIds = line.get(PSUP_TO_METIERS_CORRESPONDANCE_PATH_PSUP_HEADER);
            if(psupIds == null)
                throw new RuntimeException(CsvTools.MISSING_HEADER + PSUP_TO_METIERS_CORRESPONDANCE_PATH_PSUP_HEADER + CsvTools.IN_LINE + line);
            val psupIdList = Arrays.stream(psupIds.split(";")).map(String::trim).filter(s -> !s.isBlank()).toList();
            String ideoId = line.get(PSUP_TO_METIERS_CORRESPONDANCE_PATH_METIER_IDEO_HEADER);
            if(ideoId == null)
                throw new RuntimeException(CsvTools.MISSING_HEADER + PSUP_TO_METIERS_CORRESPONDANCE_PATH_METIER_IDEO_HEADER + CsvTools.IN_LINE + line);
            if(ideoId.isBlank())
                continue;
            for(String psupId : psupIdList) {
                result.computeIfAbsent(psupId, k -> new ArrayList<>()).add(ideoId);
            }
        }
        return result;
    }

    public static @NotNull Map<String,@NotNull List<@NotNull String>> loadLiensFormationsMpsDomainesMps(DataSources sources) {
        Map<String,@NotNull List<@NotNull String>> result = new HashMap<>();
        val lines = CsvTools.readCSV(sources.getSourceDataFilePath(DataSources.MPS_FORMATIONS_TO_MPS_DOMAINE), ',');
        for (Map<String,String> line : lines) {
            if(line.isEmpty()) continue;
            String mpsFormationId = line.get(MPS_FORMATIONS_TO_MPS_DOMAINE_FORMATION_HEADER);
            if(mpsFormationId == null)
                throw new RuntimeException(CsvTools.MISSING_HEADER + MPS_FORMATIONS_TO_MPS_DOMAINE_FORMATION_HEADER + CsvTools.IN_LINE + line);
            String domaineMpsId = line.get(MPS_FORMATIONS_TO_MPS_DOMAINE_DOMAINE_HEADER);
            if(domaineMpsId == null)
                throw new RuntimeException(CsvTools.MISSING_HEADER + MPS_FORMATIONS_TO_MPS_DOMAINE_DOMAINE_HEADER + CsvTools.IN_LINE + line);
            result.computeIfAbsent(mpsFormationId, k -> new ArrayList<>()).add(domaineMpsId);
        }
        return result;
    }


    protected static @NotNull Map<String,@NotNull Set<@NotNull String>> loadLiensFormationsIdeoMetiers(DataSources sources) {
        Map<String,@NotNull Set<@NotNull String>> result = new HashMap<>();
        LOGGER.info("chargement de " + PSUP_TO_METIERS_CORRESPONDANCE_PATH);
        val lines = CsvTools.readCSV(sources.getSourceDataFilePath(PSUP_TO_METIERS_CORRESPONDANCE_PATH), ',');
        for (Map<String,String> line : lines) {
            if(line.isEmpty()) continue;
            String ideoIds = line.get(PSUP_TO_METIERS_CORRESPONDANCE_PATH_FORMATION_IDEO_HEADER);
            if(ideoIds == null)
                throw new RuntimeException(CsvTools.MISSING_HEADER + PSUP_TO_METIERS_CORRESPONDANCE_PATH_FORMATION_IDEO_HEADER + CsvTools.IN_LINE + line);
            val ideosList = Arrays.stream(ideoIds.split(";")).map(String::trim).toList();
            String ideoMetierId = line.get(PSUP_TO_METIERS_CORRESPONDANCE_PATH_METIER_IDEO_HEADER);
            if(ideoMetierId == null)
                throw new RuntimeException(CsvTools.MISSING_HEADER + PSUP_TO_METIERS_CORRESPONDANCE_PATH_METIER_IDEO_HEADER + CsvTools.IN_LINE + line);
            for(String ideoId : ideosList) {
                result.computeIfAbsent(ideoId, k -> new HashSet<>()).add(ideoMetierId);
            }
        }
        return result;
    }


    protected static Map<String,FormationIdeoDuSup> extractFormationsIdeoDuSup(
            List<FormationIdeoSimple> formationsIdeoSansfiche,
            List<FicheFormationIdeo> formationsIdeoAvecFiche
    ) {

        Map<String, FormationIdeoDuSup> formationsPerKey = new HashMap<>(
                formationsIdeoSansfiche.stream()
                        .filter(FormationIdeoSimple::aUnIdentifiantIdeo)
                        .filter(FormationIdeoSimple::estFormationDuSup)
                        .collect(Collectors.toMap(
                                FormationIdeoSimple::identifiant,
                                FormationIdeoDuSup::new
                        ))
        );

        updateCreationLien(formationsPerKey, IDEO_OD_FORMATIONS_SIMPLE_URL);

        //in this order, so that richer information with fiche wins
        formationsPerKey.putAll(
                formationsIdeoAvecFiche
                        .stream()
                        .filter(FicheFormationIdeo::estFormationDuSup)
                        .collect(Collectors.toMap(
                                FicheFormationIdeo::identifiant,
                                FormationIdeoDuSup::new
                        ))
        );

        updateCreationLien(formationsPerKey, IDEO_OD_FORMATIONS_FICHES_URL);

        return formationsPerKey.values().stream()
                .collect(Collectors.toMap(
                        FormationIdeoDuSup::ideo,
                        f -> f
                ));
    }


    private static List<MetierIdeoDuSup> extractMetiersIdeoDuSup(
            List<MetierIdeoSimple> metiersIdeoSimples,
            List<MetiersScrapped.MetierScrap> metiersScrapped,
            List<FicheMetierIdeo> fichesMetiers,
            Set<String> formationsDuSup,
            List<SousDomaineWeb> sousDomainesWeb
    ) {

        Map<String,MetierIdeoDuSup> metiers = new HashMap<>();

        metiersScrapped.forEach(m -> {
            if(m.nom()!= null && !m.nom().isEmpty()) {
                val met = new MetierIdeoDuSup(m);
                metiers.put(met.ideo(), met);
            }
        });

        val sousDomainesWebByIdeoKey = sousDomainesWeb.stream().collect(Collectors.toMap(SousDomaineWeb::ideo, d -> d));
        for (MetierIdeoSimple m : metiersIdeoSimples) {
            var met = metiers.get(m.idIdeo());
            met = MetierIdeoDuSup.merge(m, sousDomainesWebByIdeoKey, met);
            metiers.put(met.ideo(), met);
        }

        for(FicheMetierIdeo m : fichesMetiers) {
            var met = metiers.get(m.identifiant());
            met = MetierIdeoDuSup.merge(m, met);
            metiers.put(met.ideo(), met);
        }

        LOGGER.info("Suppression des metiers non post bac");
        Set<String> metiersToRemove = fichesMetiers.stream()
                .filter(m -> !m.isMetierSup(formationsDuSup))
                .map(FicheMetierIdeo::identifiant)
                .collect(Collectors.toSet());

        metiers.keySet().removeAll(metiersToRemove);

        return metiers.values().stream().toList();
    }

    static List<SousDomaineWeb> loadDomainesSousDomaines(DataSources sources) throws Exception {

        List<SousDomaineWeb> domainesSansId = loadDomainesideo(sources);

        List<FicheFormationIdeo> formationsIdeoAvecFiche = loadFichesFormationsIdeo(sources);

        Map<String, String> sousDomainesAvecId = formationsIdeoAvecFiche.stream()
                .flatMap(f -> f.getSousdomainesWeb().stream())
                .distinct()
                .collect(Collectors.toMap(
                        Pair::getRight,
                        Pair::getLeft
                ));

        val result =  domainesSansId.stream().map(d -> {
            String id = sousDomainesAvecId.get(d.sousDomaineOnisep());
            return new SousDomaineWeb(id, d.domaineOnisep(), d.sousDomaineOnisep());
        }).filter(d -> d.ideo() != null)
                .sorted(Comparator.comparing(d -> d.domaineOnisep() + d.sousDomaineOnisep())).toList();

        try(val csv = CsvTools.getWriter(Constants.DIAGNOSTICS_OUTPUT_DIR + "domaines_sous_domaines.csv")) {
            csv.appendHeaders(List.of("ideo","domaine","sousDomaine"));
            for (SousDomaineWeb d : result) {
                csv.append(List.of(d.ideo(),d.domaineOnisep(),d.sousDomaineOnisep()));
            }
        }

        return result;
    }

    private static List<SousDomaineWeb> loadDomainesideo(DataSources sources) throws IOException {
        val typeToken = new TypeToken<List<SousDomaineWeb>>(){}.getType();
        return Serialisation.fromLocalJson(sources.getSourceDataFilePath(IDEO_OD_DOMAINES_PATH), typeToken);
    }


    public static List<FormationIdeoSimple> loadFormationsSimplesIdeo(DataSources sources) throws Exception {
        val typeToken = new TypeToken<List<FormationIdeoSimple>>(){}.getType();
        return Serialisation.fromLocalJson(sources.getSourceDataFilePath(IDEO_OD_FORMATIONS_SIMPLE_PATH), typeToken);
    }


    public static List<MetierIdeoSimple> loadMetiersSimplesIdeo(DataSources sources) throws Exception {
        val typeToken = new TypeToken<List<MetierIdeoSimple>>(){}.getType();
        return Serialisation.fromLocalJson(sources.getSourceDataFilePath(IDEO_OD_METIERS_SIMPLE_PATH), typeToken);
    }

    public static @NotNull Map<String,@NotNull Set<String>> loadOldToNewIdeo(DataSources sources) {
        val csv = CsvTools.readCSV(sources.getSourceDataFilePath(IDEO_OLD_TO_NEW_PATH));
        Map<String,@NotNull Set<String>> result = new HashMap<>();
        csv.forEach(line -> {
            if(line.size() != 2) {
                throw new RuntimeException("Invalid line " + line);
            }
            if(!line.containsKey(OLD_TO_NEW_IDEO_OLD_IDEO_HEADER) || !line.containsKey(OLD_TO_NEW_IDEO_NEW_IDEO_HEADER)) {
                throw new RuntimeException(CsvTools.MISSING_HEADER + OLD_TO_NEW_IDEO_OLD_IDEO_HEADER + " or " + OLD_TO_NEW_IDEO_NEW_IDEO_HEADER);
            }
            result.computeIfAbsent(line.get(OLD_TO_NEW_IDEO_OLD_IDEO_HEADER), k -> new HashSet<>()).add(line.get(OLD_TO_NEW_IDEO_NEW_IDEO_HEADER));
        });
        return result;
    }



    public static List<FicheFormationIdeo> loadFichesFormationsIdeo(DataSources sources) throws Exception {
        JavaType listType = new ObjectMapper().getTypeFactory().constructCollectionType(
                List.class,
                FicheFormationIdeo.class
        );
        return Serialisation.fromZippedXml(
                sources.getSourceDataFilePath(DataSources.IDEO_OD_FORMATIONS_FICHES_PATH),
                listType
        );
    }

    public static List<FicheMetierIdeo> loadFichesMetiersIdeo(DataSources sources) throws IOException {
        JavaType listType = new ObjectMapper().getTypeFactory().constructCollectionType(
                List.class,
                FicheMetierIdeo.class
        );
        return Serialisation.fromZippedXml(
                sources.getSourceDataFilePath(DataSources.IDEO_OD_METIERS_FICHES_PATH),
                listType
        );
    }


    private OnisepDataLoader() {}

    @NotNull
    public static Map<String, @NotNull FormationIdeoDuSup> loadFormationsIdeoDuSup(DataSources sources) throws Exception {
        val formationsIdeoSansfiche = OnisepDataLoader.loadFormationsSimplesIdeo(sources);
        val formationsIdeoAvecFiche = OnisepDataLoader.loadFichesFormationsIdeo(sources);
        val formationsIdeoDuSup = extractFormationsIdeoDuSup(
                formationsIdeoSansfiche,
                formationsIdeoAvecFiche
        );
        val metiersIdeoDudup = OnisepDataLoader.loadMetiersSimplesIdeo(sources);

        val oldIdeoToNewIdeo = OnisepDataLoader.loadOldToNewIdeo(sources);
        val mastersIdeoToLicencesIdeo = loadIdeoHeritagesMastersLicences(sources, formationsIdeoDuSup.keySet(), oldIdeoToNewIdeo);
        injectInFormationsIdeo(formationsIdeoDuSup, mastersIdeoToLicencesIdeo, true);
        updateCreationLien(formationsIdeoDuSup, IDEO_HERITAGES_LICENCES_MASTERS_PATH);

        val licencesIdeoToCPGEIdeo  = loadIdeoHeritagesLicencesCpge(sources, formationsIdeoDuSup.keySet(), oldIdeoToNewIdeo);
        injectInFormationsIdeo(formationsIdeoDuSup, licencesIdeoToCPGEIdeo, false);
        updateCreationLien(formationsIdeoDuSup, IDEO_HERITAGES_LICENCES_CPGE_PATH);

        val formationsIdeoToMetiersIdeo = loadLiensFormationsIdeoMetiers(sources);
        injectMetiersInFormationsIdeo(formationsIdeoDuSup, formationsIdeoToMetiersIdeo);
        updateCreationLien(formationsIdeoDuSup, PSUP_TO_METIERS_CORRESPONDANCE_PATH);

        val known = new HashSet<>(formationsIdeoDuSup.keySet());
        known.addAll(metiersIdeoDudup.stream().map(MetierIdeoSimple::idIdeo).collect(Collectors.toSet()));
        outputMissingcodesdiagnostics(mastersIdeoToLicencesIdeo, "heritagesMastersLicencesCodesInconnus.csv", "master", "licence", formationsIdeoDuSup.keySet());
        outputMissingcodesdiagnostics(licencesIdeoToCPGEIdeo, "heritagesCPGELicencesCodesInconnus.csv", "licence", "cpge", formationsIdeoDuSup.keySet());
        outputMissingcodesdiagnostics(formationsIdeoToMetiersIdeo, "heritagesFormationsMetiersCodesInconnus.csv", "formation", "metier", known);

        return formationsIdeoDuSup;
    }


    private static void outputMissingcodesdiagnostics(
            Map<String, Set<String>> correspondance,
            String filename,
            String legataire,
            String heritier,
            Set<String> formationsIdeoDuSup
    ) throws IOException {
        try (val csv = CsvTools.getWriter(DIAGNOSTICS_OUTPUT_DIR + filename)) {
            val headers = List.of(
                    "code " + legataire,
                    "code " + heritier,
                    "code inconnu"
            );
            csv.appendHeaders(headers);
            correspondance.forEach((master, licences) -> {
                if (!formationsIdeoDuSup.contains(master)) {
                    csv.append(List.of(master, String.join(";", licences), master));
                }
                licences.forEach(licence -> {
                    if (!formationsIdeoDuSup.contains(licence)) {
                        csv.append(List.of(master, licence, licence));
                    }
                });
            });
        }
    }


}

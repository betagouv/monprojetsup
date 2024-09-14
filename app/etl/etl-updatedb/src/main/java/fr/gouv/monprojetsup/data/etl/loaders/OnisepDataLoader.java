package fr.gouv.monprojetsup.data.etl.loaders;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.model.formations.FilieresPsupVersIdeoData;
import fr.gouv.monprojetsup.data.domain.model.formations.FormationIdeoDuSup;
import fr.gouv.monprojetsup.data.domain.model.interets.Interets;
import fr.gouv.monprojetsup.data.domain.model.metiers.MetierIdeoDuSup;
import fr.gouv.monprojetsup.data.domain.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.data.domain.model.onisep.OnisepData;
import fr.gouv.monprojetsup.data.domain.model.onisep.SousDomaineWeb;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.FicheFormationIdeo;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.FormationIdeoSimple;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.PsupToIdeoCorrespondance;
import fr.gouv.monprojetsup.data.domain.model.onisep.metiers.FicheMetierIdeo;
import fr.gouv.monprojetsup.data.domain.model.onisep.metiers.MetierIdeoSimple;
import fr.gouv.monprojetsup.data.domain.model.rome.InteretsRome;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.gouv.monprojetsup.data.domain.Constants.*;
import static fr.gouv.monprojetsup.data.domain.model.formations.FormationIdeoDuSup.getSousdomainesWebMpsIds;
import static fr.gouv.monprojetsup.data.etl.loaders.DataSources.*;
import static fr.gouv.monprojetsup.data.tools.CsvTools.readCSV;


public class OnisepDataLoader {
    private static final Logger LOGGER = Logger.getLogger(OnisepData.class.getSimpleName());
    private static final String FORMATION_INCONNUE = "Formation inconnue ";
    private static final String METIER_INCONNU = "Métier inconnu ";

    public static @NotNull OnisepData fromFiles(DataSources sources) throws Exception {

        LOGGER.info("Chargement des intérêts et des secteursActivite");

        List<SousDomaineWeb> sousDomainesWeb =  new ArrayList<>(loadDomainesSousDomaines(sources));

        Interets interets = loadInterets(sources);

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

        val edgesMetiers = getEdgesMetiers(metiersIdeoDuSup.values());
        val edgesDomainesMetiers = edgesMetiers.getLeft();
        val edgesInteretsMetiers = edgesMetiers.getMiddle();
        val edgesSecteursMetiers = edgesMetiers.getRight();

        LOGGER.info("Restriction des secteursActivite et intérêts aux valeurs utilisées");
        Set<String> domainesUsed = new HashSet<>();

        domainesUsed.addAll(edgesDomainesMetiers.stream().map(Pair::getLeft).toList());
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

        Set<String> interetsUsed = new HashSet<>(edgesInteretsMetiers.stream().map(Pair::getLeft).toList());
        before = interets.size();
        interets.retainAll(interetsUsed);
        after = interets.size();
        LOGGER.info("Intérêts: " + before + " -> " + after);

        LOGGER.info("Injection des secteursActivite webs onisep dans les secteursActivite MPS");
        val domainesMps = DomainesMpsLoader.load(sources);

        return new OnisepData(
                domainesMps,
                interets,
                edgesFormationsDomaines,
                edgesDomainesMetiers,
                edgesSecteursMetiers,
                edgesMetiersFormations,
                edgesInteretsMetiers,
                filieresPsupToFormationsMetiersIdeo,
                metiersIdeoDuSup.values().stream().sorted(Comparator.comparing(MetierIdeoDuSup::idMps)).toList(),
                formationsIdeoDuSup.values().stream().toList()
        );

    }


    @SuppressWarnings("unused")
    private static void injectInMetiers(
            List<MetierIdeoDuSup> metiersIdeoDuSup,
            Map<String, Set<String>> richIdeoToPoorIdeo
    ) {
        Map<String, MetierIdeoDuSup> metiersIdeoDuSupByKey = metiersIdeoDuSup.stream()
                .collect(Collectors.toMap(MetierIdeoDuSup::idMps, m -> m));
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

    private static void injectInFormationsIdeo(
            Map<String, FormationIdeoDuSup> formationsIdeoDuSup,
            Map<String, Set<String>> richIdeoToPoorIdeo) {
        richIdeoToPoorIdeo.forEach((richId, poorsId) -> {
            val rich = formationsIdeoDuSup.get(richId);
            if (rich == null) {
                LOGGER.warning(FORMATION_INCONNUE + " légataire " + richId);
            } else {
                poorsId.forEach(poorId -> {
                    val poor = formationsIdeoDuSup.get(poorId);
                    if (poor == null) {
                        LOGGER.warning(FORMATION_INCONNUE + " héritier " +  poorId);
                    } else {
                        poor.inheritFrom(rich);
                    }
                });
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
        return loadHeritageCsv(sources.getSourceDataFilePath(PSUP_HERITAGES_PATH), PSUP_HERITAGES_HERITIER_HEADER, PSUP_HERITAGES_LEGATAIRES_HEADER, formations);
    }

    private static Map<String, Set<String>> loadHeritageCsv(String filename, String heritierHeader, String legataireHeader, Set<String> formations) {
        val requiredHeaders = List.of(
                heritierHeader,
                legataireHeader
        );
        val lines = readCSV(
                filename
                , ',',
                requiredHeaders
        );
        val ligneAvecCodeInconnu = lines.stream().flatMap
                        (l -> Stream.of(l.get(heritierHeader),
                                l.get(legataireHeader)
                        )).filter(id ->
                        !formations.contains(id))
                .findAny();
        ligneAvecCodeInconnu.ifPresent(s -> LOGGER.warning("Code inconnu " + s));

        //l'existence des headers est garantie
        return lines.stream().collect(Collectors.groupingBy(
                        line -> line.get(legataireHeader),//rich
                        Collectors.mapping(
                                line -> line.get(heritierHeader),//poor,
                                Collectors.toSet()
                        )
                )
        );


    }

    //legataire vers heritier
    protected static Map<String, Set<String>> loadIdeoHeritagesLicencesCpge(
            DataSources sources,
            Set<String> formationsIdeoDuSup) {
        //IDEO2_PREPA,LIBELLE_PREPA,IDEO2_LICENCE,LIBELLE_LICENCE,
        //FOR.1473,Classe préparatoire à l'école nationale des Chartes (1re année),FOR.4666,licence mention philosophie,
        return loadHeritageCsv(sources.getSourceDataFilePath(IDEO_HERITAGES_LICENCES_CPGE_PATH), IDEO_HERITAGES_LICENCES_CPGE_HERITIER_HEADER, IDEO_HERITAGES_LICENCES_CPGE_LEGATAIRES_HEADER, formationsIdeoDuSup);
    }

    private static Map<String, Set<String>> loadIdeoHeritagesMastersLicences(
            DataSources sources,
            Set<String> formationsIdeoDuSup
    ) {
        //IDEO_HERITAGES_LICENCES_MASTERS_PATH
        //Mention_Licence_MM;Mention_Master_MM;Mention_Licence_ONISEP;ID_Licence_ONISEP;Mention_Master_ONISEP;ID_Master_ONISEP;Score;Remarques
        return loadHeritageCsv(sources.getSourceDataFilePath(IDEO_HERITAGES_LICENCES_MASTERS_PATH), IDEO_HERITAGES_MASTERS_LICENCES_HERITIER_HEADER, IDEO_HERITAGES_MASTERS_LICENCES_LEGATAIRES_HEADER, formationsIdeoDuSup);
    }


    protected static Triple<
                List<Pair<String, String>>,
                List<Pair<String, String>>,
                List<Pair<String, String>>
                > getEdgesMetiers(
            Collection<MetierIdeoDuSup> metiersIdeoDuSup
    ) {
        val edgesDomainesMetiers = new ArrayList<Pair<String,String>>();
        val edgesInteretsMetiers = new ArrayList<Pair<String,String>>();
        val edgesSecteursMetiers = new ArrayList<Pair<String,String>>();
        metiersIdeoDuSup.forEach(m -> {
            val idMps = m.idMps();
            m.secteursActivite().forEach(key
                    -> edgesSecteursMetiers.add(Pair.of(cleanup(key), idMps))
            );
            m.interets().forEach(interetKey
                    -> edgesInteretsMetiers.add(Pair.of(cleanup(interetKey), idMps))
            );
        });
        return Triple.of(edgesDomainesMetiers, edgesInteretsMetiers, edgesSecteursMetiers);
    }

    protected static Pair<List<Pair<String, String>>, List<Pair<String, String>>> getEdgesFormations(
            List<SousDomaineWeb> sousDomainesWeb,
            List<FilieresPsupVersIdeoData> filieresPsupToFormationsMetiersIdeo) {

        val edgesMetiersFormations = filieresPsupToFormationsMetiersIdeo.stream().flatMap(
                fil -> fil.ideoMetiersIds().stream().map(metier -> Pair.of(cleanup(metier), fil.mpsId()))
        ).toList();

        val sousdomainesWebByIdeoKey = sousDomainesWeb.stream().collect(Collectors.toMap(SousDomaineWeb::ideo, d -> d));
        val edgesFormationsDomaines = filieresPsupToFormationsMetiersIdeo.stream().flatMap(
                fil -> getSousdomainesWebMpsIds(fil.libellesOuClesSousdomainesWeb(),sousdomainesWebByIdeoKey)
                        .stream().map(domaineId -> Pair.of(fil.mpsId(), cleanup(domaineId)))
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

    public static Interets loadInterets(DataSources sources) throws IOException {
        val groupes = CsvTools.readCSV(sources.getSourceDataFilePath(DataSources.INTERETS_GROUPES_PATH), '\t');
        val metiers = loadFichesMetiersIDeo(sources);
        val romeData = RomeDataLoader.load(sources);
        val labels = new HashMap<String,String>();
        labels.putAll(romeData.getLabels());
        labels.putAll(
                metiers.stream()
                        .flatMap(m -> m.getInterestLabels().stream())
                        .distinct()
                        .collect(Collectors.toMap(
                                Pair::getLeft,
                                Pair::getRight
                        ))
        );
        return Interets.getInterets(groupes, labels);
    }


    public static Map<String, MetierIdeoDuSup> loadMetiers(
            Collection<FormationIdeoDuSup> formationsIdeoSuSup,
            List<SousDomaineWeb> domainesPro,
            DataSources sources
    ) throws Exception {
        List<MetierIdeoSimple> metiersOnisep = loadMetiersSimplesIdeo(sources);
        List<MetiersScrapped.MetierScrap> metiersScrapped = loadMetiersScrapped(sources);
        List<FicheMetierIdeo> fichesMetiers = loadFichesMetiersIDeo(sources);

        return extractMetiersIdeoDuSup(
                metiersOnisep,
                metiersScrapped,
                fichesMetiers,
                formationsIdeoSuSup.stream().map(FormationIdeoDuSup::ideo).collect(Collectors.toSet()),
                domainesPro
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
        LOGGER.info("Chargement de " + DataSources.PSUP_TO_IDEO_CORRESPONDANCE_PATH);
        val csv = CsvTools.readCSV(sources.getSourceDataFilePath(DataSources.PSUP_TO_IDEO_CORRESPONDANCE_PATH), ',');
        val lines = PsupToIdeoCorrespondance.fromCsv(csv);
        val filieresPsupToFormationsMetiersIdeo = FilieresPsupVersIdeoData.compute(lines, formationsIdeoDuSup);

        LOGGER.info("Chargement des héritages psup --> psup");
        val formationsPsup = filieresPsupToFormationsMetiersIdeo.stream()
                .flatMap(l -> Stream.of(
                        Constants.gFlCodToMpsId(l.gFlCod()),
                        gFrCodToMpsId(l.gFrCod())
                ))
                .collect(Collectors.toSet());
        val heritages = loadPsupHeritages(sources, formationsPsup);

        LOGGER.info("Application des héritages psup --> psup");
        injectInFormationsPsup(filieresPsupToFormationsMetiersIdeo, heritages);

        return filieresPsupToFormationsMetiersIdeo;
    }


    protected static Map<String,FormationIdeoDuSup> extractFormationsIdeoDuSup(List<FormationIdeoSimple> formationsIdeoSansfiche, List<FicheFormationIdeo> formationsIdeoAvecFiche) {

        Map<String, FormationIdeoDuSup> formationsPerKey = new HashMap<>();

        formationsPerKey.putAll(
                formationsIdeoSansfiche.stream()
                        .filter(FormationIdeoSimple::aUnIdentifiantIdeo)
                        .filter(FormationIdeoSimple::estFormationDuSup)
                .collect(Collectors.toMap(
                        FormationIdeoSimple::identifiant,
                        FormationIdeoDuSup::new
                ))
        );

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

    public static List<FicheMetierIdeo> loadFichesMetiersIDeo(DataSources sources) throws IOException {
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

        val licencesIdeoToCPGEIdeo  = loadIdeoHeritagesLicencesCpge(sources, formationsIdeoDuSup.keySet());
        injectInFormationsIdeo(formationsIdeoDuSup, licencesIdeoToCPGEIdeo);
        val mastersIdeoToLicencesIdeo  = loadIdeoHeritagesMastersLicences(sources, formationsIdeoDuSup.keySet());
        injectInFormationsIdeo(formationsIdeoDuSup, mastersIdeoToLicencesIdeo);
        return formationsIdeoDuSup;
    }

}

package fr.gouv.monprojetsup.data.etl.loaders;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.model.formations.FilieresPsupVersFormationsEtMetiersIdeo;
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
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.domain.Constants.cleanup;


public class OnisepDataLoader {
    private static final Logger LOGGER = Logger.getLogger(OnisepData.class.getSimpleName());

    public static @NotNull OnisepData fromFiles(DataSources sources) throws Exception {

        LOGGER.info("Chargement des intérêts et des domaines");

        List<SousDomaineWeb> sousDomainesWeb =  new ArrayList<>(loadDomainesSousDomaines());

        Interets interets = loadInterets(sources);

        LOGGER.info("Chargements des formations ideo");
        val formationsIdeoDuSup = loadFormationsIdeoDuSup();

        LOGGER.info("Chargement des metiers ideo");
        val metiersIdeoDuSup = loadMetiers(formationsIdeoDuSup.values(), sousDomainesWeb, sources);

        LOGGER.info("Insertion des données ROME dans les données Onisep");
        val romeData = RomeDataLoader.load(sources);
        insertRomeInteretsDansMetiers(romeData.centresInterest(), metiersIdeoDuSup); //before updateLabels

        LOGGER.info("Chargement des correspondances");
        val filieresPsupToFormationsMetiersIdeo = loadPsupToIdeoCorrespondance(sources, formationsIdeoDuSup.values().stream().toList());

        val edgesFormations = getEdgesFormations(sousDomainesWeb, formationsIdeoDuSup, filieresPsupToFormationsMetiersIdeo);
        val edgesFormationsDomaines = edgesFormations.getLeft();
        val edgesMetiersFormations = edgesFormations.getRight();

        val edgesMetiers = getEdgesMetiers(metiersIdeoDuSup);
        val edgesDomainesMetiers = edgesMetiers.getLeft();
        val edgesInteretsMetiers = edgesMetiers.getRight();

        LOGGER.info("Restriction des domaines et intérêts aux valeurs utilisées");
        Set<String> domainesUsed = new HashSet<>();
        domainesUsed.addAll(edgesDomainesMetiers.stream().map(Pair::getLeft).toList());
        domainesUsed.addAll(edgesFormationsDomaines.stream().map(Pair::getRight).toList());
        int before = sousDomainesWeb.size();
        sousDomainesWeb.forEach(sdb -> {
                    if(!domainesUsed.contains(sdb.mpsId())) LOGGER.info("Domaine non utilisé: " + sdb);
                }
        );
        sousDomainesWeb.removeIf(d -> !domainesUsed.contains(d.mpsId()));
        int after = sousDomainesWeb.size();
        LOGGER.info("Domaines: " + before + " -> " + after);

        Set<String> interetsUsed = new HashSet<>(edgesInteretsMetiers.stream().map(Pair::getLeft).toList());
        before = interets.size();
        interets.retainAll(interetsUsed);
        after = interets.size();
        LOGGER.info("Intérêts: " + before + " -> " + after);

        LOGGER.info("Injection des domaines webs onisep dans les domaines MPS");
        val domainesMps = DomainesMpsLoader.load(sources);

        return new OnisepData(
                domainesMps,
                interets,
                edgesFormationsDomaines,
                edgesDomainesMetiers,
                edgesMetiersFormations,
                edgesInteretsMetiers,
                filieresPsupToFormationsMetiersIdeo,
                metiersIdeoDuSup,
                formationsIdeoDuSup.values().stream().toList()
        );

    }

    protected static Pair<ArrayList<Pair<String, String>>, ArrayList<Pair<String, String>>> getEdgesMetiers(List<MetierIdeoDuSup> metiersIdeoDuSup) {
        val edgesDomainesMetiers = new ArrayList<Pair<String,String>>();
        val edgesInteretsMetiers = new ArrayList<Pair<String,String>>();
        metiersIdeoDuSup.forEach(m -> {
            m.domaines().forEach(domaineKey
                    -> edgesDomainesMetiers.add(Pair.of(cleanup(domaineKey), m.idMps()))
            );
            m.interets().forEach(interetKey
                    -> edgesInteretsMetiers.add(Pair.of(cleanup(interetKey), m.idMps()))
            );
        });
        return Pair.of(edgesDomainesMetiers, edgesInteretsMetiers);
    }

    protected static Pair<List<Pair<String, String>>, List<Pair<String, String>>> getEdgesFormations(
            List<SousDomaineWeb> sousDomainesWeb,
            Map<String, FormationIdeoDuSup> formationsIdeoDuSup,
            List<FilieresPsupVersFormationsEtMetiersIdeo> filieresPsupToFormationsMetiersIdeo
    ) {
        val edgesFormationsDomaines = new ArrayList<Pair<String,String>>();
        val edgesMetiersFormations = new ArrayList<Pair<String,String>>();

        //baseline: links from the big xml and jm and cécile correspondances
        val sousdomainesWebByIdeoKey = sousDomainesWeb.stream().collect(Collectors.toMap(SousDomaineWeb::ideo, d -> d));
        filieresPsupToFormationsMetiersIdeo.forEach(
                fil -> {
                    val psupId = fil.gFlCod();
                    fil.ideoFormationsIds().stream()
                            .map(formationsIdeoDuSup::get)
                            .filter(Objects::nonNull)
                            .forEach( f -> {
                                        edgesMetiersFormations.addAll(
                                                f.metiers().stream()
                                                        .map(metier -> Pair.of(cleanup(metier), psupId))
                                                        .toList()
                                        );
                                        edgesFormationsDomaines.addAll(
                                                f.getSousdomainesWebMpsIds(sousdomainesWebByIdeoKey).stream()
                                                        .map(cleDomaineMps -> Pair.of(psupId, cleDomaineMps))
                                                        .toList()
                                        );
                                    }
                            );
                }
                );
        return Pair.of(edgesFormationsDomaines, edgesMetiersFormations);

    }

    private static void insertRomeInteretsDansMetiers(
            InteretsRome romeInterets,
            List<MetierIdeoDuSup> metiersIdeo
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

    private static Interets loadInterets(DataSources sources) throws IOException, InterruptedException {
        val groupes = CsvTools.readCSV(sources.getSourceDataFilePath(DataSources.INTERETS_GROUPES_PATH), '\t');
        val metiers = loadFichesMetiersIDeo();
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


    public static List<MetierIdeoDuSup> loadMetiers(
            Collection<FormationIdeoDuSup> formationsIdeoSuSup,
            List<SousDomaineWeb> domainesPro,
            DataSources sources
    ) throws Exception {
        List<MetierIdeoSimple> metiersOnisep = loadMetiersSimplesIdeo();
        List<MetiersScrapped.MetierScrap> metiersScrapped = loadMetiersScrapped(sources);
        List<FicheMetierIdeo> fichesMetiers = loadFichesMetiersIDeo();

       return  extractMetiersIdeoDuSup(
                metiersOnisep,
                metiersScrapped,
                fichesMetiers,
                formationsIdeoSuSup.stream().map(FormationIdeoDuSup::ideo).collect(Collectors.toSet()),
                domainesPro
        );
    }


    private static List<MetiersScrapped.MetierScrap> loadMetiersScrapped(DataSources sources) throws IOException {
        MetiersScrapped metiersScrapped = Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.ONISEP_SCRAPPED_DESCRIPTIFS_METIERS_PATH),
                MetiersScrapped.class
        );
        return metiersScrapped.metiers().values().stream().toList();
    }


    protected static List<FilieresPsupVersFormationsEtMetiersIdeo> loadPsupToIdeoCorrespondance(DataSources sources, List<FormationIdeoDuSup> formationsIdeo) {
        LOGGER.info("Chargement de " + DataSources.PSUP_TO_IDEO_CORRESPONDANCE_PATH);
        val csv = CsvTools.readCSV(sources.getSourceDataFilePath(DataSources.PSUP_TO_IDEO_CORRESPONDANCE_PATH), ',');
        val lines = PsupToIdeoCorrespondance.fromCsv(csv);
        return FilieresPsupVersFormationsEtMetiersIdeo.compute(lines, formationsIdeo);
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
                metiers.put(met.idMps(), met);
            }
        });

        val sousDomainesWebByIdeoKey = sousDomainesWeb.stream().collect(Collectors.toMap(SousDomaineWeb::ideo, d -> d));
        for (MetierIdeoSimple m : metiersIdeoSimples) {
            var met = metiers.get(m.idMps());
            met = MetierIdeoDuSup.merge(m, sousDomainesWebByIdeoKey, met);
            metiers.put(met.idMps(), met);
        }

        for(FicheMetierIdeo m : fichesMetiers) {
            var met = metiers.get(m.idMps());
            met = MetierIdeoDuSup.merge(m, met);
            metiers.put(met.idMps(), met);
        }

        LOGGER.info("Suppression des metiers non post bac");
        Set<String> metiersToRemove = fichesMetiers.stream()
                .filter(m -> !m.isMetierSup(formationsDuSup))
                .map(FicheMetierIdeo::idMps)
                .collect(Collectors.toSet());

        metiers.keySet().removeAll(metiersToRemove);

        return metiers.values().stream().toList();
    }

    public static List<FormationIdeoSimple> loadFormationsSimplesIdeo() throws Exception {
        val typeToken = new TypeToken<List<FormationIdeoSimple>>(){}.getType();
        return Serialisation.fromRemoteJson(DataSources.IDEO_OD_FORMATIONS_SIMPLE_URI, typeToken, Constants.DATA_IDEO_DIRNAME);
    }

    static List<SousDomaineWeb> loadDomainesSousDomaines() throws Exception {
        val typeToken = new TypeToken<List<SousDomaineWeb>>(){}.getType();
        List<SousDomaineWeb> domainesSansId = Serialisation.fromRemoteJson(DataSources.IDEO_OD_DOMAINES_URI, typeToken, Constants.DATA_IDEO_DIRNAME);

        List<FicheFormationIdeo> formationsIdeoAvecFiche = loadFichesFormationsIdeo();

        Map<String, String> sousDomainesAvecId = formationsIdeoAvecFiche.stream()
                .flatMap(f -> f.getSousdomainesWeb().stream())
                .distinct()
                .collect(Collectors.toMap(
                        Pair::getRight,
                        Pair::getLeft
                ));

        return domainesSansId.stream().map(d -> {
            String id = sousDomainesAvecId.get(d.sousDomaineOnisep());
            return new SousDomaineWeb(id, d.domaineOnisep(), d.sousDomaineOnisep());
        }).filter(d -> d.ideo() != null).toList();
    }


    public static List<MetierIdeoSimple> loadMetiersSimplesIdeo() throws Exception {
        val typeToken = new TypeToken<List<MetierIdeoSimple>>(){}.getType();
        return Serialisation.fromRemoteJson(DataSources.IDEO_OD_METIERS_SIMPLE_URI, typeToken, Constants.DATA_IDEO_DIRNAME);
    }


    public static List<FicheFormationIdeo> loadFichesFormationsIdeo() throws Exception {
        JavaType listType = new ObjectMapper().getTypeFactory().constructCollectionType(
                List.class,
                FicheFormationIdeo.class
        );
        return Serialisation.fromRemoteZippedXml(
                DataSources.IDEO_OD_FORMATIONS_FICHES_URI,
                listType, Constants.DATA_IDEO_DIRNAME
        );
    }

    public static List<FicheMetierIdeo> loadFichesMetiersIDeo() throws IOException, InterruptedException {
        JavaType listType = new ObjectMapper().getTypeFactory().constructCollectionType(
                List.class,
                FicheMetierIdeo.class
        );
        return Serialisation.fromRemoteZippedXml(
                DataSources.IDEO_OD_METIERS_FICHES_URI,
                listType, Constants.DATA_IDEO_DIRNAME
        );
    }


    private OnisepDataLoader() {}

    @NotNull
    public static Map<String, @NotNull FormationIdeoDuSup> loadFormationsIdeoDuSup() throws Exception {
        val formationsIdeoSansfiche = OnisepDataLoader.loadFormationsSimplesIdeo();
        val formationsIdeoAvecFiche = OnisepDataLoader.loadFichesFormationsIdeo();
        return extractFormationsIdeoDuSup(
                formationsIdeoSansfiche,
                formationsIdeoAvecFiche
        );
    }
}

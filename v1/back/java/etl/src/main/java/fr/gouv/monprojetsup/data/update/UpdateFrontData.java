package fr.gouv.monprojetsup.data.update;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.config.DataServerConfig;
import fr.gouv.monprojetsup.data.model.attendus.Attendus;
import fr.gouv.monprojetsup.data.model.attendus.GrilleAnalyse;
import fr.gouv.monprojetsup.data.model.cities.CitiesFront;
import fr.gouv.monprojetsup.data.model.cities.CitiesLoader;
import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.data.model.interets.Interets;
import fr.gouv.monprojetsup.data.model.metiers.Metiers;
import fr.gouv.monprojetsup.data.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.data.model.specialites.Specialites;
import fr.gouv.monprojetsup.data.model.specialites.SpecialitesLoader;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.model.tags.TagsSources;
import fr.gouv.monprojetsup.data.model.thematiques.Thematiques;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import fr.gouv.monprojetsup.data.update.onisep.FichesMetierOnisep;
import fr.gouv.monprojetsup.data.update.onisep.OnisepData;
import fr.gouv.monprojetsup.data.update.onisep.SecteursPro;
import fr.gouv.monprojetsup.data.update.psup.PsupData;
import fr.gouv.monprojetsup.data.update.rome.RomeData;
import lombok.val;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static fr.gouv.monprojetsup.data.Constants.*;
import static fr.gouv.monprojetsup.data.tools.Serialisation.fromZippedJson;


public class UpdateFrontData {

    private static final Logger LOGGER = Logger.getLogger(UpdateFrontData.class.getSimpleName());


    public record DataContainer(
            Specialites specialites,
            CitiesFront cities,
            TagsSources sources,
            Descriptifs descriptifs,
            SecteursPro secteursActivite,
            Metiers metiers,
            Thematiques thematiques,
            Interets interets,
            Map<String, List<Descriptifs.Link>> urls,
            Map<String, String> groups,
            Map<String, List<String>> labelsTypes,
            Map<String, String> constants, //stats nationales sur moyenne générale et moyennes au bac
            Map<String, Attendus> eds,
            Map<String, GrilleAnalyse> grillesAnalyseCandidatures,
            Map<String, String> grillesAnalyseCandidaturesLabels,
            List<String> profileFields
    ) {

        public static DataContainer load(
                PsupData psupData,
                OnisepData onisepData,
                Map<String, Descriptifs.Link> links,
                PsupStatistiques.LASCorrespondance lasCorrespondance,
                Map<String, Attendus> eds,
                Map<String, GrilleAnalyse> grilles,
                Map<String, String> labels) throws IOException {

            LOGGER.info("Calcul des correspondance");
            Map<String, String> groups = psupData.getCorrespondances();


            LOGGER.info("Chargement des sources des mots-clés, et extension via la correspondance");
            TagsSources tags = TagsSources.load(groups);


            LOGGER.info("Génération des descriptifs");
            Descriptifs descriptifs = loadDescriptifs(onisepData, groups, lasCorrespondance.lasToGeneric());


            /* we cannot do tthat anymore... need to do it by hand for the moment... and should be a dynmaic call in the future
            List<String> declaredfields = Arrays.stream(ProfileDTO.class.getDeclaredFields()).map(f -> f.getName()).toList();
             */
            List<String> declaredfields = List.of(
                    "niveau",
                    "bac",
                    "duree",
                    "apprentissage",
                    "geo_pref",
                    "spe_classes",
                    "interests",
                    "mention",
                    "moygen",
                    "choices"
            );
            LOGGER.info("Declared fields in ProfileDTO " + declaredfields);

            val urls = updateLinks(onisepData, links, lasCorrespondance.lasToGeneric(), descriptifs);

            DataContainer answer = new DataContainer(
                    SpecialitesLoader.load(ServerData.statistiques),
                    CitiesLoader.loadCitiesFront(),
                    tags,
                    descriptifs,
                    onisepData.domainesWeb(),
                    onisepData.metiers(),
                    onisepData.thematiques(),
                    onisepData.interets(),
                    urls,
                    groups,
                    Map.of(
                            "filiere", List.of(FILIERE_PREFIX, TYPE_FORMATION_PREFIX),
                            "metier", List.of(MET_PREFIX, SEC_ACT_PREFIX_IN_GRAPH),
                            "theme", List.of(THEME_PREFIX),
                            "interest", List.of(CENTRE_INTERETS_ROME, CENTRE_INTERETS_ONISEP),
                            "secteur", List.of(SEC_ACT_PREFIX_IN_GRAPH)
                    ),
                    Map.of(
                            "TOUS_GROUPES_CODE", PsupStatistiques.TOUS_GROUPES_CODE,
                            "MOYENNE_BAC_CODE", Integer.toString(PsupStatistiques.MOYENNE_BAC_CODE),
                            "TOUS_BACS_CODE", PsupStatistiques.TOUS_BACS_CODE,
                            "BACS_GENERAL_CODE", PsupStatistiques.BACS_GENERAL_CODE,
                            "PRECISION_PERCENTILES", Integer.toString(PsupStatistiques.PRECISION_PERCENTILES),
                            "MOYENNE_GENERALE_CODE", Integer.toString(PsupStatistiques.MOYENNE_GENERALE_CODE)
                    ),
                    eds,
                    grilles,
                    labels,
                    declaredfields
            );


            Serialisation.toJsonFile("urls.json", answer.urls, true);

            return answer;
        }

        public static Descriptifs loadDescriptifs(
                OnisepData onisepData,
                Map<String, String> groups,
                Map<String, String> lasCorrespondance
        ) throws IOException {

            LOGGER.info("Chargement des fiches metiers");
            FichesMetierOnisep fichesMetiers = FichesMetierOnisep.load();
            fichesMetiers.metiers().metier().removeIf(fiche ->
                    fiche.identifiant() == null
                            || !onisepData.metiers().metiers().containsKey(cleanup(fiche.identifiant()))
            );

            Descriptifs descriptifs =
                    Serialisation.fromJsonFile(
                            DataSources.getSourceDataFilePath(
                                    DataSources.ONISEP_DESCRIPTIFS_FORMATIONS_PATH
                            ),
                            Descriptifs.class
                    );

            Map<String, String> summaries = Serialisation.fromJsonFile(
                    DataSources.getSourceDataFilePath(
                            DataSources.ONISEP_DESCRIPTIFS_FORMATIONS_RESUMES_PATH
                    ),
                    new TypeToken<>() {
                    }.getType()
            );
            descriptifs.inject(summaries);

            MetiersScrapped metiersScrapped = Serialisation.fromJsonFile(
                    DataSources.getSourceDataFilePath(
                            DataSources.ONISEP_DESCRIPTIFS_METIERS_PATH
                    ),
                    MetiersScrapped.class);
            descriptifs.inject(metiersScrapped);

            descriptifs.injectFichesMetiers(fichesMetiers);
            descriptifs.injectDomainesPro(onisepData.domainesWeb());

            boolean includeMpsData = true;
            if (includeMpsData) {
                addMpsdescriptifs(descriptifs);
            }

            descriptifs.injectGroups(groups);
            descriptifs.injectLas(lasCorrespondance);//in this order after groups
            lasCorrespondance.keySet().forEach(key -> {
                Descriptifs.Descriptif desc = descriptifs.keyToDescriptifs().get(key);
                descriptifs.keyToDescriptifs().put(key, Descriptifs.Descriptif.addToDescriptif(PAS_LAS_TEXT, desc));
            });

            return descriptifs;
        }

        private static void addMpsdescriptifs(Descriptifs descriptifs) {
            val lines = CsvTools.readCSV(
                    DataSources.getSourceDataFilePath(DataSources.RESUMES_MPS_PATH),
                    ',');
            String keyFlFr;
            String keyDescFormation;
            String keyDescFiliere;
            String keyTypeFor = "code type formation";

            Map<String, String> resumesTypesformations = new HashMap<>();

            if (lines.isEmpty()) {
                throw new IllegalStateException("No data in " + DataSources.RESUMES_MPS_PATH);
            }
            val line0 = lines.get(0);
            keyFlFr = line0.keySet().stream().filter(k -> k.contains("code")).findAny().orElse(null);
            keyDescFormation = line0.keySet().stream().filter(k -> k.contains("formation VLauriane")).findAny().orElse(null);
            keyDescFiliere = line0.keySet().stream().filter(k -> k.contains("spécialité")).findAny().orElse(null);

            val keyUrlCorrection = line0.keySet().stream().filter(k -> k.contains("URL corrections")).findAny().orElse(null);

            val keyUrlSupplementaire = line0.keySet().stream().filter(k -> k.contains("Liens supplémentaires")).findAny().orElse(null);

            val keyRetoursOnisep = line0.keySet().stream().filter(k -> k.contains("Onisep")).findAny().orElse(null);

            if (keyFlFr == null || keyDescFormation == null || keyDescFiliere == null || keyUrlSupplementaire == null || keyUrlCorrection == null || keyRetoursOnisep == null)
                throw new IllegalStateException("No key found in " + line0);

            for (val line : lines) {
                val frCod = line.get(keyTypeFor);
                val descFormation = line.getOrDefault(keyDescFormation, "");
                if (!descFormation.isBlank()) {
                    resumesTypesformations.put(frCod, descFormation);
                }
            }

            for (val line : lines) {
                String flfrcod = line.getOrDefault(keyFlFr, "");
                if (flfrcod.isBlank()) {
                    throw new RuntimeException("Empty key " + keyFlFr + " in " + line);
                }

                String frcod = line.getOrDefault(keyTypeFor, "");

                String descForm = line.getOrDefault(keyDescFormation, "");
                if (descForm.isBlank()) {
                    descForm = resumesTypesformations.getOrDefault(frcod, "");
                }
                String descFiliere = line.get(keyDescFiliere);

                var descriptif = descriptifs.keyToDescriptifs().computeIfAbsent(flfrcod, z -> new Descriptifs.Descriptif(line));
                if (descriptif.getMultiUrls() == null) descriptif.setMultiUrls(new HashSet<>());

                if (!descFiliere.isBlank()) {
                    descriptif.setSummary(descFiliere);
                    descriptif.setSummaryFormation(descForm);
                } else if (!descForm.isBlank()) {
                    descriptif.setSummary(descForm);
                }

                val urlSupplementaire = line.getOrDefault(keyUrlSupplementaire, "");
                if (!urlSupplementaire.isBlank()) {
                    descriptif.getMultiUrls().addAll(Arrays.stream(urlSupplementaire.split("\n")).map(String::trim).toList());
                }

                val urlCorrection = line.getOrDefault(keyUrlCorrection, "");
                if (!urlCorrection.isBlank()) {
                    descriptif.setCorrectedUrl(true);
                    descriptif.getMultiUrls().addAll(Arrays.stream(urlCorrection.split("\n")).map(String::trim).toList());
                }

                descriptif.setMpsData(line);
            }
        }

        private static void addUrl(String key, String uri, String label, Map<String, List<Descriptifs.Link>> urls) {
            if (uri != null && !uri.isEmpty()) {
                if (uri.contains("www.terminales2022-2023.fr")) {
                    //deprecated uri
                    return;
                }
                val url = Descriptifs.toAvenirs(uri, label);
                val liste = urls.computeIfAbsent(cleanup(key), z -> new ArrayList<>());
                if(!liste.contains(url)) liste.add(url);
            }
        }

        public static Map<String, List<Descriptifs.Link>> updateLinks(
                OnisepData onisepData,
                Map<String, Descriptifs.Link> links,
                Map<String, String> lasCorrespondance,
                Descriptifs descriptifs
        ) {
            val urls = new HashMap<String, List<Descriptifs.Link>>();
            onisepData.metiers().metiers().forEach((s, metier) -> addUrl(
                    s,
                    "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/" + s.replace('_', '.'),
                    metier.lib(), urls
            ));

            for (val entry : links.entrySet()) {
                addUrl(entry.getKey(), entry.getValue().uri(), entry.getValue().label(), urls);
            }
            onisepData.metiers().metiers().values().forEach(metier -> {
                if (metier.urlRome() != null && !metier.urlRome().isEmpty()) {
                    addUrl(metier.id(), metier.urlRome(), metier.libRome(), urls);
                }
            });
            onisepData.domainesWeb().domaines().forEach(domainePro
                    -> addUrl(cleanup(domainePro.id()), domainePro.url(), domainePro.nom(), urls)
            );
            descriptifs.keyToDescriptifs().forEach((key, descriptif) -> {
                if (descriptif.isCorrectedUrl()) {
                    urls.remove(cleanup(key));
                }
                Collection<String> multiUrls = descriptif.multiUrls();
                if (multiUrls != null) {
                    multiUrls.forEach(url2 -> addUrl(key, url2, url2, urls));
                }
            });
            addUrl(gFlCodToFrontId(PASS_FL_COD), URL_ARTICLE_PAS_LAS, "Les études de santé", urls);
            lasCorrespondance.forEach((keyLas, keyGen) -> {
                addUrl(keyLas, URL_ARTICLE_PAS_LAS,"Les études de santé", urls);
                urls.getOrDefault(keyGen, List.of()).forEach(s -> addUrl(keyLas, s.uri(), s.label(), urls));
            });
            return urls;
        }
    }

    public static void main(String[] args) throws IOException {

        DataServerConfig.load();

        LOGGER.info("Chargement et minimization de " + DataSources.FRONT_MID_SRC_PATH);
        PsupStatistiques data = fromZippedJson(DataSources.getSourceDataFilePath(DataSources.FRONT_MID_SRC_PATH), PsupStatistiques.class);
        data.removeFormations();
        data.removeSmallPopulations();

        LOGGER.info("Chargement et nettoyage de " + DataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME));
        PsupData psupData = Serialisation.fromZippedJson(DataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME), PsupData.class);
        psupData.cleanup();


        LOGGER.info("Chargement des données Onisep");
        final OnisepData onisepData = OnisepData.fromFiles();

        LOGGER.info("Chargement des données ROME");
        RomeData romeData = RomeData.load();

        LOGGER.info("Insertion des données ROME dans les données Onisep");
        onisepData.insertRomeData(romeData.centresInterest()); //before updateLabels

        LOGGER.info("Maj des données Onisep (noms des filières et urls)");
        data.updateLabels(onisepData, psupData, data.getLASCorrespondance().lasToGeneric());

        LOGGER.info("Ajout des liens metiers");
        val urls = new HashMap<String, Descriptifs.Link>();
        data.liensOnisep.forEach((key, value) -> urls.put(key, Descriptifs.toAvenirs(value, data.labels.getOrDefault(key,""))));
        onisepData.metiers().metiers().forEach((s, metier) -> {
            urls.put(s, Descriptifs.toAvenirs(
                    "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/" + s.replace('_','.'),
                    metier.lib())
            );
        });

        Map<String, Attendus> eds = Attendus.getAttendus(
                psupData,
                data,
                SpecialitesLoader.load(ServerData.statistiques),
                false
        );
        Map<String, GrilleAnalyse> grilles = GrilleAnalyse.getGrilles(psupData);

        DataContainer data2 = DataContainer.load(psupData, onisepData, urls, data.getLASCorrespondance(), eds, grilles, GrilleAnalyse.getLabelsMap());

        LOGGER.info("Mise à jour de " + DataSources.getFrontSrcPath());
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(Path.of(DataSources.getFrontSrcPath())))) {
            out.setMethod(8);
            out.setLevel(7);

            try (OutputStreamWriter o = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {

                out.putNextEntry(new ZipEntry(DataSources.FRONT_DATA_JSON_FILENAME2));
                new GsonBuilder().setPrettyPrinting().create().toJson(data2, o);

                o.flush();
                out.putNextEntry(new ZipEntry(DataSources.FRONT_DATA_JSON_FILENAME));
                new GsonBuilder().setPrettyPrinting().create().toJson(data, o);

            }

        }

    }
}

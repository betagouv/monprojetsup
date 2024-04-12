package fr.gouv.monprojetsup.data.update;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.config.DataServerConfig;
import fr.gouv.monprojetsup.data.model.cities.CitiesFront;
import fr.gouv.monprojetsup.data.model.cities.CitiesLoader;
import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.data.model.attendus.Attendus;
import fr.gouv.monprojetsup.data.model.interets.Interets;
import fr.gouv.monprojetsup.data.model.metiers.Metiers;
import fr.gouv.monprojetsup.data.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.data.model.specialites.Specialites;
import fr.gouv.monprojetsup.data.model.specialites.SpecialitesLoader;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.model.tags.TagsSources;
import fr.gouv.monprojetsup.data.model.thematiques.Thematiques;
import fr.gouv.monprojetsup.data.update.onisep.DomainePro;
import fr.gouv.monprojetsup.data.update.onisep.FichesMetierOnisep;
import fr.gouv.monprojetsup.data.update.onisep.OnisepData;
import fr.gouv.monprojetsup.data.update.onisep.SecteursPro;
import fr.gouv.monprojetsup.data.update.psup.PsupData;
import fr.gouv.monprojetsup.data.update.rome.RomeData;
import fr.gouv.monprojetsup.data.tools.Serialisation;

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
            Map<String, Set<String>> urls,
            Map<String, String> groups,
            Map<String,List<String>> labelsTypes,
            Map<String,String> constants, //stats nationales sur moyenne générale et moyennes au bac
            Map<String, Set<String>> liensMetiersFormations,
            Map<String, Set<String>> liensSecteursMetiers,
            Map<DomainePro, Set<String>> liensDomainesMetiers,
            Map<String, Attendus> eds,
            List<String> profileFields
    ) {

        public static DataContainer load(
                PsupData psupData,
                OnisepData onisepData,
                Map<String, String> liensOnisep,
                PsupStatistiques.LASCorrespondance lasCorrespondance,
                Map<String, Attendus> eds
        ) throws IOException {

            LOGGER.info("Calcul des correspondance");
            Map<String, String> groups = psupData.getCorrespondances();


            LOGGER.info("Chargement des sources des mots-clés, et extension via la correspondance");
            TagsSources tags = TagsSources.load(groups);


            LOGGER.info("Génération des descriptifs");
            Descriptifs descriptifs = loadDescriptifs(onisepData, groups, lasCorrespondance.lasToGeneric());


            Map<String, Set<String>> liensMetiersFormations
                    = onisepData.getExtendedMetiersVersFormations(groups, lasCorrespondance, descriptifs);

            Map<String, Set<String>> liensSecteursMetiers
                    = onisepData.getSecteursVersMetiers(
                            onisepData.fichesMetiers(),
                            onisepData.formations().getFormationsDuSup()
            );

            Map<DomainePro, Set<String>> liensDomainesMetiers
                    = OnisepData.getDomainesVersMetiers(onisepData.metiers());

            Map<String, Set<String>> urls = new TreeMap<>();

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

            DataContainer answer = new DataContainer(
                    SpecialitesLoader.load(),
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
                    liensMetiersFormations,
                    liensSecteursMetiers,
                    liensDomainesMetiers,
                    eds,
                    declaredfields
            );

            answer.updateUrls(onisepData, liensOnisep, lasCorrespondance.lasToGeneric());

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

            Descriptifs descriptifs = Serialisation.fromJsonFile(
                    DataSources.getSourceDataFilePath(DataSources.ONISEP_DESCRIPTIFS_FORMATIONS_PATH),
                    Descriptifs.class
            );

            Map<String, String> summaries = Serialisation.fromJsonFile(
                    DataSources.getSourceDataFilePath(DataSources.ONISEP_DESCRIPTIFS_FORMATIONS_RESUMES_PATH),
                    new TypeToken<Map<String, String>>(){}.getType()
            );
            descriptifs.inject(summaries);

            MetiersScrapped metiersScrapped = Serialisation.fromJsonFile(DataSources.getSourceDataFilePath(DataSources.ONISEP_DESCRIPTIFS_METIERS_PATH), MetiersScrapped.class);
            descriptifs.inject(metiersScrapped);

            descriptifs.injectFichesMetiers(fichesMetiers);
            descriptifs.injectDomainesPro(onisepData.domainesWeb());
            descriptifs.injectGroups(groups);
            descriptifs.injectLas(lasCorrespondance);//in this order after groups
            lasCorrespondance.keySet().forEach(key -> {
                Descriptifs.Descriptif desc = descriptifs.keyToDescriptifs().get(key);
                descriptifs.keyToDescriptifs().put(key, Descriptifs.Descriptif.addToDescriptif(PAS_LAS_TEXT, desc));
            });
            /* article actuel ok
            String passKey = gFlCodToFrontId(PASS_FL_COD);
            Descriptifs.Descriptif desc = descriptifs.keyToDescriptifs().get(passKey);
            descriptifs.keyToDescriptifs().put(passKey, addToDescriptif(PAS_LAS_TEXT, desc));
            */
            return  descriptifs;
        }

        private void addUrl(String key, String url) {
            if(url != null && !url.isEmpty()) {
                if(url.contains("www.terminales2022-2023.fr")) {
                    if(url.contains("www.terminales2022-2023.fr/http/redirections/formation/slug/")) {
                        url = url.replace(
                                "www.terminales2022-2023.fr",
                                "www.onisep.fr");
                    } else {
                        return;//we skip those broken urls
                    }
                }
                urls.computeIfAbsent(cleanup(key), z -> new HashSet<>()).add(url);
            }
        }

        public void updateUrls(OnisepData onisepData, Map<String, String> liensOnisep, Map<String, String> lasCorrespondance) {
            urls.clear();
            for (Map.Entry<String, String> entry :  liensOnisep.entrySet()) {
                String key = entry.getKey();
                String s2 = entry.getValue();
                addUrl(key, s2);
            }
            onisepData.metiers().metiers().values().forEach(metier -> {
                if (metier.url() != null && !metier.url().isEmpty()) {
                    addUrl(metier.id(), metier.url());
                }
                if (metier.urlRome() != null && !metier.urlRome().isEmpty()) {
                    addUrl(metier.id(), metier.urlRome());
                }
            });
            onisepData.domainesWeb().domaines().forEach(domainePro -> addUrl(cleanup(domainePro.id()), domainePro.url()));
            descriptifs.keyToDescriptifs().forEach((key, descriptif) -> {
                addUrl(key, descriptif.url());
                List<String> urls = descriptif.multiUrls();
                if(urls != null) {
                    urls.forEach(url2 -> addUrl(key, url2));
                }
            });
            addUrl(gFlCodToFrontId(PASS_FL_COD), URL_ARTICLE_PAS_LAS);
            lasCorrespondance.forEach((keyLas, keyGen) -> {
                addUrl(keyLas, URL_ARTICLE_PAS_LAS);
                urls.getOrDefault(keyGen, Collections.emptySet()).forEach(s -> addUrl(keyLas, s));
            });
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

        LOGGER.info("Chargement des données ROME");
        RomeData romeData = RomeData.load();

        LOGGER.info("Chargement des données Onisep");
        final OnisepData onisepData = OnisepData.fromFiles();

        LOGGER.info("Insertion des données ROME dans les données Onisep");
        onisepData.insertRomeData(romeData.centresInterest()); //before updateLabels

        LOGGER.info("Maj des données Onisep (noms des filières et urls)");
        data.updateLabels(onisepData, psupData, data.getLASCorrespondance().lasToGeneric());

        LOGGER.info("Ajout des liens metiers");
        Map<String,String> urls = new HashMap<>(data.liensOnisep);
        onisepData.metiers().metiers().forEach((s, metier) -> {
            //onisep.fr/http/redirections/metier/slug/[identifiant]
        });

        Map<String, Attendus> eds = Attendus.getAttendus(
                psupData,
                data,
                SpecialitesLoader.load(),
                false
        );
        DataContainer data2 = DataContainer.load(psupData, onisepData, urls, data.getLASCorrespondance(), eds);

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

package fr.gouv.monprojetsup.app.data;

import fr.gouv.monprojetsup.data.model.attendus.Attendus;
import fr.gouv.monprojetsup.data.model.attendus.GrilleAnalyse;
import fr.gouv.monprojetsup.data.model.cities.CitiesFront;
import fr.gouv.monprojetsup.data.model.cities.CitiesLoader;
import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.data.model.descriptifs.DescriptifsLoader;
import fr.gouv.monprojetsup.data.model.interets.Interets;
import fr.gouv.monprojetsup.data.model.metiers.Metiers;
import fr.gouv.monprojetsup.data.model.specialites.Specialites;
import fr.gouv.monprojetsup.data.model.specialites.SpecialitesLoader;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.model.tags.TagsSources;
import fr.gouv.monprojetsup.data.model.thematiques.Thematiques;
import fr.gouv.monprojetsup.data.onisep.OnisepData;
import fr.gouv.monprojetsup.data.onisep.SecteursPro;
import fr.gouv.monprojetsup.data.psup.PsupData;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import lombok.val;

import java.io.IOException;
import java.util.*;

public record FrontDataContainer(
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


    public static FrontDataContainer load(
            PsupData psupData,
            OnisepData onisepData,
            Map<String, Descriptifs.Link> links,
            PsupStatistiques.LASCorrespondance lasCorrespondance,
            Map<String, Attendus> eds,
            Map<String, GrilleAnalyse> grilles,
            Map<String, String> labels,
            DataSources sources) throws IOException {

        //UpdateFrontData.LOGGER.info("Calcul des correspondance");
        Map<String, String> groups = psupData.getCorrespondances();


        //UpdateFrontData.LOGGER.info("Chargement des sources des mots-clés, et extension via la correspondance");
        TagsSources tags = TagsSources.loadTagsSources(groups, sources);


        //UpdateFrontData.LOGGER.info("Génération des descriptifs");
        Descriptifs descriptifs = DescriptifsLoader.loadDescriptifs(onisepData, groups, lasCorrespondance.lasToGeneric(), sources);


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
        //UpdateFrontData.LOGGER.info("Declared fields in ProfileDTO " + declaredfields);

        val urls = LinksUpdater.updateLinks(onisepData, links, lasCorrespondance.lasToGeneric(), descriptifs);

        FrontDataContainer answer = new FrontDataContainer(
                SpecialitesLoader.load(ServerData.statistiques),
                CitiesLoader.loadCitiesFront(sources),
                tags,
                descriptifs,
                onisepData.domainesWeb(),
                onisepData.metiers(),
                onisepData.thematiques(),
                onisepData.interets(),
                urls,
                groups,
                Map.of(
                        "filiere", List.of(Constants.FILIERE_PREFIX, Constants.TYPE_FORMATION_PREFIX),
                        "metier", List.of(Constants.MET_PREFIX, Constants.SEC_ACT_PREFIX_IN_GRAPH),
                        "theme", List.of(Constants.THEME_PREFIX),
                        "interest", List.of(Constants.CENTRE_INTERETS_ROME, Constants.CENTRE_INTERETS_ONISEP),
                        "secteur", List.of(Constants.SEC_ACT_PREFIX_IN_GRAPH)
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

}

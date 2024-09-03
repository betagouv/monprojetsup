package fr.gouv.monprojetsup.data.etl.loaders;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.domain.model.formations.FiliereToFormationsOnisep;
import fr.gouv.monprojetsup.data.domain.model.formations.FormationIdeoDuSup;
import fr.gouv.monprojetsup.data.domain.model.graph.Edges;
import fr.gouv.monprojetsup.data.domain.model.interets.Interets;
import fr.gouv.monprojetsup.data.domain.model.metiers.MetierIdeoDuSup;
import fr.gouv.monprojetsup.data.domain.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.data.domain.model.onisep.DomainePro;
import fr.gouv.monprojetsup.data.domain.model.onisep.InteretsOnisep;
import fr.gouv.monprojetsup.data.domain.model.onisep.OnisepData;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.FicheFormationIdeo;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.FormationIdeoSimple;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.PsupToIdeoCorrespondance;
import fr.gouv.monprojetsup.data.domain.model.onisep.metiers.FicheMetierIdeo;
import fr.gouv.monprojetsup.data.domain.model.onisep.metiers.MetierIdeoSimple;
import fr.gouv.monprojetsup.data.domain.model.thematiques.Thematiques;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class OnisepDataLoader {
    private static final Logger LOGGER = Logger.getLogger(OnisepData.class.getSimpleName());

    public static @NotNull OnisepData fromFiles(DataSources sources) throws Exception {

        LOGGER.info("Chargement des secteurs et domaines pro");
        List<DomainePro> domainesPro =  Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.DOMAINES_PRO_PATH),
                new TypeToken<List<DomainePro>>() {
                }.getType()
        );


        Thematiques thematiques = ThematiquesLoader.loadThematiquesOnisep(sources);

        InteretsOnisep interetsOnisep = Serialisation.fromJsonFile(sources.getSourceDataFilePath(DataSources.INTERETS_PATH), InteretsOnisep.class);
        val groupes = CsvTools.readCSV(sources.getSourceDataFilePath(DataSources.INTERETS_GROUPES_PATH), '\t');
        Interets interets = new Interets(interetsOnisep, groupes);

        Edges edgesThematiquesFilieres = new Edges();


        Edges edgesThematiquesMetiers = new Edges();


        Set<String> themesUsed = new HashSet<>();
        themesUsed.addAll(edgesThematiquesFilieres.targets());
        themesUsed.addAll(edgesThematiquesMetiers.targets());
        themesUsed.removeIf(t -> !t.startsWith("T_ITM"));
        thematiques.retainAll(themesUsed);


        List<FormationIdeoSimple> formationsIdeoSansfiche = loadFormationsSimplesIdeo();

        List<FicheFormationIdeo> formationsIdeoAvecFiche = loadFichesFormationsIdeo();

        List<FormationIdeoDuSup> formationsIdeoSuSup = extractFormationsIdeoDuSup(
                formationsIdeoSansfiche,
                formationsIdeoAvecFiche
        );

        PsupToIdeoCorrespondance lines = loadPsupToIdeoCorrespondance(sources);

        LOGGER.info("Calcul des filieres");
        List<FiliereToFormationsOnisep> filieresToFormationsOnisep = FiliereToFormationsOnisep.getFilieres(
                lines
        );

        LOGGER.info("Chargement des metiers");
        List<MetierIdeoDuSup> metiersIdeoDuSup = loadMetiers(formationsIdeoSuSup, domainesPro, sources);

        Edges edgesInteretsMetiers = new Edges();
        metiersIdeoDuSup.forEach(m -> {
            m.domaines().forEach(domaineKey -> edgesThematiquesMetiers.put(domaineKey, m.idMps()));
            m.interets().forEach(interetKey -> edgesInteretsMetiers.put(interetKey, m.idMps(), false, OnisepData.EDGES_INTERETS_METIERS_WEIGHT));
        });

        //baseline
        val edgesMetiersFilieres = new Edges();
        //adding links from the big xml and jm and cécile correspondances
        OnisepData.getMetiersVersFormationsMps(
                filieresToFormationsOnisep,
                formationsIdeoSuSup,
                lines,
                metiersIdeoDuSup
        ).forEach(
                (metier, formations) -> formations.forEach(
                        key -> edgesMetiersFilieres.put(metier, key)
                )
        );


        return new OnisepData(
                thematiques,
                interets,
                edgesThematiquesFilieres,
                edgesThematiquesMetiers,
                edgesMetiersFilieres,
                edgesInteretsMetiers,
                filieresToFormationsOnisep,
                lines,
                metiersIdeoDuSup,
                formationsIdeoSuSup);

    }

    public static List<MetierIdeoDuSup> loadMetiers(List<FormationIdeoDuSup> formationsIdeoSuSup, List<DomainePro> domainesPro, DataSources sources) throws Exception {
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


    private static PsupToIdeoCorrespondance loadPsupToIdeoCorrespondance(DataSources sources) throws IOException {
        LOGGER.info("Chargement de " + DataSources.PSUP_TO_IDEO_CORRESPONDANCE_PATH);
        return  Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(
                        DataSources.PSUP_TO_IDEO_CORRESPONDANCE_PATH
                ),
                PsupToIdeoCorrespondance.class
        );

    }

    private static List<FormationIdeoDuSup> extractFormationsIdeoDuSup(List<FormationIdeoSimple> formationsIdeoSansfiche, List<FicheFormationIdeo> formationsIdeoAvecFiche) {

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

        return formationsPerKey.values().stream().toList();
    }

    private static List<MetierIdeoDuSup> extractMetiersIdeoDuSup(
            List<MetierIdeoSimple> metiersIdeoSimples,
            List<MetiersScrapped.MetierScrap> metiersScrapped,
            List<FicheMetierIdeo> fichesMetiers,
            Set<String> formationsDuSup,
            List<DomainePro> domainesPro
    ) {

        Map<String,MetierIdeoDuSup> metiers = new HashMap<>();

        metiersScrapped.forEach(m -> {
            if(m.nom()!= null && !m.nom().isEmpty()) {
                val met = new MetierIdeoDuSup(m);
                metiers.put(met.idMps(), met);
            }
        });

        for (MetierIdeoSimple m : metiersIdeoSimples) {
            var met = metiers.get(m.idMps());
            met = MetierIdeoDuSup.merge(m, domainesPro, met);
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
        return Serialisation.fromRemoteJson(DataSources.IDEO_OD_FORMATIONS_SIMPLE_URI, typeToken, true);
    }

    public static List<MetierIdeoSimple> loadMetiersSimplesIdeo() throws Exception {
        val typeToken = new TypeToken<List<MetierIdeoSimple>>(){}.getType();
        return Serialisation.fromRemoteJson(DataSources.IDEO_OD_METIERS_SIMPLE_URI, typeToken, true);
    }


    public static List<FicheFormationIdeo> loadFichesFormationsIdeo() throws Exception {
        JavaType listType = new ObjectMapper().getTypeFactory().constructCollectionType(
                List.class,
                FicheFormationIdeo.class
        );
        return Serialisation.fromRemoteZippedXml(
                DataSources.IDEO_OD_FORMATIONS_FICHES_URI,
                listType, true
        );
    }

    public static List<FicheMetierIdeo> loadFichesMetiersIDeo() throws IOException, InterruptedException {
        JavaType listType = new ObjectMapper().getTypeFactory().constructCollectionType(
                List.class,
                FicheMetierIdeo.class
        );
        return Serialisation.fromRemoteZippedXml(
                DataSources.IDEO_OD_METIERS_FICHES_URI,
                listType, true
        );
    }


    private OnisepDataLoader() {}
}

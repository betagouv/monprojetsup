package fr.gouv.monprojetsup.data.etl.loaders;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.domain.Constants;
import fr.gouv.monprojetsup.data.domain.Helpers;
import fr.gouv.monprojetsup.data.domain.model.disciplines.LiensMetiersThemesOnisep;
import fr.gouv.monprojetsup.data.domain.model.formations.FilieresToFormationsOnisep;
import fr.gouv.monprojetsup.data.domain.model.graph.Edges;
import fr.gouv.monprojetsup.data.domain.model.interets.Interets;
import fr.gouv.monprojetsup.data.domain.model.metiers.Metiers;
import fr.gouv.monprojetsup.data.domain.model.metiers.MetiersScrapped;
import fr.gouv.monprojetsup.data.domain.model.onisep.*;
import fr.gouv.monprojetsup.data.domain.model.onisep.billy.PsupToOnisepLines;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.FormationsAvecMetiers;
import fr.gouv.monprojetsup.data.domain.model.onisep.formations.FormationsOnisep;
import fr.gouv.monprojetsup.data.domain.model.thematiques.Thematiques;
import fr.gouv.monprojetsup.data.tools.DictApproxInversion;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.domain.Constants.FORMATION_PREFIX;
import static fr.gouv.monprojetsup.data.domain.Constants.cleanup;
import static fr.gouv.monprojetsup.data.etl.loaders.CsvTools.toCsv;

public class OnisepDataLoader {
    private static final Logger LOGGER = Logger.getLogger(OnisepData.class.getSimpleName());

    public static @NotNull OnisepData fromFiles(DataSources sources) throws IOException {

        LOGGER.info("Chargement des secteurs et domaines pro");
        List<DomainePro> domainesPro =  Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(DataSources.DOMAINES_PRO_PATH),
                new TypeToken<List<DomainePro>>() {
                }.getType()
        );

        LOGGER.info("Chargement des fiches metiers");
        FichesMetierOnisep fichesMetiers = FichesMetiersOnisepLoader.loadFichesMetierOnisep(sources);

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

        Thematiques thematiques = ThematiquesLoader.loadThematiquesOnisep(sources);

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
                        OnisepData.EDGES_INTERETS_METIERS_WEIGHT
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
        OnisepData.getMetiersVersFormations(
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
                fichesMetiers,
                formationsOnisep);

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

    private OnisepDataLoader() {}
}

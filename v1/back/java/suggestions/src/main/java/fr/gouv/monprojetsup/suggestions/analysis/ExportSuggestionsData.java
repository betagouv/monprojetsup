package fr.gouv.monprojetsup.suggestions.analysis;

import fr.gouv.monprojetsup.suggestions.data.Constants;
import fr.gouv.monprojetsup.suggestions.data.DataSources;
import fr.gouv.monprojetsup.suggestions.data.Helpers;
import fr.gouv.monprojetsup.suggestions.data.ServerData;
import fr.gouv.monprojetsup.suggestions.data.model.Edges;
import fr.gouv.monprojetsup.suggestions.data.model.thematiques.Thematiques;
import fr.gouv.monprojetsup.suggestions.data.tools.Serialisation;
import fr.gouv.monprojetsup.suggestions.data.update.onisep.LienThematiqueFormation;
import fr.gouv.monprojetsup.suggestions.data.update.onisep.LienThematiqueFormation2;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.suggestions.data.ServerData.getLabel;

@Slf4j
public class ExportSuggestionsData {

    public static final Logger LOGGER = Logger.getLogger(ExportSuggestionsData.class.getName());

    public static void main(String[] args) throws Exception {

        LOGGER.info("Loading data for suggestion Server...");
        ServerData.load();

        log.info("Initializing details ");
        AlgoSuggestions.initialize();

        exportCorrSecteursMetiers();

        exportTypesFormationsPsup();

        exportCorrThemeSfilieres();

        exportGraphStats();

        exportGroupsWithNoMetiers();

        exportGroupsWithNoThemes();

        exportNewEdgesFromNewThematiques();

    }

    private static void exportNewEdgesFromNewThematiques() throws IOException {
        /* ******************** NOUVELLES THEMATIQUES **********************************/
        Thematiques thematiques = ServerData.getThematiques();

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

        LienThematiqueFormation2 indexThematiqueFormation2 = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.THEMATIQUES_FORMATIONS_PAIRES_v2_PATH),
                LienThematiqueFormation2.class
        );
        Map<String, List<String>> newEdges
                = indexThematiqueFormation2.paires()
                .stream()
                .filter(p -> AlgoSuggestions.edgesKeys.hasNode(p.flCod()) && !edgesThematiquesFilieres.hasEdge(p.flCod(), p.Themas()))
                .collect(Collectors.groupingBy(LienThematiqueFormation2.ThematiqueFormationPaireOnisep2::flCod))
                .entrySet()
                .stream().collect(Collectors.toMap(
                                e -> ServerData.getLabel(e.getKey(), e.getKey()),
                                e -> e.getValue().stream().map(d -> ServerData.getLabel(d.Themas(), d.Themas())).toList()
                        )
                );
        Serialisation.toJsonFile("newEdges.json", newEdges, true);

    }


    private static void exportGroupsWithNoThemes() throws IOException {
        /* *** formations sans themes associés  ****/
        Map<String, String> groupsWithNoTheme = AlgoSuggestions.edgesKeys.edges()
                .entrySet().stream().filter(e ->
                        e.getKey().startsWith(Constants.FILIERE_PREFIX)
                                && AlgoSuggestions.edgesKeys.getPredecessors(e.getKey())
                                .keySet().stream().filter(s -> s.startsWith(Constants.THEME_PREFIX)).findAny().isEmpty()
                )
                //.filter(p -> flGroups.getOrDefault(p.getKey(),p.getKey()).equals(p.getKey()))
                .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> getLabel(e.getKey(), e.getKey())
                        )
                );
        Serialisation.toJsonFile("filieresWithNoTheme.json", groupsWithNoTheme, true);
        Map<String, List<String>> filieresWithTheme = new TreeMap<>();
        AlgoSuggestions.edgesKeys.edges().forEach((s, strings) -> {
            if (s.startsWith(Constants.FILIERE_PREFIX)) {
                List<String> themes = AlgoSuggestions.edgesKeys.getPredecessors(s)
                        .keySet().stream().filter(t -> t.startsWith(Constants.THEME_PREFIX))
                        .map(t -> getLabel(t, t))
                        .sorted()
                        .toList();
                filieresWithTheme.put(getLabel(s, s), themes);
            }
        });
        Serialisation.toJsonFile("filieresWithThemes.json", filieresWithTheme, true);

    }

    private static void exportGroupsWithNoMetiers() throws IOException {
        /* *** formations sans métiers associés  ****/
        List<String> groupsWithNoMetiers = AlgoSuggestions.edgesKeys.edges()
                .entrySet().stream().filter(
                        e -> Helpers.isFiliere(e.getKey())
                                && e.getValue().stream().noneMatch(Helpers::isMetier)
                ).map(e -> getLabel(e.getKey(), e.getKey()) + " (" + e.getKey() + ")"
                ).sorted().toList();

        Serialisation.toJsonFile("groupsWithNoMetiers.json", groupsWithNoMetiers, true);

    }

    private static void exportGraphStats() throws IOException {

        /* ** stats ***/
        Map<String, Long> stats = new TreeMap<>();
        stats.put("métiers onisep", (long) ServerData.getNbMetiersOnisep());
        stats.put("métiers graphe", AlgoSuggestions.edgesKeys.nodes().stream().filter(n -> n.startsWith(Constants.MET_PREFIX)).count());
        stats.put("thèmes dans les données onisep", (long) ServerData.getNbThematiquesOnisep());
        stats.put("thèmes graphe", AlgoSuggestions.edgesKeys.nodes().stream().filter(n -> n.startsWith(Constants.THEME_PREFIX)).count());
        stats.put("formations graphe", AlgoSuggestions.edgesKeys.nodes().stream().filter(Helpers::isFiliere).count());
        stats.put("secteurs d'activité", AlgoSuggestions.edgesKeys.nodes().stream().filter(n -> n.startsWith(Constants.SEC_ACT_PREFIX_IN_GRAPH)).count());
        stats.put("centres d'intérêts", AlgoSuggestions.edgesKeys.nodes().stream().filter(n -> n.startsWith(Constants.CENTRE_INTERETS_ROME) || n.startsWith(Constants.CENTRE_INTERETS_ONISEP)).count());
        Serialisation.toJsonFile("stats.json", stats, true);

    }

    protected static void exportCorrSecteursMetiers() throws IOException {

        Map<String, List<String>> themesMetiers = new HashMap<>();
        AlgoSuggestions.edgesKeys.edges().forEach((s, strings) -> {
            if(s.startsWith(Constants.THEME_PREFIX)) {
                String labelTheme = ServerData.getDebugLabel(s);
                List<String> metiers = strings.stream()
                        .filter(m -> m.startsWith(Constants.MET_PREFIX))
                        .map(ServerData::getDebugLabel)
                        .toList();
                themesMetiers.put(labelTheme, metiers);
            }
        });

        Serialisation.toJsonFile("corrThemesMEtiers.json",
                themesMetiers,
                true
        );


        Map<String, List<String>> secteursMetiers = new HashMap<>();
        Map<String, List<String>> metiersSecteurs = new HashMap<>();
        AlgoSuggestions.edgesKeys.edges().forEach((s, strings) -> {
            if(s.startsWith(Constants.SEC_ACT_PREFIX_IN_GRAPH)) {
                String secteur = ServerData.getDebugLabel(s);
                List<String> metiers = strings.stream()
                        .filter(m -> m.startsWith(Constants.MET_PREFIX))
                        .map(ServerData::getDebugLabel)
                        .toList();
                secteursMetiers.put(secteur, metiers);
                metiers.forEach(met -> metiersSecteurs.computeIfAbsent(met, z -> new ArrayList<>()).add(secteur));
            }
        });

        Serialisation.toJsonFile("corrSecteursMetiers.json",
                secteursMetiers,
                true
        );
        Serialisation.toJsonFile("corrMetiersSecteurs.json",
                metiersSecteurs,
                true
        );
        metiersSecteurs.values().removeIf(l -> l.size() >= 2);
        Serialisation.toJsonFile("corrMetiersSecteursSingletons.json",
                metiersSecteurs,
                true
        );

    }

    protected static void exportTypesFormationsPsup() throws IOException {
        Serialisation.toJsonFile("typesFormationsPsup.json",
                new TreeMap<>(ServerData.getTypesMacros()),
                true
        );


    }
    protected static void exportCorrThemeSfilieres() throws IOException {

        Map<String, List<String>> themesFilieres = new HashMap<>();
        AlgoSuggestions.edgesKeys.edges().forEach((s, strings) -> {
            if(s.startsWith(Constants.THEME_PREFIX)) {
                String labelTheme = ServerData.getDebugLabel(s);
                List<String> metiers = strings.stream()
                        .filter(m -> m.startsWith(Constants.FILIERE_PREFIX) || m.startsWith(Constants.TYPE_FORMATION_PREFIX))
                        .map(ServerData::getDebugLabel)
                        .toList();
                themesFilieres.put(labelTheme, metiers);
            }
        });

        Serialisation.toJsonFile("corrThemesFilieres.json",
                themesFilieres,
                true
        );

    }
}

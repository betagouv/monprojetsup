package fr.gouv.monprojetsup.suggestions.export.reference;

import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.domain.Helpers;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.suggestions.algo.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import fr.gouv.monprojetsup.suggestions.port.LabelsPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ExportSuggestionsData {
    private final SuggestionsData data;
    private final AlgoSuggestions algo;
    private final Map<String, String> labels;

    @Autowired
    public ExportSuggestionsData(
            SuggestionsData data,
            AlgoSuggestions algo,
            LabelsPort labelsPort) {
        this.data = data;
        this.algo = algo;
        this.labels = labelsPort.retrieveDebugLabels();
    }

    public void export() throws Exception {


        exportGraphStats();

        exportCorrSecteursMetiers();

        exportCorrThemesFilieres();

        exportGroupsWithNoMetiers();

        exportGroupsWithNoThemes();


    }



    private void exportGroupsWithNoThemes() throws IOException {
        /* *** formations sans themes associés  ****/
        Map<String, String> groupsWithNoTheme = algo.edgesKeys.edges()
                .entrySet().stream().filter(e ->
                        e.getKey().startsWith(Constants.FILIERE_PREFIX)
                                && algo.edgesKeys.getPredecessors(e.getKey())
                                .keySet().stream().filter(s -> s.startsWith(Constants.THEME_PREFIX)).findAny().isEmpty()
                )
                //.filter(p -> flGroups.getOrDefault(p.getKey(),p.getKey()).equals(p.getKey()))
                .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> labels.getOrDefault(e.getKey(), e.getKey())
                        )
                );
        Serialisation.toJsonFile("filieresWithNoTheme.json", groupsWithNoTheme, true);
        Map<String, List<String>> filieresWithTheme = new TreeMap<>();
        algo.edgesKeys.edges().forEach((s, strings) -> {
            if (s.startsWith(Constants.FILIERE_PREFIX)) {
                List<String> themes = algo.edgesKeys.getPredecessors(s)
                        .keySet().stream().filter(t -> t.startsWith(Constants.THEME_PREFIX))
                        .map(t -> labels.getOrDefault(t, t))
                        .sorted()
                        .toList();
                filieresWithTheme.put(labels.getOrDefault(s, s), themes);
            }
        });
        Serialisation.toJsonFile("filieresWithThemes.json", filieresWithTheme, true);

    }

    private void exportGroupsWithNoMetiers() throws IOException {
        /* *** formations sans métiers associés ****/
        List<String> groupsWithNoMetiers = algo.edgesKeys.edges()
                .entrySet().stream().filter(
                        e -> Helpers.isFiliere(e.getKey())
                                && e.getValue().stream().noneMatch(Helpers::isMetier)
                ).map(e -> labels.getOrDefault(e.getKey(), e.getKey()) + " (" + e.getKey() + ")"
                ).sorted().toList();

        Serialisation.toJsonFile("groupsWithNoMetiers.json", groupsWithNoMetiers, true);

    }

    private void exportGraphStats() throws IOException {

        /* ** stats ***/
        Map<String, Long> stats = new TreeMap<>();
        //stats.put("métiers onisep", (long) SuggestionsData.getNbMetiersOnisep());
        stats.put("métiers graphe", algo.edgesKeys.nodes().stream().filter(n -> n.startsWith(Constants.MET_PREFIX)).count());
        //stats.put("thèmes dans les données onisep", (long) SuggestionsData.getNbThematiquesOnisep());
        stats.put("thèmes graphe", algo.edgesKeys.nodes().stream().filter(n -> n.startsWith(Constants.THEME_PREFIX)).count());
        stats.put("formations graphe", algo.edgesKeys.nodes().stream().filter(Helpers::isFiliere).count());
        stats.put("secteurs d'activité", algo.edgesKeys.nodes().stream().filter(n -> n.startsWith(Constants.SEC_ACT_PREFIX_IN_GRAPH)).count());
        stats.put("centres d'intérêts", algo.edgesKeys.nodes().stream().filter(n -> n.startsWith(Constants.CENTRE_INTERETS_ROME) || n.startsWith(Constants.CENTRE_INTERETS_ONISEP)).count());
        Serialisation.toJsonFile("stats.json", stats, true);

    }

    protected void exportCorrSecteursMetiers() throws IOException {

        Map<String, List<String>> themesMetiers = new HashMap<>();
        algo.edgesKeys.edges().forEach((s, strings) -> {
            if(s.startsWith(Constants.THEME_PREFIX)) {
                String labelTheme = data.getDebugLabel(s);
                List<String> metiers = strings.stream()
                        .filter(m -> m.startsWith(Constants.MET_PREFIX))
                        .map(data::getDebugLabel)
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
        algo.edgesKeys.edges().forEach((s, strings) -> {
            if(s.startsWith(Constants.SEC_ACT_PREFIX_IN_GRAPH)) {
                String secteur = data.getDebugLabel(s);
                List<String> metiers = strings.stream()
                        .filter(m -> m.startsWith(Constants.MET_PREFIX))
                        .map(data::getDebugLabel)
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

    protected void exportCorrThemesFilieres() throws IOException {

        Map<String, List<String>> themesFilieres = new HashMap<>();
        algo.edgesKeys.edges().forEach((s, strings) -> {
            if(s.startsWith(Constants.THEME_PREFIX)) {
                String labelTheme = data.getDebugLabel(s);
                List<String> metiers = strings.stream()
                        .filter(m -> m.startsWith(Constants.FILIERE_PREFIX) || m.startsWith(Constants.TYPE_FORMATION_PREFIX))
                        .map(data::getDebugLabel)
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

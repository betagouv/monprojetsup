package fr.gouv.monprojetsup.suggestions.export.reference;

import fr.gouv.monprojetsup.data.commun.entity.LienEntity;
import fr.gouv.monprojetsup.data.formation.entity.FormationEntity;
import fr.gouv.monprojetsup.data.metier.entity.MetierEntity;
import fr.gouv.monprojetsup.data.referentiel.entity.DomaineEntity;
import fr.gouv.monprojetsup.data.referentiel.entity.InteretEntity;
import fr.gouv.monprojetsup.data.tools.CsvTools;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.suggestions.algo.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import fr.gouv.monprojetsup.suggestions.data.model.Edges;
import fr.gouv.monprojetsup.suggestions.export.DomainesDb;
import fr.gouv.monprojetsup.suggestions.export.InteretsDb;
import fr.gouv.monprojetsup.suggestions.export.MetiersDb;
import fr.gouv.monprojetsup.suggestions.infrastructure.FormationDb;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Slf4j
@Component
public class AnalyzeSuggestionsData {

    private final Map<String, String> labels;

    private final Map<String, String> debugLabels;
    private final Edges edges;
    private final Set<String> relatedtoHealth;
    private final Set<String> domaines;
    private final Set<String> metiers;
    private final Set<String> interets;
    private final Map<String, FormationEntity> formations;
    private final Set<String> formationsIds;

    @Autowired
    public AnalyzeSuggestionsData(
            SuggestionsData data,
            AlgoSuggestions algo,
            DomainesDb domainesDb,
            MetiersDb metiersDb,
            InteretsDb interetsDb,
            FormationDb formationDb
    ) {
        this.labels = data.getLabels();
        this.debugLabels = data.getDebugLabels();
        this.edges = algo.getEdgesKeys();
        this.relatedtoHealth = algo.getRelatedToHealth();
        this.domaines = domainesDb.findAll().stream().map(DomaineEntity::getId).collect(Collectors.toSet());
        this.metiers = metiersDb.findAll().stream().map(MetierEntity::getId).collect(Collectors.toSet());
        this.interets = interetsDb.findAll().stream().map(InteretEntity::getId).collect(Collectors.toSet());
        this.formations = formationDb.findAll().stream()
                .collect(Collectors.toMap(
                        FormationEntity::getId,
                        f -> f
                ));
        this.formationsIds = this.formations.keySet();
    }

    private String getDebugLabel(String key) {
        return this.debugLabels.getOrDefault(key, key);
    }


    private boolean isDomaine(String key) {
        return domaines.contains(key);
    }
    private boolean isInteret(String key) {
        return interets.contains(key);
    }
    private boolean isMetier(String key) {
        return metiers.contains(key);
    }

    private boolean isFormation(String key) {
        return formationsIds.contains(key);
    }

    public void analyze() throws Exception {

        outputFormationsAnalysis();

        outputMetiersanalysis();

        /*
        outputRelatedToHealth();

        outputGraph();

        outputSemanticGraph();

        outputMetiersSansFormations();
        */
    }

    private void outputMetiersanalysis() throws IOException {

        val metiersedges = edges.edges().entrySet().stream()
                .filter(e -> isMetier(e.getKey()))
                .map( e -> Triple.of(e.getKey(), getDebugLabel(e.getKey()), e.getValue()))
                .sorted(Comparator.comparing(Triple::getMiddle))
                .toList();

        Serialisation.toJsonFile("metiers_sans_interets_ni_formation.json",
                metiersedges.stream()
                        .filter(e -> e.getRight().stream().noneMatch(f -> isInteret(f) || isFormation(f)))
                        .map(Triple::getMiddle)
                        .toList(),
                true);

    }


    private void outputFormationsAnalysis() throws IOException {

        val formationsEdges = edges.edges().entrySet().stream()
                .filter(e -> isFormation(e.getKey()))
                .map(e -> Triple.of(e.getKey(), getDebugLabel(e.getKey()), e.getValue()))
                .sorted(Comparator.comparing(Triple::getMiddle))
                .toList();


        Serialisation.toJsonFile("formations_sans_domaines.json",
                formationsEdges.stream()
                        .filter(e -> e.getRight().stream().noneMatch(this::isDomaine))
                        .map(Triple::getMiddle)
                        .toList(),
                true);

        Serialisation.toJsonFile("formations_sans_domaines_ni_metier.json",
                formationsEdges.stream()
                        .filter(e -> e.getRight().stream().noneMatch(f -> isDomaine(f) || isMetier(f)))
                        .map(Triple::getMiddle)
                        .toList(),
                true);

        Serialisation.toJsonFile("formations_sans_domaines.json",
                formationsEdges.stream()
                        .filter(e -> e.getRight().stream().noneMatch(this::isDomaine))
                        .map(Triple::getMiddle)
                        .toList(),
                true);

        try (val csvTools = new CsvTools("formations.csv", ',')) {
            csvTools.appendHeaders(List.of(
                    "remarque",
                    "clé MPS",
                    "intitulé",
                    "formations ideos",
                    "formations psup",
                    "metiers",
                    "domaines",
                    "liens psup",
                    "liens onisep",
                    "liens autre"
            ));
            for (Triple<String, String, Set<String>> keyLabelEdgesFormation : formationsEdges) {
                val domainess = keyLabelEdgesFormation.getRight().stream()
                        .filter(this::isDomaine)
                        .map(this::getDebugLabel)
                        .sorted()
                        .collect(Collectors.joining("\n"));
                val metierss = keyLabelEdgesFormation.getRight().stream()
                        .filter(this::isMetier)
                        .map(this::getDebugLabel)
                        .sorted()
                        .collect(Collectors.joining("\n"));
                val formation = this.formations.get(keyLabelEdgesFormation.getLeft());
                Objects.requireNonNull(formation, "formation not found");
                List<String> liens = formation
                        .getLiens()
                        .stream()
                        .map(LienEntity::getUrl)
                        .sorted()
                        .toList();
                val key = keyLabelEdgesFormation.getLeft();
                val remarque = new StringBuilder();
                if(domainess.isBlank()) remarque.append("Pas de domaine\n");
                if(metierss.isBlank()) remarque.append("Pas de métiers\n");
                if(liens.isEmpty()) remarque.append("Pas de liens\n");
                csvTools.append(
                        List.of(
                                remarque.toString(),
                                key,
                                labels.get(key),
                                String.join("\n", Objects.requireNonNull(formation.getFormationsIdeo()).stream().map(this::getDebugLabel).toList()),
                                String.join("\n", Objects.requireNonNull(formation.getFormationsAssociees()).stream().map(this::getDebugLabel).toList()),
                                metierss,
                                domainess,
                                liens.stream().filter(s -> s.contains("parcoursup")).collect(Collectors.joining("\n")),
                                liens.stream().filter(s -> s.contains("avenirs")).collect(Collectors.joining("\n")),
                                liens.stream().filter(s -> !s.contains("parcoursup") && !s.contains("avenirs")).collect(Collectors.joining("\n"))
                        ));
            }
        }


    }


    private void outputGraph() throws IOException {
        Serialisation.toJsonFile("graph.json", edges.edges(), true);
    }
    private void outputSemanticGraph() throws IOException {
        Edges edgesLabels = new Edges();
        edgesLabels.createLabelledGraphFrom(edges, debugLabels);
        Serialisation.toJsonFile("semantic_graph.json", edgesLabels, true);
    }

    private void outputRelatedToHealth() throws IOException {
        /* formations liés aux métiers de la santé */
        Serialisation.toJsonFile("relatedToHealth.json",
                relatedtoHealth
                        .stream().map(this::getDebugLabel)
                        .toList()
                , true);

    }
}

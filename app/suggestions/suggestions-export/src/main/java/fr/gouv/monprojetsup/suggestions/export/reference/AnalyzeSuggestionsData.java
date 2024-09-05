package fr.gouv.monprojetsup.suggestions.export.reference;

import fr.gouv.monprojetsup.data.formation.entity.FormationEntity;
import fr.gouv.monprojetsup.data.metier.entity.MetierEntity;
import fr.gouv.monprojetsup.data.referentiel.entity.DomaineEntity;
import fr.gouv.monprojetsup.data.referentiel.entity.InteretEntity;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.suggestions.algo.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import fr.gouv.monprojetsup.suggestions.data.model.Edges;
import fr.gouv.monprojetsup.suggestions.export.DomainesDb;
import fr.gouv.monprojetsup.suggestions.export.InteretsDb;
import fr.gouv.monprojetsup.suggestions.export.MetiersDb;
import fr.gouv.monprojetsup.suggestions.infrastructure.FormationDb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Slf4j
@Component
public class AnalyzeSuggestionsData {

    private final Map<String, String> labels;
    private final Edges edges;
    private final Set<String> relatedtoHealth;
    private final Set<String> domaines;
    private final Set<String> metiers;
    private final Set<String> interets;
    private final Set<String> formations;

    @Autowired
    public AnalyzeSuggestionsData(
            SuggestionsData data,
            AlgoSuggestions algo,
            DomainesDb domainesDb,
            MetiersDb metiersDb,
            InteretsDb interetsDb,
            FormationDb formationDb
    ) {
        this.labels = data.getDebugLabels();
        this.edges = algo.getEdgesKeys();
        this.relatedtoHealth = algo.getRelatedToHealth();
        this.domaines = domainesDb.findAll().stream().map(DomaineEntity::getId).collect(Collectors.toSet());
        this.metiers = metiersDb.findAll().stream().map(MetierEntity::getId).collect(Collectors.toSet());
        this.interets = interetsDb.findAll().stream().map(InteretEntity::getId).collect(Collectors.toSet());
        this.formations = formationDb.findAll().stream().map(FormationEntity::getId).collect(Collectors.toSet());

    }

    private String getDebugLabel(String key) {
        return this.labels.getOrDefault(key, key);
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
        return formations.contains(key);
    }

    public void analyze() throws Exception {

        outputFormationsSansDomainesOuMetiers();

        /*
        outputRelatedToHealth();

        outputGraph();

        outputSemanticGraph();

        outputMetiersSansFormations();
        */
    }


    private void outputFormationsSansDomainesOuMetiers() throws IOException {

        Serialisation.toJsonFile("formations_sans_domaines.json",
                edges.edges().entrySet().stream()
                        .filter(e -> isFormation(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(this::isDomaine))
                        .map(e -> getDebugLabel(e.getKey()))
                        .sorted()
                        .toList(),
                true);

        Serialisation.toJsonFile("formations_sans_domaines_ni_metier.json",
                edges.edges().entrySet().stream()
                        .filter(e -> isFormation(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(f -> isDomaine(f) || isMetier(f)))
                        .map(e -> getDebugLabel(e.getKey()))
                        .sorted()
                        .toList(),
                true);

        Serialisation.toJsonFile("metiers_sans_interets_ni_formation.json",
                edges.edges().entrySet().stream()
                        .filter(e -> isMetier(e.getKey()))
                        .filter(e -> e.getValue().stream().noneMatch(f -> isInteret(f) || isFormation(f)))
                        .map(e -> getDebugLabel(e.getKey()))
                        .toList(),
                true);


    }


    private void outputGraph() throws IOException {
        Serialisation.toJsonFile("graph.json", edges.edges(), true);
    }
    private void outputSemanticGraph() throws IOException {
        Edges edgesLabels = new Edges();
        edgesLabels.createLabelledGraphFrom(edges, labels);
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

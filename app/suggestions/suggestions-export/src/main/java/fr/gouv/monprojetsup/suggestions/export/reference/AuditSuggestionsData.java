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
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.domain.Constants.DIAGNOSTICS_OUTPUT_DIR;

@Repository
interface FormationDb extends JpaRepository<FormationEntity, String> {

}

@Slf4j
@Component
public class AuditSuggestionsData {

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
    public AuditSuggestionsData(
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

    public void outputDiagnostics() throws Exception {

        outputFormationDiagnostics();

        outputMetiersDiagnostics();

        /*
        outputRelatedToHealth();

        outputGraph();

        outputSemanticGraph();

        outputMetiersSansFormations();
        */
    }

    private void outputMetiersDiagnostics() throws IOException {

        val metiersedges = edges.edges().entrySet().stream()
                .filter(e -> isMetier(e.getKey()))
                .map( e -> Triple.of(e.getKey(), getDebugLabel(e.getKey()), e.getValue()))
                .sorted(Comparator.comparing(Triple::getMiddle))
                .toList();

        outputMetierDiagnostic(
                metiersedges.stream().filter(e -> e.getRight().stream().noneMatch(this::isInteret)).map(Triple::getMiddle).toList(),
                DIAGNOSTICS_OUTPUT_DIR + "metiers_sans_centres_interets.csv"
                );

        outputMetierDiagnostic(
                metiersedges.stream().filter(e -> e.getRight().stream().noneMatch(this::isFormation)).map(Triple::getMiddle).toList(),
                DIAGNOSTICS_OUTPUT_DIR + "metiers_sans_formation_mps.csv"
        );

        outputMetierDiagnostic(
                metiersedges.stream().filter(e -> e.getRight().stream().noneMatch(this::isDomaine)).map(Triple::getMiddle).toList(),
                DIAGNOSTICS_OUTPUT_DIR + "metiers_sans_domaines.csv"
        );

    }

    private void outputMetierDiagnostic(List<String> list, String fileName) throws IOException {
        try(CsvTools tools = CsvTools.getWriter(fileName)) {
            tools.append(List.of("label"));
            for (String s : list) {
                tools.append(List.of(s));
            }
        }
    }


    private void outputFormationDiagnostics() throws IOException {

        val formationsEdges =
                edges.edges().entrySet().stream()
                .filter(e -> isFormation(e.getKey()))
                .map(e -> Triple.of(e.getKey(), getDebugLabel(e.getKey()), e.getValue()))
                .sorted(Comparator.comparing(Triple::getMiddle))
                .toList();


        exportDiagnostic(formationsEdges, DIAGNOSTICS_OUTPUT_DIR + "formations.csv");

        val sansDomaines = formationsEdges.stream()
                .filter(e -> e.getRight().stream().noneMatch(this::isDomaine))
                .toList();
        exportDiagnostic(sansDomaines, DIAGNOSTICS_OUTPUT_DIR + "formations_sans_domaines.csv");

        val sansMetiers = formationsEdges.stream()
                .filter(e -> e.getRight().stream().noneMatch(this::isMetier))
                .toList();
        exportDiagnostic(sansMetiers, DIAGNOSTICS_OUTPUT_DIR + "formations_sans_metiers.csv");

    }

    private void exportDiagnostic(List<Triple<String, String, Set<String>>> formationsEdges, String filename) throws IOException {


        try (val csvTools = CsvTools.getWriter(filename)) {
            csvTools.appendHeaders(List.of(
                    "remarque",
                    "clé MPS",
                    "intitulé",
                    "formations ideos",
                    "formations psup",
                    "metiers",
                    "domainesWeb",
                    "liens psup",
                    "liens onisep",
                    "liens autre",
                    "capacité accueil"
            ));
            for (val keyLabelEdgesFormation : formationsEdges) {

                val key = keyLabelEdgesFormation.getLeft();
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
                val remarque = new ArrayList<String>();
                if(domainess.isBlank()) remarque.add("Pas de domaine");
                if(metierss.isBlank()) remarque.add("Pas de métiers");
                if(liens.isEmpty()) remarque.add("Pas de liens");
                csvTools.append(
                        List.of(
                                String.join(" - ", remarque),
                                key,
                                labels.get(key),
                                String.join("\n", Objects.requireNonNull(formation.getFormationsIdeo()).stream().map(this::getDebugLabel).toList()),
                                String.join("\n", Objects.requireNonNull(formation.getFormationsAssociees()).stream().map(this::getDebugLabel).toList()),
                                metierss,
                                domainess,
                                liens.stream().filter(s -> s.contains("parcoursup")).collect(Collectors.joining("\n")),
                                liens.stream().filter(s -> s.contains("avenirs")).collect(Collectors.joining("\n")),
                                liens.stream().filter(s -> !s.contains("parcoursup") && !s.contains("avenirs")).collect(Collectors.joining("\n")),
                                Integer.toString(formation.getCapacite())
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

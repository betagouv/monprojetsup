package fr.gouv.monprojetsup.suggestions.export.reference;

import fr.gouv.monprojetsup.data.commun.entity.LienEntity;
import fr.gouv.monprojetsup.data.formation.entity.FormationEntity;
import fr.gouv.monprojetsup.data.metier.entity.MetierEntity;
import fr.gouv.monprojetsup.data.referentiel.entity.DomaineEntity;
import fr.gouv.monprojetsup.data.referentiel.entity.InteretSousCategorieEntity;
import fr.gouv.monprojetsup.data.tools.CsvTools;
import fr.gouv.monprojetsup.suggestions.algo.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import fr.gouv.monprojetsup.suggestions.data.model.Edges;
import fr.gouv.monprojetsup.suggestions.export.DomainesDb;
import fr.gouv.monprojetsup.suggestions.export.InteretsSousCategorieDb;
import fr.gouv.monprojetsup.suggestions.export.MetiersDb;
import fr.gouv.monprojetsup.suggestions.infrastructure.FormationDb;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.Constants.DIAGNOSTICS_OUTPUT_DIR;


@Slf4j
@Component
public class AuditSuggestionsData {

    private final Map<String, String> labels;

    private final Map<String, String> debugLabels;
    private final Edges edges;
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
            InteretsSousCategorieDb interetsDb,
            FormationDb formationDb
    ) {
        this.labels = data.getLabels();
        this.debugLabels = data.getDebugLabels();
        this.edges = algo.getEdgesKeys();
        this.domaines = domainesDb.findAll().stream().map(DomaineEntity::getId).collect(Collectors.toSet());
        this.metiers = metiersDb.findAll().stream().map(MetierEntity::getId).collect(Collectors.toSet());
        this.interets = interetsDb.findAll().stream().map(InteretSousCategorieEntity::getId).collect(Collectors.toSet());
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

        val metiersbackedges = edges.edges().entrySet().stream()
                .flatMap(e -> e.getValue().stream().map(v -> Pair.of(e.getKey(),v)))
                .filter(p -> isMetier(p.getRight()))
                .collect(Collectors.groupingBy(Pair::getRight));

        outputMetierDiagnostic(
                metiersbackedges.entrySet().stream().filter(e -> e.getValue().stream().noneMatch(p -> isInteret(p.getLeft()))).map(Map.Entry::getKey).distinct().toList(),
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


        outputMetierDiagnostic(
                metiersedges.stream().filter(e -> e.getRight().stream().noneMatch(this::isFormation)).map(Triple::getMiddle).toList(),
                DIAGNOSTICS_OUTPUT_DIR + "metiers_sans_formation_mps.csv"
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

        exportDiagnosticFormationsMetiers(formationsEdges, DIAGNOSTICS_OUTPUT_DIR + "formations_metiers.csv");


        val sansDomaines = formationsEdges.stream()
                .filter(e -> e.getRight().stream().noneMatch(this::isDomaine))
                .toList();
        exportDiagnostic(sansDomaines, DIAGNOSTICS_OUTPUT_DIR + "formations_sans_domaines.csv");

        val sansMetiers = formationsEdges.stream()
                .filter(e -> e.getRight().stream().noneMatch(this::isMetier))
                .toList();
        exportDiagnostic(sansMetiers, DIAGNOSTICS_OUTPUT_DIR + "formations_sans_metiers.csv");

        try(CsvTools tools = CsvTools.getWriter(DIAGNOSTICS_OUTPUT_DIR + "diagnostic_global_formations.csv")) {
            tools.append(List.of(
                    "critère qualité données de référence MPS",
                    "nb actuel de formations satisfaisant le critère",
                    "% objectif atteint",
                    "définition objectif"
            ));
            Map<String, Set<String>> domainesParFormations = formationsEdges.stream()
                    .collect(Collectors.toMap(
                            Triple::getLeft,
                            e -> e.getRight().stream().filter(this::isDomaine).collect(Collectors.toSet())
                    ));
            Map<String, Set<String>> metiersParFormations = formationsEdges.stream()
                    .collect(Collectors.toMap(
                            Triple::getLeft,
                            e -> e.getRight().stream().filter(this::isMetier).collect(Collectors.toSet())
                    ));
            val nbMax = 4L;
            val nbFormations = formationsEdges.stream().map(Triple::getLeft).distinct().count();
            val objectifs = Map.of(1L,100,2L, 75, 3L, 50, 4L, 30);
            for(long i = 1; i <= nbMax; i++) {
                val objectifPct = objectifs.get(i);
                long finalI = i;
                val nbFormationOk = domainesParFormations.entrySet().stream()
                        .filter(e -> e.getValue().size() >= finalI)
                        .count();
                val objectifNb = objectifPct * nbFormations / 100;
                val pctObjectif = Math.min(100, (100 * nbFormationOk) / objectifNb);

                tools.append(List.of(
                        "formations avec >= " + i + " domaine(s) associé(s)",
                        "" + nbFormationOk,
                                pctObjectif + "%",
                                objectifNb + " (i.e. " + objectifPct + "% des formations)"
                )
                );
            }
            for(long i = 1; i <= nbMax; i++) {
                val objectifPct = objectifs.get(i);
                long finalI = i;
                val nbFormationOk = metiersParFormations.entrySet().stream()
                        .filter(e -> e.getValue().size() >= finalI)
                        .count();
                val objectifNb = objectifPct * nbFormations / 100;
                val pctObjectif = Math.min(100, (100 * nbFormationOk) / objectifNb);
                tools.append(List.of(
                        "formations avec >= " + i + " métier(s) associé(s)",
                        "" + nbFormationOk,
                        pctObjectif + "%",
                        objectifNb + " (i.e. " + objectifPct + "% des formations)"
                ));
            }
        }

    }


    private void exportDiagnostic(List<Triple<String, String, Set<String>>> formationsEdges, String filename) throws IOException {


        try (val csvTools = CsvTools.getWriter(filename)) {
            csvTools.appendHeaders(List.of(
                    "remarque",
                    "nb métiers",
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
                val listeMetiers = keyLabelEdgesFormation.getRight().stream()
                        .filter(this::isMetier)
                        .map(this::getDebugLabel)
                        .sorted().toList();
                val metierss = String.join("\n", listeMetiers);
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
                                "" + listeMetiers.size(),
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

    private void exportDiagnosticFormationsMetiers(List<Triple<String, String, Set<String>>> formationsEdges, String s) throws IOException {
        try(CsvTools tools = CsvTools.getWriter(DIAGNOSTICS_OUTPUT_DIR + "formations_metiers.csv")) {
            tools.appendHeaders(List.of(
                    "id formation MPS",
                    "nom formation MPS",
                    "id metier IDEO",
                    "nom metier IDEO",
                    "ids formations IDEO",
                    "noms formations IDEO",
                    "capacite accueil"
            ));
            val formationsEdgesSorted =
                    formationsEdges.stream()
                    .sorted(Comparator.comparing(t -> {
                        val idFormationMps = t.getLeft();
                        val formation = this.formations.get(idFormationMps);
                        if(formation == null) return 0;
                        return -formation.getCapacite();
                    }))
                    .toList();

            for (val keyLabelEdgesFormation : formationsEdgesSorted) {
                val idFormationMps = keyLabelEdgesFormation.getLeft();
                val nomFormationMps = labels.get(idFormationMps);

                val formation = this.formations.get(idFormationMps);

                val idsFormationsIdeo = Objects.requireNonNull(formation.getFormationsIdeo());
                val nomsFormationsIdeo =
                        String.join("\n", idsFormationsIdeo.stream()
                                .map(this::getDebugLabel)
                                .toList());

                val listeMetiers = keyLabelEdgesFormation.getRight().stream()
                        .filter(this::isMetier)
                        .sorted().toList();

                boolean first = true;
                for (String idMetier : listeMetiers) {
                    tools.append(
                            List.of(
                                    first ? idFormationMps : "",
                                    first ? nomFormationMps : "",
                                    idMetier,
                                    labels.get(idMetier),
                                    first ? String.join(" ; ", idsFormationsIdeo) : "",
                                    first ? nomsFormationsIdeo : "",
                                    first ? ("" + formation.getCapacite()) : ""
                            )
                    );
                    first = false;
                }
/*                        List.of(
                                keyLabelEdgesFormation.getLeft(),
                                keyLabelEdgesFormation.getMiddle(),
                                "" + listeMetiers.size(),
                                metierss
                        ));*/
            }
        }

    }

}
